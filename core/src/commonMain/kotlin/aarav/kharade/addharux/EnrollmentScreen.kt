package aarav.kharade.addharux

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import aarav.kharade.addharux.civic.enrollment.EnrollmentData
import aarav.kharade.addharux.civic.enrollment.EnrollmentRequest
import aarav.kharade.addharux.civic.enrollment.EnrollmentValidator
import aarav.kharade.addharux.civic.api.ApiClient
import aarav.kharade.addharux.civic.api.ApiResult
import kotlinx.coroutines.launch

@Composable
fun EnrollmentScreen(apiClient: ApiClient, currentLanguage: Language, onSubmitSuccess: (() -> Unit)? = null) {
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
            Text(text = submissionStatus!!, color = MaterialTheme.colorScheme.primary)
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
                    
                    val request = EnrollmentRequest(type = "enrollment", data = data)
                    coroutineScope.launch {
                        try {
                            val result = apiClient.submitEnrollment(request)
                            when (result) {
                                is ApiResult.Success -> {
                                    // Localized prefix + raw ID (IDs are not translated)
                                    submissionStatus = "${"Success!".localized(currentLanguage)} ID: ${result.data.applicationId}"
                                    onSubmitSuccess?.invoke()
                                }
                                is ApiResult.Error -> {
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
