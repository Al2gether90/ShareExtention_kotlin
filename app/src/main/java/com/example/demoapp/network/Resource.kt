package com.example.demoapp.network

sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null,
    val code: Int = 200
) {
    class Success<T>(data: T) : Resource<T>(data)
    class Error<T>(message: String, data: T? = null, code: Int = 200) :
        Resource<T>(data, message, code)

    class Loading<T> : Resource<T>()
}