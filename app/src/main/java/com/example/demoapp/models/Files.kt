package com.example.demoapp.models


import com.google.gson.annotations.SerializedName

data class Files(
    @SerializedName("zip") val zip: Zip
)