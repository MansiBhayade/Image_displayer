package com.example.task

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {

    private lateinit var imageAdapter: ImageAdapter
    private val clientId = "dQQXzwDgK5gqlpAU6qR13nz1ah5C15BLJmkG5_v99nM"
    private var page = 1
    private var isLoading = false
    private var job: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageAdapter = ImageAdapter(this, mutableListOf())
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 3)
        recyclerView.adapter = imageAdapter

        fetchImages(page)

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as GridLayoutManager
                val totalItemCount = layoutManager.itemCount
                val lastVisibleItem = layoutManager.findLastVisibleItemPosition()

                if (!isLoading && totalItemCount <= (lastVisibleItem + 5)) {
                    page++
                    fetchImages(page)
                }
            }
        })
    }

    private fun fetchImages(page: Int) {
        isLoading = true
        job?.cancel()
        job = CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitInstance.api.getPhotos(clientId, page)
                val imageUrls = response.map { it.urls.small }

                withContext(Dispatchers.Main) {
                    imageAdapter.updateData(imageAdapter.imageUrls + imageUrls)
                    isLoading = false
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    isLoading = false
                }
            }
        }
    }
}
