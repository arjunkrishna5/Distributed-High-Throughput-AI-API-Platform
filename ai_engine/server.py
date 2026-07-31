# Lightweight Python gRPC server listening on localhost port 50051.
# Implements the InferenceService RPC interface to return mock predictions.

import concurrent.futures
import time
import grpc

import inference_pb2
import inference_pb2_grpc


class InferenceServicer(inference_pb2_grpc.InferenceServiceServicer):
    """Implements the gRPC Predict RPC method defined in inference.proto."""

    def Predict(self, request, context):
        print(f"[Python AI Engine] Received request text: '{request.text}'")
        return inference_pb2.InferenceResponse(
            label="POSITIVE",
            confidence_score=0.99
        )


def serve():
    """Initializes and starts the gRPC server on localhost port 50051."""
    server = grpc.server(concurrent.futures.ThreadPoolExecutor(max_workers=10))
    inference_pb2_grpc.add_InferenceServiceServicer_to_server(InferenceServicer(), server)
    
    server.add_insecure_port('[::]:50051')
    server.start()
    print("[Python AI Engine] gRPC Server running on localhost:50051...")
    
    try:
        while True:
            time.sleep(86400)
    except KeyboardInterrupt:
        server.stop(0)


if __name__ == '__main__':
    serve()
