# Performance Benchmarks

This document outlines the comparative testing framework used to prove the performance claims of the lightweight architecture. 

## Methodology: Measure. Don't Predict.
To avoid premature optimization, benchmarking follows a strict sequential phase strategy:

1. **Phase 1: Correctness (Development Machine)**
   * Deploy the Swift backend to a standard laptop.
   * Connect to local SQLite/PostgreSQL.
   * Ensure all synthetic workflows execute securely and correctly.

2. **Phase 2: Baseline Benchmark (Development Machine)**
   * Execute load tests against the laptop environment to establish a theoretical maximum baseline.

3. **Phase 3: Edge Benchmark (Raspberry Pi 4)**
   * Deploy the exact same backend and database configuration to the Raspberry Pi 4.
   * Run the identical benchmarking suite.

## Comparative Matrix
The final results will be measured (not predicted) and recorded in the following format:

| Metric | Laptop (Baseline) | Raspberry Pi 4 (Edge) |
|---|---|---|
| **Requests/sec** | ??? | ??? |
| **p50 Latency (ms)** | ??? | ??? |
| **p95 Latency (ms)** | ??? | ??? |
| **CPU Load (%)** | ??? | ??? |
| **RAM Usage (MB)** | ??? | ??? |
| **Power (W)** | ??? | ??? |

## Goal
To demonstrate that a secure, well-architected government service platform can operate with high concurrency and low latency on minimal infrastructure by directly comparing standard hardware against resource-constrained edge hardware.
