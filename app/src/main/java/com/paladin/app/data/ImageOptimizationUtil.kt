package com.paladin.app.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import java.io.*

/**
 * Utility for intelligent image optimization that preserves high text readability
 * (e.g. for photographed A4 pages, character sheets, rulebook excerpts)
 * while drastically reducing file size (e.g. 10 MB -> ~800 KB).
 */
object ImageOptimizationUtil {

    // 2560px max dimension ensures A4 documents at ~220 DPI (easily readable down to 8pt font)
    const val MAX_IMAGE_DIMENSION = 2560
    const val JPEG_QUALITY = 85

    /**
     * Reads an image from source Uri or InputStream, optimizes it (auto-rotate, scale down to max 2560px, JPEG 85%),
     * and saves it to the target file.
     */
    fun optimizeAndSave(context: Context, sourceUri: Uri, targetFile: File): Boolean {
        return try {
            // Write input stream to a temp file first to inspect dimensions and EXIF
            val tempFile = File(context.cacheDir, "temp_proc_${System.currentTimeMillis()}.tmp")
            context.contentResolver.openInputStream(sourceUri)?.use { input ->
                FileOutputStream(tempFile).use { output ->
                    input.copyTo(output)
                }
            } ?: return false

            val success = optimizeFileToFile(tempFile, targetFile)
            tempFile.delete()
            success
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Optimizes a source file and writes the compressed result to targetFile.
     */
    fun optimizeFileToFile(sourceFile: File, targetFile: File): Boolean {
        return try {
            FileOutputStream(targetFile).use { fos ->
                compressFileToStream(sourceFile, fos)
            }
            targetFile.exists() && targetFile.length() > 0
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Compresses and scales an image file directly into an OutputStream.
     */
    fun compressFileToStream(file: File, out: OutputStream) {
        try {
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            BitmapFactory.decodeFile(file.absolutePath, options)

            val width = options.outWidth
            val height = options.outHeight

            if (width <= 0 || height <= 0) {
                // Not a decodable image, copy raw
                file.inputStream().use { it.copyTo(out) }
                return
            }

            var inSampleSize = 1
            val maxDim = maxOf(width, height)
            if (maxDim > MAX_IMAGE_DIMENSION) {
                while ((maxDim / (inSampleSize * 2)) >= MAX_IMAGE_DIMENSION) {
                    inSampleSize *= 2
                }
            }

            val decodeOptions = BitmapFactory.Options().apply {
                this.inSampleSize = inSampleSize
                inPreferredConfig = Bitmap.Config.ARGB_8888 // High quality for sharp text
            }

            var bitmap = BitmapFactory.decodeFile(file.absolutePath, decodeOptions)

            if (bitmap != null) {
                // Secondary precise downscaling if still larger than MAX_IMAGE_DIMENSION
                val currentMax = maxOf(bitmap.width, bitmap.height)
                if (currentMax > MAX_IMAGE_DIMENSION) {
                    val scaleFactor = MAX_IMAGE_DIMENSION.toFloat() / currentMax.toFloat()
                    val targetWidth = (bitmap.width * scaleFactor).toInt()
                    val targetHeight = (bitmap.height * scaleFactor).toInt()
                    val scaled = Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, true)
                    if (scaled != bitmap) {
                        bitmap.recycle()
                        bitmap = scaled
                    }
                }

                // Check orientation from EXIF
                try {
                    val exif = ExifInterface(file.absolutePath)
                    val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
                    val matrix = Matrix()
                    when (orientation) {
                        ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
                        ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
                        ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
                    }
                    if (!matrix.isIdentity) {
                        val rotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
                        if (rotated != bitmap) {
                            bitmap.recycle()
                            bitmap = rotated
                        }
                    }
                } catch (e: Exception) {
                    // Ignore EXIF failure
                }

                bitmap.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, out)
                bitmap.recycle()
            } else {
                file.inputStream().use { it.copyTo(out) }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            file.inputStream().use { it.copyTo(out) }
        }
    }
}
