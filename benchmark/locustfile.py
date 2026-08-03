# Locust performance benchmark script with automated 3-stage step load shape.
# Evaluates PyTorch gRPC inference latency, in-memory caching lookups, and Bucket4j rate limiting.

import random
import uuid
from locust import HttpUser, task, between, LoadTestShape

SAMPLE_SENTENCES = [
    "The high-throughput AI API gateway architecture is extremely fast and reliable!",
    "PyTorch Transformer inference runs with ultra-low latency.",
    "Java 21 virtual threads scale non-blocking concurrency effortlessly.",
    "Bucket4j token bucket rate limiting shields system against traffic spikes.",
    "Polyglot microservices combine the strengths of Java and Python.",
    "Deep learning sentiment analysis model predicts positive and negative text accurately.",
    "gRPC protocol buffers binary IPC provides high-speed internal communication.",
    "In-memory caching serves repeated query predictions in sub-millisecond response time."
]

class GatewayUser(HttpUser):
    host = "http://localhost:8080"
    wait_time = between(0.1, 0.5)

    @task
    def predict_sentiment(self):
        # 80% chance of random text to test PyTorch gRPC inference, 20% unique payload
        text_sample = random.choice(SAMPLE_SENTENCES)
        if random.random() < 0.2:
            text_sample += f" (request-{uuid.uuid4().hex[:8]})"

        payload = {"text": text_sample}
        headers = {"Content-Type": "application/json"}
        self.client.post("/api/v1/predict", json=payload, headers=headers)


class StepLoadShape(LoadTestShape):
    def tick(self):
        run_time = self.get_run_time()

        if run_time < 30:
            return (15, 5)   # Stage 1: 15 Users (Un-throttled AI Inferences)
        elif run_time < 60:
            return (60, 10)  # Stage 2: 60 Users (High Concurrency Scaling)
        elif run_time < 90:
            return (200, 25) # Stage 3: 200 Users (Rate-Limiting Anti-DoS Defense)
        else:
            return None
