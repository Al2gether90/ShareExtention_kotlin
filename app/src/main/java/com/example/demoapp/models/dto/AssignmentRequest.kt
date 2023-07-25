package com.example.demoapp.models.dto


import com.google.gson.annotations.SerializedName

data class AssignmentRequest(
    @SerializedName("apiToken") var apiToken: String? = null,
    @SerializedName("email") var email: String? = null
)