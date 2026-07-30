# Distributed-High-Throughput-AI-API-Platform

## Problem Statement
Exposing Python Machine Learning web services directly to high-volume user traffic causes severe request stalling and Out-Of-Memory (OOM) crashes due to CPU/GPU processing bottlenecks and Global Interpreter Lock (GIL) limitations.

## Proposed Solution
A polyglot microservices architecture that decouples traffic management from AI model execution:
- **Java 21 Gateway (Spring Boot)**: Manages high-concurrency request routing, authentication, rate limiting, and request queuing using Java 21 Virtual Threads.
- **Python AI Engine (FastAPI / PyTorch)**: Executes AI/ML tensor inference in an isolated, protected process environment.
- **gRPC (HTTP/2 Binary Protocol)**: Low-latency binary inter-service communication pipeline between Java and Python.

## Current Baseline Tech Stack
- **Gateway**: Java 21, Spring Boot 3.x, Swagger UI
- **AI Inference Engine**: Python 3.11+, PyTorch, FastAPI
- **Communication Protocol**: gRPC & Protocol Buffers (HTTP/2)
- **Benchmarking**: Locust
