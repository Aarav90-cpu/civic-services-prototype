# CivicFlow

Secure, lightweight prototype for citizen-facing government service navigation.

## Disclaimer

Independent educational prototype.
Not affiliated with UIDAI or any government body.
Uses synthetic data only.

## Problem
Citizens face complex, resource-heavy workflows when navigating civic identity services. This project explores how edge processing and lightweight architecture can simplify this experience while ensuring high data security.

## Architecture
- Client: Kotlin / Compose Multiplatform (Android, Desktop, Web)
- Server: Swift (Vapor) running on Raspberry Pi 4
- Database: SQLite (Server-side)

> **Note:** The Android app requires network access. If you are using LineageOS or another custom ROM with Restricted Networking mode, you must allow the app network access to connect to the local server.

## Features
- Synthetic enrollment
- Application status tracking
- Profile detail updates (Planned)
- Appointment booking (Planned)
- Digital identity generation (Planned)

## Security Model
See `docs/security-model.md`. Enforces strict backend validation; no client-side trust.

## Lightweight Backend
See `docs/api.md` and `docs/data-model.md`.

## Raspberry Pi Benchmark
See `docs/benchmark.md`.

## Reproducibility
All data in this repository is purely synthetic and can be found in `test-data/`. No real citizen information is used or stored.

## Roadmap
1. Core API definitions
2. Local database schema
3. Synthetic data generation scripts
4. KMP (Kotlin Multiplatform) shared core implementation
5. Lightweight server implementation
6. Raspberry Pi load testing

## License

Apache-2.0