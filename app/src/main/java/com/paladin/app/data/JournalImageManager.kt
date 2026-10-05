package com.paladin.app.data

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

object JournalImageManager {

    private const val JOURNAL_IMAGES_DIR = "journal_images"

    private fun getJournalDir(context: Context): File {
        val dir = File(context.filesDir, JOURNAL_IMAGES_DIR)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir
    }

    /**
     * Optimizes and saves an image from sourceUri (e.g. from PhotoPicker / FilePicker)
     * into the local app internal storage directory for journal images.
     * High resolution (max 2560px, JPEG 85%) is maintained so photographed A4 text remains sharp and readable.
     * Returns the absolute path of the newly saved file, or null on failure.
     */
    fun saveImageLocally(context: Context, sourceUri: Uri): String? {
        return try {
            val dir = getJournalDir(context)
            val fileName = "journal_${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(8)}.jpg"
            val destFile = File(dir, fileName)

            val success = ImageOptimizationUtil.optimizeAndSave(context, sourceUri, destFile)
            if (success) destFile.absolutePath else null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Deletes a local image file from app storage if it exists.
     */
    fun deleteImageLocally(filePath: String): Boolean {
        return try {
            val file = File(filePath)
            if (file.exists()) {
                file.delete()
            } else {
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
