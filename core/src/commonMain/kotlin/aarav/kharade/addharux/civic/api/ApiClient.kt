/*
 * Copyright 2026 Aarav Ravindra Kharde
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package aarav.kharade.addharux.civic.api

import aarav.kharade.addharux.SwiftBridge
import aarav.kharade.addharux.civic.enrollment.EnrollmentData
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString

@Serializable
private data class ApplicationRequest(
    val firstName: String,
    val lastName: String,
    val dob: String,
    val status: String = "Submitted"
)

@Serializable
private data class ApplicationResponse(
    val id: String,
    val status: String
)

object ApiClient {
    private val jsonConfig = Json { 
        ignoreUnknownKeys = true 
        encodeDefaults = true
    }
    
    suspend fun submitEnrollment(data: EnrollmentData): ApiResult<String> {
        return try {
            val firstName = data.fullName.substringBefore(" ")
            val lastName = data.fullName.substringAfter(" ", "")
            
            val requestBody = ApplicationRequest(
                firstName = firstName,
                lastName = lastName,
                dob = data.dateOfBirth
            )
            val jsonString = jsonConfig.encodeToString(requestBody)
            
            val response = SwiftBridge.submitApplication(jsonString)
            
            if (response.startsWith("Error:")) {
                return ApiResult.Error(response)
            }
            
            val apiResponse = jsonConfig.decodeFromString<ApplicationResponse>(response)
            ApiResult.Success(apiResponse.id)
        } catch (e: Exception) {
            ApiResult.Error("Exception during enrollment submission: ${e.message}", e)
        }
    }
    
    suspend fun getApplicationStatus(applicationId: String): ApiResult<String> {
        val trimmedId = applicationId.trim()
        if (!trimmedId.matches(Regex("^APP-2026-[A-Z0-9]+$"))) {
            return ApiResult.Error("Invalid Application ID format")
        }
        return try {
            val response = SwiftBridge.getApplication(trimmedId)
            
            if (response.startsWith("Error:")) {
                return ApiResult.Error(response)
            }
            
            val apiResponse = jsonConfig.decodeFromString<ApplicationResponse>(response)
            ApiResult.Success(apiResponse.status)
        } catch (e: Exception) {
            ApiResult.Error("Exception during status check: ${e.message}", e)
        }
    }
}
