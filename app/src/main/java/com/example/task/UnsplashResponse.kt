package com.example.task

import com.google.gson.annotations.SerializedName

data class UnsplashResponse(
    @SerializedName("id") val id: String,
    @SerializedName("urls") val urls: Urls
)

data class Urls(
    @SerializedName("small") val small: String
)
