# Dockerfile for Python PyTorch gRPC AI Inference Engine service on port 50051.
# Installs PyTorch, Transformers, gRPC dependencies, compiles Protobufs, and starts gRPC server.

FROM python:3.11-slim

WORKDIR /app

# Install system build dependencies
RUN apt-get update && apt-get install -y --no-install-recommends \
    build-essential \
    && rm -rf /var/lib/apt/lists/*

# Copy requirements and install dependencies
COPY ai_engine/requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# Copy source code and protobuf definitions
COPY ai_engine/ .
COPY proto/ ./proto

# Compile Protobuf definitions
RUN python generate_protos.py

# Pre-download Hugging Face DistilBERT Transformer model weights during build for 1-second boot time
RUN python -c "from transformers import pipeline; pipeline('sentiment-analysis', model='distilbert-base-uncased-finetuned-sst-2-english')"

EXPOSE 50051


CMD ["python", "server.py"]
