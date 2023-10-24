import cv2
import argparse

from ultralytics import YOLO
import supervision as sv
import numpy as np

from flask import Flask, request, jsonify, send_file
from io import BytesIO

app = Flask(__name__)


ZONE_POLYGON = np.array([
    [1, 1],
    [0.5, 0],
    [0.5, 1],
    [0, 1]
])

#[REFACTOR 1] Can remove this function in refactoring
def parse_arguments() -> argparse.Namespace:
    parser = argparse.ArgumentParser(description="YOLOv8 live")
    parser.add_argument("--image-path", required=True, help="Path to the input image")
    args = parser.parse_args()
    return args

@app.route("/process-image", methods=["POST"])
def process_image():
    try:
        #[REFACTOR 2] Remove unwanted print statement/Replace with logger
        # print("Script is running")
        # app.logger.info("Script started running")

        
        uploaded_image = request.files['image']
        # print("Script is running")
        # app.logger.info("uploaded file %s",uploaded_image.filename)
        print(uploaded_image)
        #[REFACTOR 3]
        # instead of if uploaded_image.filename != '':
        # Use if image in request.files and uploaded_image.filename !='':
        # This reduces errors
        if uploaded_image.filename != '':
            image = cv2.imdecode(np.fromstring(uploaded_image.read(), np.uint8), cv2.IMREAD_COLOR)

            model = YOLO("yolov5s.pt")
            result = model(image, agnostic_nms=True)[0]
            detections = sv.Detections.from_yolov8(result)
            labels = [
                f"{model.model.names[class_id]} {confidence:0.2f}"
                for _, confidence, class_id, _
                in detections
            ]
            image = sv.BoxAnnotator(thickness=2, text_thickness=2, text_scale=1).annotate(image, detections, labels)
            """
            # Save the annotated image to a BytesIO object
            annotated_image_io = BytesIO()
            cv2.imwrite("annotated_image.jpg", image)

            # Set the BytesIO object's cursor position to the beginning
            annotated_image_io.seek(0)

            return send_file(annotated_image_io, mimetype="image/jpeg")
            

            """
            success, encoded_image = cv2.imencode(".jpg", image)
            if not success:
                return jsonify({"error": "Failed to encode the image"})

            image_io = BytesIO(encoded_image.tobytes())

            image_io.seek(0)

            return send_file(image_io, mimetype="image/jpeg")
            
            

    except Exception as e:
        return jsonify({"error": str(e)})

if __name__ == "__main__":
    print("Script is running")
    app.debug = True
    app.run(host='localhost', port=5000)
