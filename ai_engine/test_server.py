# Unit test suite for Python PyTorch AI Engine service.
# Tests sentiment classification logic and gRPC message response structures.

import unittest
from server import InferenceServicer
import inference_pb2


class TestInferenceService(unittest.TestCase):

    def setUp(self):
        self.servicer = InferenceServicer()


    def test_predict_positive(self):
        request = inference_pb2.InferenceRequest(text="The AI gateway architecture is fast and reliable!")
        context = None
        response = self.servicer.Predict(request, context)
        
        self.assertEqual(response.label, "POSITIVE")
        self.assertGreater(response.confidence_score, 0.5)

    def test_predict_negative(self):
        request = inference_pb2.InferenceRequest(text="The system crashed due to severe network bottlenecks.")
        context = None
        response = self.servicer.Predict(request, context)
        
        self.assertEqual(response.label, "NEGATIVE")
        self.assertGreater(response.confidence_score, 0.5)


if __name__ == "__main__":
    unittest.main()
