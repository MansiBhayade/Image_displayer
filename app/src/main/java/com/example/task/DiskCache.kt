package com.example.task

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

object DiskCache {

    private const val CACHE_DIR = "image_cache"

    fun saveBitmapToDisk(context: Context, key: String, bitmap: Bitmap) {
        val cacheDir = File(context.cacheDir, CACHE_DIR)
        if (!cacheDir.exists()) {
            cacheDir.mkdirs()
        }
        val file = File(cacheDir, key)
        FileOutputStream(file).use { fos ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
        }
    }

    fun getBitmapFromDisk(context: Context, key: String): Bitmap? {
        val cacheDir = File(context.cacheDir, CACHE_DIR)
        val file = File(cacheDir, key)
        if (!file.exists()) return null
        return FileInputStream(file).use { fis ->
            BitmapFactory.decodeStream(fis)
        }
    }
}
