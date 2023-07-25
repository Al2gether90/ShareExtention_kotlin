package com.example.demoapp.network

import com.example.demoapp.models.*
import com.example.demoapp.models.dto.AssignmentRequest
import com.example.demoapp.models.dto.CreateAssignmentRequest
import com.example.demoapp.utils.TOKEN
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface API {

    @POST("api/getAssignments")
    suspend fun getAssignments(@Body assignmentRequest: AssignmentRequest): Response<ArrayList<Assignment>>

    @Multipart
    @POST("v2/upload")
    suspend fun upload(
        @Part("profileID") profileId: RequestBody?,
        @Part("assignmentID") assignmentId: RequestBody?,
        @Part("categoryID") categoryId: RequestBody?,
        @Part image: MultipartBody.Part?
    ): Response<BaseResponse>

    @POST("api/sf/createAssignmentByEmail")
    suspend fun createAssignmentByEmail(
        @Body createAssignmentRequest: CreateAssignmentRequest,
        @Header("apiToken") apiToken: String = TOKEN
    ): Response<CreateAssignmentResponse>

    @GET("api/sf/getDamageType")
    suspend fun getInspectionType(): Response<ArrayList<InspectionType>>

    @GET("api/shortenSimpleFileManagerUrl")
    suspend fun getShortLink(
        @Query("urlToShorten") url: String?,
        @Query("profileId") profileId: String,
        @Query("assignmentId") assignmentId: String?
    ): Response<ShortLinkResponse>
}