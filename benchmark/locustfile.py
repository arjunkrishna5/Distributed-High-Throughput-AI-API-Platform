# Locust performance benchmark script simulating virtual users hitting Java API Gateway.
# Measures requests per second (RPS), p95 latency percentiles, and rate limiting status under load.

from locust import HttpUser, task, between


class GatewayUser(HttpUser):
    wait_time = between(0.1, 0.5)

    @task
    def predict_sentiment(self):
        payload = {
            "text": "The high-throughput AI API gateway architecture is extremely fast and reliable!"
        }
        headers = {"Content-Type": "application/json"}
        self.client.post("/api/v1/predict", json=payload, headers=headers)
