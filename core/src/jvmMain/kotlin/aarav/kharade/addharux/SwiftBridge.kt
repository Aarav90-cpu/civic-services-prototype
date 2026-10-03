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

actual object SwiftBridge {
    // For Desktop, we can directly make HTTP requests to the backend since Swift interop is mostly for mobile
    actual suspend fun submitApplication(json: String): String = withContext(Dispatchers.IO) {
        return@withContext try {
            val url = java.net.URI("http://192.168.31.81:8080/v1/applications").toURL()
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.doOutput = true
            
            OutputStreamWriter(connection.outputStream).use { writer ->
                writer.write(json)
            }
            
            if (connection.responseCode in 200..299) {
                connection.inputStream.bufferedReader().use { it.readText() }
            } else {
                "Error: ${connection.responseCode} - ${connection.errorStream.bufferedReader().use { it.readText() }}"
            }
        } catch (e: Exception) {
            "Error: ${e.message}"
        }
    }

    actual suspend fun getApplication(id: String): String = withContext(Dispatchers.IO) {
        return@withContext try {
            val url = java.net.URI("http://192.168.31.81:8080/v1/applications/$id").toURL()
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            
            if (connection.responseCode in 200..299) {
                connection.inputStream.bufferedReader().use { it.readText() }
            } else {
                "Error: ${connection.responseCode} - ${connection.errorStream.bufferedReader().use { it.readText() }}"
            }
        } catch (e: Exception) {
            "Error: ${e.message}"
        }
    }
}
