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

class ApiClient {
    
    suspend fun submitEnrollment(data: EnrollmentData): ApiResult<String> {
        return try {
            // Build the JSON request in the core module
            val firstName = data.fullName.substringBefore(" ")
            val lastName = data.fullName.substringAfter(" ", "")
            val dob = data.dateOfBirth
            
            val json = """
            {
                "firstName": "$firstName",
                "lastName": "$lastName",
                "dob": "$dob",
                "status": "Submitted"
            }
            """.trimIndent()
            
            // Send the request via the SwiftBridge
            val response = SwiftBridge.submitApplication(json)
            
            // Optionally, core parses the Application ID from the response 
            // In a real app we'd parse JSON properly, but this is a vertical slice prototype
            val idMatch = Regex("APP-2026-[A-Z0-9]+").find(response)
            if (idMatch != null) {
                ApiResult.Success(idMatch.value)
            } else {
                ApiResult.Error("Failed to parse Application ID: $response")
            }
        } catch (e: Exception) {
            ApiResult.Error("Exception during enrollment submission: ${e.message}", e)
        }
    }
    
    suspend fun getApplicationStatus(applicationId: String): ApiResult<String> {
        return try {
            val response = SwiftBridge.getApplication(applicationId.trim())
            
            val statusMatch = Regex("\"status\"\\s*:\\s*\"([^\"]+)\"").find(response)
            if (statusMatch != null) {
                ApiResult.Success(statusMatch.groupValues[1])
            } else {
                ApiResult.Error("Could not parse status from: $response")
            }
        } catch (e: Exception) {
            ApiResult.Error("Exception during status check: ${e.message}", e)
        }
    }
}
