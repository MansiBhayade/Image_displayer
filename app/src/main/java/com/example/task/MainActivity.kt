package com.example.task

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var imageAdapter: ImageAdapter
    private val clientId = "dQQXzwDgK5gqlpAU6qR13nz1ah5C15BLJmkG5_v99nM"
    private var currentPage = 1
    private var isLoading = false
    private var currentJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageAdapter = ImageAdapter(this, mutableListOf())
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 3)
        recyclerView.adapter = imageAdapter

        fetchImages(currentPage)
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as GridLayoutManager
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                if (!isLoading && visibleItemCount + firstVisibleItemPosition >= totalItemCount && firstVisibleItemPosition >= 0) {
                    fetchImages(currentPage + 1)
                }
            }
        })
    }

    private fun fetchImages(page: Int) {
        isLoading = true
        currentJob?.cancel()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitInstance.api.getPhotos(clientId, page)
                val imageUrls = response.map { it.urls.small }

                // Log all the fetched image URLs
                imageUrls.forEach { imageUrl ->
                    Log.d("ImageFetch", "Fetched URL: $imageUrl") }
                withContext(Dispatchers.Main) {
                    imageAdapter.updateData(imageUrls)
                    currentPage = page
                    isLoading = false
                }
            } catch (e: Exception) {
                e.printStackTrace()
                isLoading = false
            }
        }
    }
}
