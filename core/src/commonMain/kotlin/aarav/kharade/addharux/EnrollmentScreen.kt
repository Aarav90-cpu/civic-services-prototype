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

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import aarav.kharade.addharux.civic.enrollment.EnrollmentData
import aarav.kharade.addharux.civic.enrollment.EnrollmentValidator
import kotlinx.coroutines.launch
import androidx.compose.foundation.text.selection.SelectionContainer

@Composable
fun EnrollmentScreen(currentLanguage: Language, onSubmitSuccess: (() -> Unit)? = null) {
    var fullName by remember { mutableStateOf("") }
    var dob by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var contact by remember { mutableStateOf("") }
    var validationErrors by remember { mutableStateOf(emptyList<String>()) }
    var submissionStatus by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Synthetic Citizen Enrollment".localized(currentLanguage), style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(24.dp))
        
        OutlinedTextField(
            value = fullName,
            onValueChange = { fullName = it },
            label = { Text("Full Name".localized(currentLanguage)) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        
        OutlinedTextField(
            value = dob,
            onValueChange = { dob = it },
            label = { Text("Date of Birth (YYYY-MM-DD)".localized(currentLanguage)) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        
        OutlinedTextField(
            value = address,
            onValueChange = { address = it },
            label = { Text("Address".localized(currentLanguage)) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        
        OutlinedTextField(
            value = contact,
            onValueChange = { contact = it },
            label = { Text("Contact Number".localized(currentLanguage)) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (validationErrors.isNotEmpty()) {
            validationErrors.forEach { error ->
                Text(text = error, color = MaterialTheme.colorScheme.error)
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (submissionStatus != null) {
            SelectionContainer {
                Text(text = submissionStatus!!, color = MaterialTheme.colorScheme.primary)
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        Button(
            onClick = {
                val data = EnrollmentData(fullName, dob, address, contact)
                val errors = EnrollmentValidator.validate(data)
                
                if (errors.isNotEmpty()) {
                    validationErrors = errors
                    submissionStatus = null
                } else {
                    validationErrors = emptyList()
                    isLoading = true
                    submissionStatus = "Submitting...".localized(currentLanguage)
                    
                    coroutineScope.launch {
                        try {
                            // Let the core module handle request building and parsing
                            val apiClient = aarav.kharade.addharux.civic.api.ApiClient
                            val result = apiClient.submitEnrollment(data)
                            
                            when (result) {
                                is aarav.kharade.addharux.civic.api.ApiResult.Success -> {
                                    submissionStatus = "${"Application ID (e.g. APP-2026-XXXXXX)".localized(currentLanguage).substringBefore(" (")}:\n${result.data}\n\n${"Status".localized(currentLanguage)}:\nSubmitted"
                                    onSubmitSuccess?.invoke()
                                }
                                is aarav.kharade.addharux.civic.api.ApiResult.Error -> {
                                    submissionStatus = "${"Error:".localized(currentLanguage)} ${result.message}"
                                }
                            }
                        } catch (e: Exception) {
                            submissionStatus = "${"Error:".localized(currentLanguage)} ${e.message ?: "Unknown error".localized(currentLanguage)}"
                        } finally {
                            isLoading = false
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
            } else {
                Text("Submit Application".localized(currentLanguage))
            }
        }
    }
}
