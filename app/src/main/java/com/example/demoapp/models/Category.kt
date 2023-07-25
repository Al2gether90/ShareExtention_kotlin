package com.example.demoapp.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Category(
    @SerializedName("categories") var categories: HashMap<String, Category>? = null,
    @SerializedName("categoryId") var categoryId: String? = null,
    @SerializedName("creationDate") var creationDate: String? = null,
    @SerializedName("deleted") var deleted: Boolean? = null,
    @SerializedName("level") var level: Int? = null,
    @SerializedName("order") var order: Int? = null,
    @SerializedName("os") var os: String? = null,
    @SerializedName("owner") var owner: String? = null,
    @SerializedName("parent") var parent: String? = null,
    @SerializedName("title") var title: String? = null,
    // for structure model
    var nodeViewId: String? = null,
    // for category wise photo listing
    var photos: ArrayList<Image>? = null
) : Parcelable
