package com.example.demoapp.models

import com.google.gson.annotations.SerializedName

data class CreateAssignmentResponse(
    @SerializedName("assignmentId") val assignmentId: String?,
    @SerializedName("assignment") val assignment: Assignment?
)