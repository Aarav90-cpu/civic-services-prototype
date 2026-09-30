# Security Model

This document defines the threat model and defense mechanisms for the prototype architecture.

## Security Boundaries

```text
Kotlin UI
   │
   │ requests
   ▼
Swift Core
   │
   ├── validation
   ├── policy
   ├── local processing
   └── request preparation
   │
   ▼
Backend
   │
   ├── authentication
   ├── application state
   └── synchronization
```

## Hard Constraints

### Client must NEVER:
* store backend secrets
* bypass authentication
* make authorization decisions
* trust user-supplied IDs
* store unnecessary sensitive information

### Server must NEVER:
* trust client validation
* assume the client is honest
* expose database credentials
* accept arbitrary service definitions

The client is always hostile from the server's perspective. Even a well-engineered Swift core can be modified if someone controls the physical device. The backend must independently validate every request.

## Threat: Unauthorized Access to Applications
* Threat Description: An attacker modifies an applicationId in an API request to view or modify an application belonging to another user.
* Defense Mechanism: The backend enforces strict ownership validation. 
  1. Authenticate user.
  2. Verify the authenticated user owns the requested application.
  3. Verify the requested operation is allowed for the application's current state.

## Threat: Direct Identity Modification
* Threat Description: An attacker attempts to directly mutate their identity record (e.g., sending a POST request to update an address directly).
* Defense Mechanism: Immutable identity records. Changes can only occur through the Application state machine. An Update Application must be created, reviewed, and approved before the system updates the Person record.

## Threat: Malicious Client
* Threat Description: An attacker reverse-engineers or modifies the client application to send malformed data or bypass UX restrictions.
* Defense Mechanism: The lightweight backend treats all incoming data as untrusted, enforcing strict schema validation and rate limiting.
