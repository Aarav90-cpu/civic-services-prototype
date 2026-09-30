# Services and Workflows

This document defines what the synthetic prototype can actually do. The prototype simulates six primary civic workflows using synthetic data.

## 1. New Enrollment
Simulates a fictional person creating a new identity in the system.
* Workflow: Start Enrollment -> Personal Information -> Address -> Supporting Documents -> Appointment -> Review -> Submit -> Application ID
* Outcome: Generates an Application ID (e.g., APP-7F82A1).
* State Progression: Submitted -> Under Review -> Approved.

## 2. Update Details
Simulates an existing synthetic identity (e.g., TEST-000001) submitting a change request (e.g., updating an address from Mumbai to Pune).
* Workflow: Identity -> Update Request -> Review -> Approved -> Identity updated.
* Note: The system creates an update application rather than directly modifying the identity to maintain auditability and security.

## 3. Check Application Status
Allows tracking the status of any application using its identifier.
* Workflow: User enters Application ID (e.g., APP-7F82A1).
* Output: Displays application type, current status, submission date, and last update date.
* State Machine: DRAFT -> SUBMITTED -> RECEIVED -> UNDER_REVIEW -> APPROVED / REJECTED.

## 4. Book Appointment
Simulates booking an appointment at a fictional service center.
* Workflow: User selects a center (e.g., Civic Center 01), chooses an available date and time slot.
* Outcome: Generates an Appointment ID (e.g., APT-92AC10) and associates it with a specific Application ID.

## 5. Download Digital Identity
Generates a synthetic digital identity document once an application is approved.
* Security Feature: The document clearly states "DEMO ONLY" and uses synthetic identifiers (e.g., TEST-000001) to prevent misuse.

## 6. Request Physical Card
Simulates the workflow of requesting a physical civic card.
* Workflow: Request Physical Card -> Confirm Address -> Select delivery option -> Submit -> Generates Card Request ID.
* State Progression: PROCESSING -> DISPATCHED.

## Schema-Driven Service UI
To ensure scalability, the client applications do not hard-code government services. Instead, the UI renders services dynamically from structured definitions provided by the backend.

### Service Definition Format
Each service is defined by a versioned payload that outlines localized titles, necessary requirements, and the step-by-step workflow.

Example for **New Enrollment**:
```json
{
  "id": "enrollment",
  "version": 1,
  "title": {
    "en": "New Identity Enrollment",
    "hi": "नई पहचान पंजीकरण",
    "mr": "नवीन ओळख नोंदणी"
  },
  "requirements": [
    "identity_information",
    "address_information",
    "supporting_document"
  ],
  "workflow": [
    "personal_information",
    "address",
    "documents",
    "appointment",
    "review",
    "submission"
  ]
}
```
This architecture allows the government to introduce new services or alter workflows without requiring an app update.
