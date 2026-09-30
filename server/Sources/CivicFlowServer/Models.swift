// Copyright 2026 Aarav Ravindra Kharade
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

import Foundation

struct Person: Codable, Identifiable {
    let id: String
    let name: String
    let dateOfBirth: String
    let address: String
    let contact: String
}

struct Application: Codable, Identifiable {
    let id: String
    let personId: String
    let type: String
    let status: String
    let createdAt: String
    let updatedAt: String
}

struct Appointment: Codable, Identifiable {
    let id: String
    let applicationId: String
    let centerId: String
    let time: String
    let status: String
}

struct ServiceTitle: Codable {
    let en: String
    let hi: String
    let mr: String
}

struct ServiceDefinition: Codable, Identifiable {
    let id: String
    let version: Int
    let title: ServiceTitle
    let requirements: [String]
    let workflow: [String]
}
