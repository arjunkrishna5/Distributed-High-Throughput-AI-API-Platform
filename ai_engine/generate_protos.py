# Helper script to compile proto/inference.proto into Python gRPC modules.
# Uses grpc_tools.protoc to generate inference_pb2.py and inference_pb2_grpc.py.

import os
import sys
from grpc_tools import protoc

def compile_proto():
    proto_path = os.path.abspath("../proto")
    proto_file = os.path.join(proto_path, "inference.proto")
    out_dir = os.path.dirname(os.path.abspath(__file__))

    command = [
        'grpc_tools.protoc',
        f'-I{proto_path}',
        f'--python_out={out_dir}',
        f'--grpc_python_out={out_dir}',
        proto_file,
    ]
    
    print(f"Compiling {proto_file} into {out_dir}...")
    exit_code = protoc.main(command)
    if exit_code == 0:
        print("Proto compilation successful!")
    else:
        print(f"Proto compilation failed with exit code: {exit_code}")

if __name__ == '__main__':
    compile_proto()
