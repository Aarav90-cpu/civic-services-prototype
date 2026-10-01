import Vapor

struct ApplicationResponse: Content {
    let applicationId: String
    let status: String
}

struct EnrollmentData: Content {
    let fullName: String
    let dateOfBirth: String
    let address: String
    let contactNumber: String
}

struct EnrollmentRequest: Content {
    let type: String
    let data: EnrollmentData
}

@main
struct App {
    // In-memory store for now instead of SQLite to test the architecture quickly
    nonisolated(unsafe) static var applicationsStore: [String: EnrollmentRequest] = [:]
    
    static func main() async throws {
        var env = try Environment.detect()
        try LoggingSystem.bootstrap(from: &env)
        
        let app = try await Vapor.Application.make(env)
        
        // Bind to all interfaces so external devices (like your phone) can connect
        app.http.server.configuration.hostname = "0.0.0.0"
        app.http.server.configuration.port = 8765
        
        // CORS config
        let corsConfiguration = CORSMiddleware.Configuration(
            allowedOrigin: .all,
            allowedMethods: [.GET, .POST, .PUT, .OPTIONS, .DELETE, .PATCH],
            allowedHeaders: [.accept, .authorization, .contentType, .origin, .xRequestedWith, .userAgent, .accessControlAllowOrigin]
        )
        let cors = CORSMiddleware(configuration: corsConfiguration)
        app.middleware.use(cors)
        
        app.post("UX") { req async throws -> ApplicationResponse in
            let enrollmentReq = try req.content.decode(EnrollmentRequest.self)
            
            // Server-side validation
            if enrollmentReq.data.fullName.count < 3 {
                throw Abort(.badRequest, reason: "Name must be at least 3 characters")
            }
            
            let id = "APP-2026-\(String(format: "%06d", App.applicationsStore.count + 1))"
            App.applicationsStore[id] = enrollmentReq
            
            return ApplicationResponse(applicationId: id, status: "submitted")
        }
        
        app.get("UX", ":id") { req async throws -> ApplicationResponse in
            guard let id = req.parameters.get("id") else {
                throw Abort(.badRequest)
            }
            guard App.applicationsStore[id] != nil else {
                throw Abort(.notFound)
            }
            return ApplicationResponse(applicationId: id, status: "submitted")
        }

        try await app.execute()
        try await app.asyncShutdown()
    }
}
