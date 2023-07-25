package com.example.demoapp.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Structure(
    @SerializedName("categories") var categories: HashMap<String, Category>? = null,
    @SerializedName("title") var title: String? = null
) : Parcelable
