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

package aarav.kharade.addharux
import java.net.HttpURLConnection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL
import java.io.OutputStreamWriter
import java.net.ConnectException

actual object SwiftBridge {
    
    private fun doRequest(urlString: String, method: String, body: String? = null): String {
        val url = java.net.URI(urlString).toURL()
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = method
        connection.connectTimeout = 3000
        connection.readTimeout = 5000
        
        if (body != null) {
            connection.setRequestProperty("Content-Type", "application/json")
            connection.doOutput = true
            OutputStreamWriter(connection.outputStream).use { writer ->
                writer.write(body)
            }
        }
        
        return if (connection.responseCode in 200..299) {
            connection.inputStream.bufferedReader().use { it.readText() }
        } else {
            "Error: ${connection.responseCode} - ${connection.errorStream?.bufferedReader()?.use { it.readText() } ?: ""}"
        }
    }

    actual suspend fun submitApplication(json: String): String = withContext(Dispatchers.IO) {
        return@withContext try {
            doRequest("http://192.168.31.31:8080/v1/applications", "POST", json)
        } catch (e: ConnectException) {
            try {
                // Fallback for Android Emulator localhost
                doRequest("http://10.0.2.2:8080/v1/applications", "POST", json)
            } catch (fallbackEx: Exception) {
                "Error: ${e.message} (Fallback also failed: ${fallbackEx.message})"
            }
        } catch (e: Exception) {
            "Error: ${e.stackTraceToString()}"
        }
    }

    actual suspend fun getApplication(id: String): String = withContext(Dispatchers.IO) {
        return@withContext try {
            doRequest("http://192.168.31.31:8080/v1/applications/$id", "GET")
        } catch (e: ConnectException) {
            try {
                // Fallback for Android Emulator localhost
                doRequest("http://10.0.2.2:8080/v1/applications/$id", "GET")
            } catch (fallbackEx: Exception) {
                "Error: ${e.message} (Fallback also failed: ${fallbackEx.message})"
            }
        } catch (e: Exception) {
            "Error: ${e.stackTraceToString()}"
        }
    }
}

