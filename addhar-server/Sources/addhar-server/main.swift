// Copyright 2026 Aarav Ravindra Kharde
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

import Vapor
import Fluent
import FluentSQLiteDriver

final class ApplicationModel: Model, @unchecked Sendable, Content {
    static let schema = "applications"
    
    @ID(custom: "id", generatedBy: .user)
    var id: String?
    
    @Field(key: "first_name")
    var firstName: String
    
    @Field(key: "last_name")
    var lastName: String
    
    @Field(key: "dob")
    var dob: String
    
    @Field(key: "status")
    var status: String
    
    init() { }
    
    init(id: String? = nil, firstName: String, lastName: String, dob: String, status: String = "Submitted") {
        self.id = id
        self.firstName = firstName
        self.lastName = lastName
        self.dob = dob
        self.status = status
    }
}

struct CreateApplicationMigration: AsyncMigration {
    func prepare(on database: Database) async throws {
        try await database.schema("applications")
            .field("id", .string, .identifier(auto: false))
            .field("first_name", .string, .required)
            .field("last_name", .string, .required)
            .field("dob", .string, .required)
            .field("status", .string, .required)
            .create()
    }
    
    func revert(on database: Database) async throws {
        try await database.schema("applications").delete()
    }
}

var env = try Environment.detect()
try LoggingSystem.bootstrap(from: &env)
let app = Application(env)
defer { app.shutdown() }

// Setup SQLite Database
app.databases.use(.sqlite(.file("db.sqlite")), as: .sqlite)
app.migrations.add(CreateApplicationMigration())

// Add CORS Middleware (S5)
// Keeping it open to .all because web client origin varies, but can be restricted in production.
let corsConfiguration = CORSMiddleware.Configuration(
    allowedOrigin: .all,
    allowedMethods: [.GET, .POST, .PUT, .OPTIONS, .DELETE, .PATCH],
    allowedHeaders: [.accept, .authorization, .contentType, .origin, .xRequestedWith, .userAgent, .accessControlAllowOrigin]
)
let cors = CORSMiddleware(configuration: corsConfiguration)
app.middleware.use(cors, at: .beginning)

// Auto-migrate
try await app.autoMigrate()

struct ApplicationStatusResponse: Content {
    let id: String
    let status: String
}

// Enrollment endpoint: POST /v1/applications
app.post("v1", "applications") { req async throws -> ApplicationModel in
    req.logger.info("Received new enrollment request")
    let newApp = try req.content.decode(ApplicationModel.self)
    
    // S4: Server-side validation
    guard newApp.firstName.count >= 2, newApp.firstName.count <= 50,
          newApp.lastName.count >= 2, newApp.lastName.count <= 50 else {
        throw Abort(.badRequest, reason: "Names must be between 2 and 50 characters.")
    }
    
    let nameCharacterSet = CharacterSet.letters.union(CharacterSet(charactersIn: " -"))
    guard newApp.firstName.rangeOfCharacter(from: nameCharacterSet.inverted) == nil,
          newApp.lastName.rangeOfCharacter(from: nameCharacterSet.inverted) == nil else {
        throw Abort(.badRequest, reason: "Names can only contain letters, spaces, and hyphens.")
    }
    
    let dateFormatter = DateFormatter()
    dateFormatter.dateFormat = "yyyy-MM-dd"
    if dateFormatter.date(from: newApp.dob) == nil {
        throw Abort(.badRequest, reason: "Invalid Date of Birth format. Expected YYYY-MM-DD.")
    }
    
    // S3: Generate a secure ID like APP-2026-XXXXXXXXXXXX
    let chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
    let randomString = String((0..<12).map { _ in chars.randomElement()! })
    newApp.id = "APP-2026-\(randomString)"
    newApp.status = "Submitted"
    
    try await newApp.save(on: req.db)
    return newApp
}

// Status endpoint: GET /v1/applications/:id
app.get("v1", "applications", ":id") { req async throws -> ApplicationStatusResponse in
    guard let id = req.parameters.get("id") else {
        throw Abort(.badRequest, reason: "Missing application ID")
    }
    req.logger.info("Received status check for ID: \(id)")
    
    guard let application = try await ApplicationModel.find(id, on: req.db) else {
        throw Abort(.notFound, reason: "Application not found")
    }
    
    // S2: Return only ID and status, preventing PII leak
    return ApplicationStatusResponse(id: application.id ?? id, status: application.status)
}

// Start the server on port 8080 binding to all interfaces
app.http.server.configuration.hostname = "0.0.0.0"
app.http.server.configuration.port = 8080

try app.run()
