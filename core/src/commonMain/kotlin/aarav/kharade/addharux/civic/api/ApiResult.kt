package aarav.kharade.addharux.civic.api

import kotlinx.serialization.Serializable

sealed class ApiResult<out T> {
    data class Success<out T>(val data: T) : ApiResult<T>()
    data class Error(val message: String) : ApiResult<Nothing>()
}

@Serializable
data class ApplicationResponse(
    val applicationId: String,
    val status: String
)
