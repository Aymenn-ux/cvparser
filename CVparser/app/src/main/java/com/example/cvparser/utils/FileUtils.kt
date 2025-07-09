package com.example.cvparser.utils

import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import java.io.File
import java.io.FileOutputStream

object FileUtils {
    fun getFileSize(size: Long): String {
        if (size <= 0) return "0 Bytes"
        val units = arrayOf("Bytes", "KB", "MB", "GB")
        val digitGroups = (Math.log10(size.toDouble()) / Math.log10(1024.0)).toInt()
        return String.format("%.1f %s", size / Math.pow(1024.0, digitGroups.toDouble()), units[digitGroups])
    }

    fun getFileFromUri(context: Context, uri: Uri): File? {
        val contentResolver = context.contentResolver
        val extension = MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(contentResolver.getType(uri)) ?: "pdf"

        val tempFile = File.createTempFile("temp_", ".$extension", context.cacheDir)
        contentResolver.openInputStream(uri)?.use { input ->
            FileOutputStream(tempFile).use { output ->
                input.copyTo(output)
            }
        }
        return tempFile
    }
}