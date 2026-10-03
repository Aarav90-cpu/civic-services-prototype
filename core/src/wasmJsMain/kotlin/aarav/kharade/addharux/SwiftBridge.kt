/*
 * Copyright 2026 Aarav Ravindra Kharade
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

import kotlin.js.Promise
import kotlin.js.JsString
import kotlinx.coroutines.await

@JsFun("(url, body) => window.fetch(url, { method: 'POST', body: body, headers: {'Content-Type': 'application/json'} }).then(res => res.text())")
private external fun fetchPost(url: String, body: String): Promise<JsString>

@JsFun("(url) => window.fetch(url).then(res => res.text())")
private external fun fetchGet(url: String): Promise<JsString>

actual object SwiftBridge {
    actual suspend fun submitApplication(json: String): String {
        return try {
            fetchPost("http://192.168.31.81:8080/v1/applications", json).await<JsString>().toString()
        } catch (e: Throwable) {
            "Error: ${e.message}"
        }
    }

    actual suspend fun getApplication(id: String): String {
        return try {
            fetchGet("http://192.168.31.81:8080/v1/applications/$id").await<JsString>().toString()
        } catch (e: Throwable) {
            "Error: ${e.message}"
        }
    }
}
