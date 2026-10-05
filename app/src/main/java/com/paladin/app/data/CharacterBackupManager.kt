package com.paladin.app.data

import android.content.Context
import android.net.Uri
import com.paladin.app.model.CharacterSheet
import kotlinx.serialization.json.Json
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.util.UUID
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

/**
 * Manages full backup archives (.paladin / .zip) containing:
 * - character.json (CharacterSheet with relative image paths)
 * - images/ (compressed photo files)
 */
object CharacterBackupManager {

    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    private const val MAX_IMAGE_DIMENSION = 1920
    private const val JPEG_QUALITY = 82

    /**
     * Exports the character and all referenced images into a compressed .paladin ZIP archive
     * directly into the given OutputStream (e.g. from context.contentResolver.openOutputStream(targetUri)).
     * Returns the number of images bundled.
     */
    fun exportBackupArchive(
        context: Context,
        outputStream: OutputStream,
        character: CharacterSheet
    ): Result<Int> = runCatching {
        var exportedImagesCount = 0

        // Map original local file path to relative archive path (e.g. "images/img_1.jpg")
        val imageMapping = mutableMapOf<String, String>()

        // 1. Collect all local images (Journal photos + optional custom profile/full images if local)
        character.journalEntries.forEach { entry ->
            entry.imagePaths.forEach { localPath ->
                if (localPath.isNotBlank() && File(localPath).exists() && !imageMapping.containsKey(localPath)) {
                    val archiveName = "images/journal_${UUID.randomUUID().toString().take(8)}.jpg"
                    imageMapping[localPath] = archiveName
                }
            }
        }

        // Custom Profile Image (if local file)
        character.customProfileImagePath?.let { path ->
            val file = if (path.startsWith("file://")) File(Uri.parse(path).path ?: "") else File(path)
            if (file.exists() && !imageMapping.containsKey(file.absolutePath)) {
                val archiveName = "images/profile_${UUID.randomUUID().toString().take(8)}.jpg"
                imageMapping[file.absolutePath] = archiveName
            }
        }

        // Custom Full Image (if local file)
        character.customFullImagePath?.let { path ->
            val file = if (path.startsWith("file://")) File(Uri.parse(path).path ?: "") else File(path)
            if (file.exists() && !imageMapping.containsKey(file.absolutePath)) {
                val archiveName = "images/full_${UUID.randomUUID().toString().take(8)}.jpg"
                imageMapping[file.absolutePath] = archiveName
            }
        }

        // 2. Rewrite CharacterSheet with relative archive paths
        val rewrittenJournalEntries = character.journalEntries.map { entry ->
            val rewrittenImages = entry.imagePaths.map { path ->
                imageMapping[path] ?: path
            }
            entry.copy(imagePaths = rewrittenImages)
        }

        val rewrittenProfilePath = character.customProfileImagePath?.let { path ->
            val file = if (path.startsWith("file://")) File(Uri.parse(path).path ?: "") else File(path)
            imageMapping[file.absolutePath] ?: path
        }

        val rewrittenFullPath = character.customFullImagePath?.let { path ->
            val file = if (path.startsWith("file://")) File(Uri.parse(path).path ?: "") else File(path)
            imageMapping[file.absolutePath] ?: path
        }

        val portableCharacter = character.copy(
            journalEntries = rewrittenJournalEntries,
            customProfileImagePath = rewrittenProfilePath,
            customFullImagePath = rewrittenFullPath
        )

        val characterJsonString = json.encodeToString(portableCharacter)

        // 3. Write ZIP stream
        ZipOutputStream(BufferedOutputStream(outputStream)).use { zipOut ->
            // Entry A: character.json
            val jsonEntry = ZipEntry("character.json")
            zipOut.putNextEntry(jsonEntry)
            zipOut.write(characterJsonString.toByteArray(Charsets.UTF_8))
            zipOut.closeEntry()

            // Entry B: compressed images
            for ((originalPath, archivePath) in imageMapping) {
                val sourceFile = File(originalPath)
                if (sourceFile.exists()) {
                    val imageEntry = ZipEntry(archivePath)
                    zipOut.putNextEntry(imageEntry)
                    compressImageToStream(sourceFile, zipOut)
                    zipOut.closeEntry()
                    exportedImagesCount++
                }
            }
        }

        exportedImagesCount
    }

    /**
     * Creates a temporary .paladin archive file in cacheDir/backups/ for sharing via Intent.
     */
    fun createShareableBackupFile(
        context: Context,
        character: CharacterSheet
    ): Result<File> = runCatching {
        val backupDir = File(context.cacheDir, "backups").apply { if (!exists()) mkdirs() }
        val sanitizedName = character.name.replace(Regex("[^a-zA-Z0-9_-]"), "_").ifBlank { "Paladin" }
        val fileName = "${sanitizedName}_Stufe${character.level}_Backup.paladin"
        val targetFile = File(backupDir, fileName)

        FileOutputStream(targetFile).use { fos ->
            exportBackupArchive(context, fos, character).getOrThrow()
        }

        targetFile
    }

    /**
     * Imports a backup from an InputStream (e.g. from an opened .paladin / .zip file).
     * Extracts images into context.filesDir/journal_images/, adjusts paths to local absolute paths,
     * and returns the restored CharacterSheet.
     */
    fun importBackupArchive(
        context: Context,
        inputStream: InputStream
    ): Result<CharacterSheet> = runCatching {
        val destJournalDir = File(context.filesDir, "journal_images").apply {
            if (!exists()) mkdirs()
        }

        var characterJsonContent: String? = null
        val extractedImageMap = mutableMapOf<String, String>() // relative archive path -> new local absolute path

        ZipInputStream(BufferedInputStream(inputStream)).use { zipIn ->
            var entry: ZipEntry? = zipIn.nextEntry
            while (entry != null) {
                val entryName = entry.name.replace("\\", "/")

                if (entryName == "character.json" || entryName.endsWith("/character.json")) {
                    characterJsonContent = zipIn.bufferedReader(Charsets.UTF_8).readText()
                } else if (entryName.startsWith("images/") && !entry.isDirectory) {
                    val rawTempFile = File(context.cacheDir, "import_temp_${System.currentTimeMillis()}.tmp")
                    FileOutputStream(rawTempFile).use { fileOut ->
                        zipIn.copyTo(fileOut)
                    }

                    val fileName = "imported_${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(8)}.jpg"
                    val destFile = File(destJournalDir, fileName)

                    // Optimize during import (ensures high quality text readability while shedding redundant MBs)
                    val optimized = ImageOptimizationUtil.optimizeFileToFile(rawTempFile, destFile)
                    if (!optimized) {
                        rawTempFile.copyTo(destFile, overwrite = true)
                    }
                    rawTempFile.delete()

                    extractedImageMap[entryName] = destFile.absolutePath
                }

                zipIn.closeEntry()
                entry = zipIn.nextEntry
            }
        }

        val jsonStr = characterJsonContent ?: throw IllegalArgumentException("Ungültige Backup-Datei: 'character.json' nicht im Archiv gefunden!")
        val importedSheet = json.decodeFromString<CharacterSheet>(jsonStr)

        // Restore image paths to new local storage locations
        val restoredJournalEntries = importedSheet.journalEntries.map { entry ->
            val restoredPaths = entry.imagePaths.map { relativeOrOldPath ->
                extractedImageMap[relativeOrOldPath] ?: relativeOrOldPath
            }
            entry.copy(imagePaths = restoredPaths)
        }

        val restoredProfilePath = importedSheet.customProfileImagePath?.let { path ->
            extractedImageMap[path] ?: path
        }

        val restoredFullPath = importedSheet.customFullImagePath?.let { path ->
            extractedImageMap[path] ?: path
        }

        importedSheet.copy(
            journalEntries = restoredJournalEntries,
            customProfileImagePath = restoredProfilePath,
            customFullImagePath = restoredFullPath
        )
    }

    /**
     * Delegates to ImageOptimizationUtil.compressFileToStream
     */
    private fun compressImageToStream(file: File, out: OutputStream) {
        ImageOptimizationUtil.compressFileToStream(file, out)
    }
}
