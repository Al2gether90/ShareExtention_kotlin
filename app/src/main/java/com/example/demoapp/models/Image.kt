package com.example.demoapp.models

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Image(
    var isSelected: Boolean = false,
    var uri: Uri? = null,
    var categoryId: String? = null
) : Parcelable
