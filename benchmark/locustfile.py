# Locust performance benchmark script with automated 3-stage step load shape.
# Measures un-cached latency, virtual thread concurrency scaling, and rate-limiting anti-DoS protection.

from locust import HttpUser, task, between, LoadTestShape


class GatewayUser(HttpUser):
    host = "http://localhost:8080"
    wait_time = between(0.1, 0.5)

    @task
    def predict_sentiment(self):
        payload = {
            "text": "The high-throughput AI API gateway architecture is extremely fast and reliable!"
        }
        headers = {"Content-Type": "application/json"}
        self.client.post("/api/v1/predict", json=payload, headers=headers)


class StepLoadShape(LoadTestShape):
    # Automated 3-stage load testing configuration
    def tick(self):
        run_time = self.get_run_time()

        if run_time < 30:
            return (15, 5)   # Stage 1: 15 Users (100% Green / Un-throttled AI Inferences)
        elif run_time < 60:
            return (60, 10)  # Stage 2: 60 Users (High Concurrency Scaling)
        elif run_time < 90:
            return (200, 25) # Stage 3: 200 Users (Rate-Limiting Anti-DoS Defense)
        else:
            return None
