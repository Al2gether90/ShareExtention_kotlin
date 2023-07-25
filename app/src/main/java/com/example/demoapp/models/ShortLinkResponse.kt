package com.example.demoapp.models

import com.google.gson.annotations.SerializedName

data class ShortLinkResponse(
    @SerializedName("previewLink") val previewLink: String? = null,
    @SerializedName("shortLink") val shortLink: String? = null,
    @SerializedName("success") val success: Boolean? = null,
    @SerializedName("urlToShorten") val urlToShorten: String? = null
)