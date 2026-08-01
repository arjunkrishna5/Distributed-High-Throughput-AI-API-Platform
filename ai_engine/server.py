# Python gRPC server running real PyTorch DistilBERT sentiment analysis model on port 50051.
# Receives gRPC text requests, runs neural network inference, and returns real sentiment predictions.

import concurrent.futures
import time
import grpc
from transformers import pipeline

import inference_pb2
import inference_pb2_grpc

print("[Python AI Engine] Loading PyTorch DistilBERT Model into Memory...")
nlp_pipeline = pipeline("sentiment-analysis", model="distilbert-base-uncased-finetuned-sst-2-english")
print("[Python AI Engine] PyTorch AI Model Loaded Successfully!")


class InferenceServicer(inference_pb2_grpc.InferenceServiceServicer):
    """Executes real PyTorch DistilBERT Transformer inference on incoming gRPC requests."""

    def Predict(self, request, context):
        input_text = request.text if request.text.strip() else "I love AI engineering!"
        print(f"[Python AI Engine] Running PyTorch Inference on: '{input_text}'")

        result = nlp_pipeline(input_text)[0]
        label = result['label']
        confidence = float(result['score'])

        print(f"[Python AI Engine] Result: {label} (Confidence: {confidence:.4f})")

        return inference_pb2.InferenceResponse(
            label=label,
            confidence_score=confidence
        )


def serve():
    """Initializes and starts the gRPC server on localhost port 50051."""
    server = grpc.server(concurrent.futures.ThreadPoolExecutor(max_workers=10))
    inference_pb2_grpc.add_InferenceServiceServicer_to_server(InferenceServicer(), server)

    server.add_insecure_port('[::]:50051')
    server.start()
    print("[Python AI Engine] gRPC Server running live on localhost:50051...")

    try:
        while True:
            time.sleep(86400)
    except KeyboardInterrupt:
        server.stop(0)


if __name__ == '__main__':
    serve()
