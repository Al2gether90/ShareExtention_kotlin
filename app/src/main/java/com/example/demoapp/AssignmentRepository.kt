package com.example.demoapp

import com.example.demoapp.models.*
import com.example.demoapp.models.dto.CreateAssignmentRequest
import retrofit2.Response
import java.io.File

class AssignmentRepository(private var dataSource: AssignmentDataSource) {

    suspend fun getAssignments(): Response<ArrayList<Assignment>> {
        return dataSource.getAssignments()
    }

    suspend fun upload(
        profileId: String?,
        categoryId: String?,
        assignmentId: String?,
        file: File?
    ): Response<BaseResponse> {
        return dataSource.upload(profileId, categoryId, assignmentId, file)
    }

    suspend fun createAssignmentByEmail(createAssignmentRequest: CreateAssignmentRequest): Response<CreateAssignmentResponse> {
        return dataSource.createAssignmentByEmail(createAssignmentRequest)
    }

    suspend fun getInspectionType(): Response<ArrayList<InspectionType>> {
        return dataSource.getInspectionType()
    }

    suspend fun getShortLink(
        url: String?,
        profileId: String,
        assignmentId: String?
    ): Response<ShortLinkResponse> {
        return dataSource.getShortLink(url, profileId, assignmentId)
    }
}