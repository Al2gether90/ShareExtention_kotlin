package com.example.demoapp.models

import com.google.gson.annotations.SerializedName

open class BaseResponse(

    @SerializedName("msg")
    val message: String? = null,

    @SerializedName("res")
    val isSuccessful: Boolean = false,

)
