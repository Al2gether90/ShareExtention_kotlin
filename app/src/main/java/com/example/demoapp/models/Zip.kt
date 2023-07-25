package com.example.demoapp.models


import com.google.gson.annotations.SerializedName

data class Zip(
    @SerializedName("errorMessage") val errorMessage: String,
    @SerializedName("fileStatus") val fileStatus: String,
    @SerializedName("generationDate") val generationDate: Long,
    @SerializedName("lastGeneratedUrl") val lastGeneratedUrl: String,
    @SerializedName("supportInformed") val supportInformed: String
)