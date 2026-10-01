package aarav.kharade.addharux.civic.enrollment

import kotlinx.serialization.Serializable

@Serializable
data class EnrollmentData(
    val fullName: String,
    val dateOfBirth: String,
    val address: String,
    val contactNumber: String
)

@Serializable
data class EnrollmentRequest(
    val type: String,
    val data: EnrollmentData
)
