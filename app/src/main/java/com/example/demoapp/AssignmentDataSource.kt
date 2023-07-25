package com.example.demoapp

import com.example.demoapp.models.*
import com.example.demoapp.models.dto.AssignmentRequest
import com.example.demoapp.models.dto.CreateAssignmentRequest
import com.example.demoapp.network.RetrofitClient
import com.example.demoapp.utils.EMAIL
import com.example.demoapp.utils.TOKEN
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import java.io.File
import com.example.demoapp.utils.MEDIA_TYPE_IMAGE as MEDIA_TYPE_IMAGE1

class AssignmentDataSource {

    suspend fun getAssignments(): Response<ArrayList<Assignment>> {
        return RetrofitClient.instance.getAssignments(AssignmentRequest().apply {
            email = EMAIL
            apiToken = TOKEN
        })
    }

    suspend fun upload(
        profileId: String?,
        categoryId: String?,
        assignmentId: String?,
        file: File?
    ): Response<BaseResponse> {
        var image: MultipartBody.Part? = null
        file?.let {
            val requestBody = file.asRequestBody(MEDIA_TYPE_IMAGE1.toMediaTypeOrNull())
            image = MultipartBody.Part.createFormData(
                "singleFile", file.name, requestBody
            )
        }
        return RetrofitClient.instance.upload(
            profileId?.toRequestBody(),
            categoryId?.toRequestBody(),
            assignmentId?.toRequestBody(),
            image,
        )
    }

    suspend fun createAssignmentByEmail(createAssignmentRequest: CreateAssignmentRequest): Response<CreateAssignmentResponse> {
        return RetrofitClient.instance.createAssignmentByEmail(createAssignmentRequest)
    }

    suspend fun getInspectionType(): Response<ArrayList<InspectionType>> {
        return RetrofitClient.instance.getInspectionType()
    }

    suspend fun getShortLink(
        url: String?,
        profileId: String,
        assignmentId: String?
    ): Response<ShortLinkResponse> {
        return RetrofitClient.instance.getShortLink(url, profileId, assignmentId)
    }
}