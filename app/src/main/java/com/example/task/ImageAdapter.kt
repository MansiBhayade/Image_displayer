package com.example.task

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import androidx.collection.LruCache

class ImageAdapter(
    private val context: Context,
     val imageUrls: MutableList<String>
) : RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    private val memoryCache: LruCache<String, Bitmap> = LruCache(50)

    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.grid_item, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val imageUrl = imageUrls[position]
        loadImage(imageUrl, holder.imageView)
    }

    override fun getItemCount(): Int {
        return imageUrls.size
    }

    fun updateData(newImageUrls: List<String>) {
        imageUrls.clear()
        imageUrls.addAll(newImageUrls)
        notifyDataSetChanged()
    }

    private fun loadImage(imageUrl: String, imageView: ImageView) {
        val cacheKey = imageUrl.hashCode().toString()
        memoryCache.get(cacheKey)?.let {
            imageView.setImageBitmap(it)
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            var bitmap = DiskCache.getBitmapFromDisk(context, cacheKey)
            if (bitmap == null) {
                bitmap = downloadBitmap(imageUrl)
                if (bitmap != null) {
                    DiskCache.saveBitmapToDisk(context, cacheKey, bitmap)
                }
            }
            bitmap?.let {
                memoryCache.put(cacheKey, it)
                withContext(Dispatchers.Main) {
                    imageView.setImageBitmap(it)
                }
            }
        }
    }

    private fun downloadBitmap(imageUrl: String): Bitmap? {
        return try {
            val url = URL(imageUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input = connection.inputStream
            BitmapFactory.decodeStream(input)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
}


