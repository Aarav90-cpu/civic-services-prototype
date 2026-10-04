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
import kotlinx.coroutines.launch
import androidx.compose.foundation.text.selection.SelectionContainer

@Composable
fun StatusScreen(currentLanguage: Language) {
    var applicationId by remember { mutableStateOf("") }
    var applicationStatus by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Application Status Tracking".localized(currentLanguage), style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(24.dp))
        
        OutlinedTextField(
            value = applicationId,
            onValueChange = { applicationId = it },
            label = { Text("Application ID (e.g. APP-2026-XXXXXX)".localized(currentLanguage)) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (applicationStatus != null) {
            SelectionContainer {
                Text(text = applicationStatus!!, color = MaterialTheme.colorScheme.primary)
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        Button(
            onClick = {
                if (applicationId.isNotBlank()) {
                    isLoading = true
                    applicationStatus = "Checking status...".localized(currentLanguage)
                    
                    coroutineScope.launch {
                        try {
                            val apiClient = aarav.kharade.addharux.civic.api.ApiClient
                            val result = apiClient.getApplicationStatus(applicationId.trim())
                            
                            when (result) {
                                is aarav.kharade.addharux.civic.api.ApiResult.Success -> {
                                    applicationStatus = "${"Status".localized(currentLanguage)}: ${result.data}"
                                }
                                is aarav.kharade.addharux.civic.api.ApiResult.Error -> {
                                    applicationStatus = "${"Error:".localized(currentLanguage)} ${result.message}"
                                }
                            }
                        } catch (e: Exception) {
                            applicationStatus = "Error: ${e.message}"
                        } finally {
                            isLoading = false
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading && applicationId.isNotBlank()
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
            } else {
                Text("Check Status".localized(currentLanguage))
            }
        }
    }
}
