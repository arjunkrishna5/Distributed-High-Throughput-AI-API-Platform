// Service component that manages gRPC client channel communication with Python.
// Sends text prediction requests to Python AI Engine running on port 50051.

package com.platform.ai.gateway.service;

import com.platform.ai.grpc.InferenceRequest;
import com.platform.ai.grpc.InferenceResponse;
import com.platform.ai.grpc.InferenceServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class GrpcClientService {

    private final InferenceServiceGrpc.InferenceServiceBlockingStub blockingStub;

    public GrpcClientService() {
        String grpcHost = System.getenv().getOrDefault("GRPC_HOST", "localhost");
        ManagedChannel channel = ManagedChannelBuilder.forAddress(grpcHost, 50051)
                .usePlaintext()
                .build();
        this.blockingStub = InferenceServiceGrpc.newBlockingStub(channel);
    }


    public Map<String, Object> getPrediction(String text) {
        InferenceRequest request = InferenceRequest.newBuilder()
                .setText(text)
                .build();

        InferenceResponse response = this.blockingStub.predict(request);

        return Map.of(
                "label", response.getLabel(),
                "confidence_score", response.getConfidenceScore(),
                "source", "Python AI Engine via gRPC"
        );
    }
}
