package com.example.demoapp.models

import com.google.gson.annotations.SerializedName

data class InspectionType(
    @SerializedName("id") val id: String? = null,
    @SerializedName("value") val value: String? = null
)