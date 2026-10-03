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

package aarav.kharade.addharux.civic.enrollment

object EnrollmentValidator {
    fun validate(data: EnrollmentData): List<String> {
        val errors = mutableListOf<String>()
        
        if (data.fullName.trim().length < 3) {
            errors.add("Name must be at least 3 characters long")
        }
        
        if (!data.dateOfBirth.matches(Regex("""\d{4}-\d{2}-\d{2}"""))) {
            errors.add("Date of birth must be in YYYY-MM-DD format")
        }
        
        if (data.address.trim().length < 10) {
            errors.add("Address must be at least 10 characters long")
        }
        
        if (!data.contactNumber.matches(Regex("""^\+?[0-9]{10,14}$"""))) {
            errors.add("Contact number is invalid")
        }
        
        return errors
    }
}
