package com.example.demoapp.models.dto

import android.os.Parcelable
import com.example.demoapp.models.Assignment
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class CreateAssignmentRequest(
    @SerializedName("assignment") val assignment: Assignment?,
    @SerializedName("email") val email: String?,
) : Parcelable