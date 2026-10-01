package aarav.kharade.addharux.civic.api

import aarav.kharade.addharux.civic.enrollment.EnrollmentRequest
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

// serverHost is injected by each platform's entry point.
// Android passes BuildConfig.SERVER_HOST (auto-detected LAN IP at build time).
// Desktop and web default to localhost.
//
// skipLoopback: set to true on physical Android devices — 127.0.0.1 on a phone is the
// phone itself, not the dev machine. Trying it wastes 8 seconds per attempt.
class ApiClient(
    private val serverHost: String = "192.168.31.81",
    private val skipLoopback: Boolean = false
) {

    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
        // Without a timeout, a hung TCP connection stalls the coroutine forever (UI freezes).
        install(HttpTimeout) {
            connectTimeoutMillis = 8_000
            requestTimeoutMillis = 8_000
            socketTimeoutMillis = 8_000
        }
    }

    suspend fun submitEnrollment(request: EnrollmentRequest): ApiResult<ApplicationResponse> {
        // Build the fallback list. Loopback entries are excluded on physical devices.
        val hostsToTry = buildList {
            add("http://$serverHost:8765")  // LAN IP (correct path for Android on same WiFi)
            add("http://10.0.2.2:8765")     // emulator gateway to host
            if (!skipLoopback) {
                add("http://127.0.0.1:8765") // loopback — only useful on desktop/web
            }
        }

        var lastError: String? = null

        for (host in hostsToTry) {
            try {
                println("Trying host: $host")
                val response = client.post("$host/UX/") {
                    contentType(ContentType.Application.Json)
                    header("Bypass-Tunnel-Reminder", "true")
                    setBody(request)
                }

                if (response.status.isSuccess()) {
                    val data = response.body<ApplicationResponse>()
                    return ApiResult.Success(data)
                } else {
                    return ApiResult.Error("Server returned error: ${response.status}")
                }
            } catch (e: Exception) {
                println("Ktor exception on $host: ${e.message}")
                lastError = e.message
            }
        }

        return ApiResult.Error("Network error across all hosts: $lastError")
    }
}
