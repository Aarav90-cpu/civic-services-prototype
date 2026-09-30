# API Contract

This document defines the communication interfaces between the mobile clients (Kotlin/Swift) and the lightweight backend.

## Design Philosophy
The API relies on clearly defined contracts so the client and backend can be developed independently. All endpoints enforce authentication and strict payload validation.

## Core Endpoints

### Applications
* POST /v1/applications
  * Purpose: Creates a new application (enrollment or update).
* GET /v1/applications/{id}
  * Purpose: Retrieves the status and details of a specific application.

### Appointments
* POST /v1/appointments
  * Purpose: Books a new appointment tied to an application.
* GET /v1/appointments/{id}
  * Purpose: Retrieves appointment details.

### Services
* GET /v1/services
  * Purpose: Retrieves definitions and metadata for available fictional services and service centers.

### Identity
* GET /v1/identity/{id}/digital
  * Purpose: Retrieves the synthetic digital identity document (watermarked as DEMO ONLY).
