# Data Model

This document outlines the synthetic data structures and relationships that form the database schema for the prototype.

## Core Entities
* Person: Represents a synthetic citizen (e.g., ID: TEST-000001).
* Application: A request for a new enrollment or an update.
* Appointment: A scheduled time at a fictional service center.
* Document: Supporting fictional evidence for an application.
* ServiceCenter: A fictional location for appointments.
* DigitalIdentity: The generated synthetic digital document.
* PhysicalCard: A request record for a physical card.
* AuditEvent: Logs of actions performed within the system for security tracking.

## Entity Relationships
* Person -> Applications (One-to-Many)
* Application -> Appointment (One-to-One or One-to-Many depending on retries)
* Person -> Digital Identity (One-to-One)
* Person -> Physical Card (One-to-Many)

## Synthetic Data Generation
All data in this system is deliberately fake and generated for testing purposes. 
* Identifiers follow a synthetic format (e.g., TEST-000001, TEST-000002).
* Names, addresses, and phone numbers are placeholders (e.g., Test Person, 42 Example Road, +91-90000-00001).
* This ensures the system can be stress-tested with tens of thousands of concurrent requests without exposing or compromising real personal data.
