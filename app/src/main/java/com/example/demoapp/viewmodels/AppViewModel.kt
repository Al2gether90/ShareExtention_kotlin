package com.example.demoapp.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.demoapp.AssignmentDataSource
import com.example.demoapp.AssignmentRepository
import com.example.demoapp.DemoApp
import com.example.demoapp.R
import com.example.demoapp.models.*
import com.example.demoapp.models.dto.CreateAssignmentRequest
import com.example.demoapp.network.Event
import com.example.demoapp.network.Resource
import com.example.demoapp.utils.hasInternetConnection
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.File
import java.io.IOException

class AppViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AssignmentRepository(AssignmentDataSource())

    var selectedImages = arrayListOf<String>()

    private val _assignmentResponse =
        MutableLiveData<Event<Resource<ArrayList<Assignment>>>>()
    val assignmentResponse: LiveData<Event<Resource<ArrayList<Assignment>>>> =
        _assignmentResponse

    fun getAssignments() = viewModelScope.launch {

        _assignmentResponse.postValue(Event(Resource.Loading()))

        try {
            if (getApplication<DemoApp>().hasInternetConnection()) {
                val response = repository.getAssignments()
                _assignmentResponse.postValue(handleResponse(response))
            } else {
                _assignmentResponse.postValue(
                    Event(
                        Resource.Error(
                            getApplication<DemoApp>().getString(
                                R.string.no_internet_connection
                            )
                        )
                    )
                )
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> {
                    _assignmentResponse.postValue(
                        Event(
                            Resource.Error(
                                getApplication<DemoApp>().getString(
                                    R.string.network_failure
                                )
                            )
                        )
                    )
                }
                else -> {
                    Log.e("PARSING", "getAssignments: ${t.localizedMessage}")
                    _assignmentResponse.postValue(
                        Event(
                            Resource.Error(
                                getApplication<DemoApp>().getString(
                                    R.string.conversion_error
                                )
                            )
                        )
                    )
                }
            }
        }
    }

    private fun handleResponse(response: Response<ArrayList<Assignment>>): Event<Resource<ArrayList<Assignment>>> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Event(Resource.Success(resultResponse))
            }
        }
        return Event(Resource.Error(response.message()))
    }

    private val _uploadResponse =
        MutableLiveData<Event<Resource<BaseResponse>>>()
    val uploadResponse: LiveData<Event<Resource<BaseResponse>>> =
        _uploadResponse

    fun upload(
        profileId: String?,
        categoryId: String?,
        assignmentId: String?,
        file: File?
    ) = viewModelScope.launch {

        _uploadResponse.postValue(Event(Resource.Loading()))

        try {
            if (getApplication<DemoApp>().hasInternetConnection()) {
                val response = repository.upload(profileId, categoryId, assignmentId, file)
                _uploadResponse.postValue(handleUploadResponse(response))
            } else {
                _uploadResponse.postValue(
                    Event(
                        Resource.Error(
                            getApplication<DemoApp>().getString(
                                R.string.no_internet_connection
                            )
                        )
                    )
                )
            }
        } catch (t: Throwable) {
            Log.d("UPLOAD", "Error -> ${t.localizedMessage}")
            when (t) {
                is IOException -> {
                    _uploadResponse.postValue(
                        Event(
                            Resource.Error(
                                getApplication<DemoApp>().getString(
                                    R.string.network_failure
                                )
                            )
                        )
                    )
                }
                else -> {
                    _uploadResponse.postValue(
                        Event(
                            Resource.Error(
                                getApplication<DemoApp>().getString(
                                    R.string.conversion_error
                                )
                            )
                        )
                    )
                }
            }
        }
    }

    private fun handleUploadResponse(response: Response<BaseResponse>): Event<Resource<BaseResponse>> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Event(Resource.Success(resultResponse))
            }
        }
        return Event(Resource.Error(response.message()))
    }

    private val _createAssignmentResponse =
        MutableLiveData<Event<Resource<CreateAssignmentResponse>>>()
    val createAssignmentResponse: LiveData<Event<Resource<CreateAssignmentResponse>>> =
        _createAssignmentResponse

    fun createAssignmentByEmail(createAssignmentRequest: CreateAssignmentRequest) =
        viewModelScope.launch {

            _createAssignmentResponse.postValue(Event(Resource.Loading()))

            try {
                if (getApplication<DemoApp>().hasInternetConnection()) {
                    val response = repository.createAssignmentByEmail(createAssignmentRequest)
                    _createAssignmentResponse.postValue(handleCreateAssignmentResponse(response))
                } else {
                    _createAssignmentResponse.postValue(
                        Event(
                            Resource.Error(
                                getApplication<DemoApp>().getString(
                                    R.string.no_internet_connection
                                )
                            )
                        )
                    )
                }
            } catch (t: Throwable) {
                Log.d("UPLOAD", "Error -> ${t.localizedMessage}")
                when (t) {
                    is IOException -> {
                        _createAssignmentResponse.postValue(
                            Event(
                                Resource.Error(
                                    getApplication<DemoApp>().getString(
                                        R.string.network_failure
                                    )
                                )
                            )
                        )
                    }
                    else -> {
                        _createAssignmentResponse.postValue(
                            Event(
                                Resource.Error(
                                    getApplication<DemoApp>().getString(
                                        R.string.conversion_error
                                    )
                                )
                            )
                        )
                    }
                }
            }
        }

    private fun handleCreateAssignmentResponse(response: Response<CreateAssignmentResponse>): Event<Resource<CreateAssignmentResponse>> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Event(Resource.Success(resultResponse))
            }
        }
        return Event(Resource.Error(response.message()))
    }

    private val _inspectionTypeResponse =
        MutableLiveData<Event<Resource<ArrayList<InspectionType>>>>()
    val inspectionTypeResponse: LiveData<Event<Resource<ArrayList<InspectionType>>>> =
        _inspectionTypeResponse

    fun getInspectionType() = viewModelScope.launch {

        _createAssignmentResponse.postValue(Event(Resource.Loading()))

        try {
            if (getApplication<DemoApp>().hasInternetConnection()) {
                val response = repository.getInspectionType()
                _inspectionTypeResponse.postValue(handleInspectionTypeResponse(response))
            } else {
                _createAssignmentResponse.postValue(
                    Event(
                        Resource.Error(
                            getApplication<DemoApp>().getString(
                                R.string.no_internet_connection
                            )
                        )
                    )
                )
            }
        } catch (t: Throwable) {
            Log.d("UPLOAD", "Error -> ${t.localizedMessage}")
            when (t) {
                is IOException -> {
                    _createAssignmentResponse.postValue(
                        Event(
                            Resource.Error(
                                getApplication<DemoApp>().getString(
                                    R.string.network_failure
                                )
                            )
                        )
                    )
                }
                else -> {
                    _createAssignmentResponse.postValue(
                        Event(
                            Resource.Error(
                                getApplication<DemoApp>().getString(
                                    R.string.conversion_error
                                )
                            )
                        )
                    )
                }
            }
        }
    }

    private fun handleInspectionTypeResponse(response: Response<ArrayList<InspectionType>>): Event<Resource<ArrayList<InspectionType>>> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Event(Resource.Success(resultResponse))
            }
        }
        return Event(Resource.Error(response.message()))
    }

    private val _shortUrlResponse =
        MutableLiveData<Event<Resource<ShortLinkResponse>>>()
    val shortUrlResponse: LiveData<Event<Resource<ShortLinkResponse>>> =
        _shortUrlResponse

    fun getShortUrl(
        url: String?,
        profileId: String,
        assignmentId: String?
    ) = viewModelScope.launch {

        _shortUrlResponse.postValue(Event(Resource.Loading()))

        try {
            if (getApplication<DemoApp>().hasInternetConnection()) {
                val response = repository.getShortLink(url, profileId, assignmentId)
                _shortUrlResponse.postValue(handleShortUrlResponse(response))
            } else {
                _shortUrlResponse.postValue(
                    Event(
                        Resource.Error(
                            getApplication<DemoApp>().getString(
                                R.string.no_internet_connection
                            )
                        )
                    )
                )
            }
        } catch (t: Throwable) {
            Log.d("UPLOAD", "Error -> ${t.localizedMessage}")
            when (t) {
                is IOException -> {
                    _shortUrlResponse.postValue(
                        Event(
                            Resource.Error(
                                getApplication<DemoApp>().getString(
                                    R.string.network_failure
                                )
                            )
                        )
                    )
                }
                else -> {
                    _shortUrlResponse.postValue(
                        Event(
                            Resource.Error(
                                getApplication<DemoApp>().getString(
                                    R.string.conversion_error
                                )
                            )
                        )
                    )
                }
            }
        }
    }

    private fun handleShortUrlResponse(response: Response<ShortLinkResponse>): Event<Resource<ShortLinkResponse>> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Event(Resource.Success(resultResponse))
            }
        }
        return Event(Resource.Error(response.message()))
    }
}