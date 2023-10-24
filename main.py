"""
import cv2
import argparse

from ultralytics import YOLO
import supervision as sv
import numpy as np


ZONE_POLYGON = np.array([
    [1, 1],
    [0.5, 0],
    [0.5, 1],
    [0, 1]
])


def parse_arguments() -> argparse.Namespace:
    parser = argparse.ArgumentParser(description="YOLOv8 live")
    parser.add_argument(
        "--webcam-resolution", 
        default=[1500, 720], 
        nargs=2, 
        type=int
    )
    args = parser.parse_args()
    return args


def main():
    args = parse_arguments()
    frame_width, frame_height = args.webcam_resolution

    cap = cv2.VideoCapture(0)
    cap.set(cv2.CAP_PROP_FRAME_WIDTH, frame_width)
    cap.set(cv2.CAP_PROP_FRAME_HEIGHT, frame_height)

    model = YOLO("yolov5s.pt")

    box_annotator = sv.BoxAnnotator(
        thickness=2,
        text_thickness=2,
        text_scale=1
    )

    # zone_polygon = (ZONE_POLYGON * np.array(args.webcam_resolution)).astype(int)
    # zone = sv.PolygonZone(polygon=zone_polygon, frame_resolution_wh=tuple(args.webcam_resolution))
    # zone_annotator = sv.PolygonZoneAnnotator(
    #     zone=zone, 
    #     color=sv.Color.red(),
    #     thickness=2,
    #     text_thickness=4,
    #     text_scale=2
    # )

    while True:
        ret, frame = cap.read()

        result = model(frame, agnostic_nms=True)[0]
        detections = sv.Detections.from_yolov8(result)
        labels = [
            f"{model.model.names[class_id]} {confidence:0.2f}"
            for _, confidence, class_id, _
            in detections
        ]
        frame = box_annotator.annotate(
            scene=frame, 
            detections=detections, 
            labels=labels
        )

        # zone.trigger(detections=detections)
        # frame = zone_annotator.annotate(scene=frame)      
        
        cv2.imshow("yolov8", frame)

        if (cv2.waitKey(30) == 27):
            break


if __name__ == "__main__":
    main()

    

import cv2
import argparse
from ultralytics import YOLO
import supervision as sv
import numpy as np

ZONE_POLYGON = np.array([
    [1, 1],
    [0.5, 0],
    [0.5, 1],
    [0, 1]
])

def parse_arguments() -> argparse.Namespace:
    parser = argparse.ArgumentParser(description="YOLOv8 live")
    parser.add_argument("--image-path", required=True, help="Path to the input image")
    args = parser.parse_args()
    return args

def main():
    args = parse_arguments()

    model = YOLO("yolov5s.pt")

    box_annotator = sv.BoxAnnotator(
        thickness=2,
        text_thickness=2,
        text_scale=1
    )

    frame = cv2.imread(args.image_path) 

    result = model(frame, agnostic_nms=True)[0]
    detections = sv.Detections.from_yolov8(result)
    labels = [
        f"{model.model.names[class_id]} {confidence:0.2f}"
        for _, confidence, class_id, _
        in detections
    ]
    frame = box_annotator.annotate(
        scene=frame, 
        detections=detections, 
        labels=labels
    )

    output_image_path = "PATH/output_annotated_image.jpg"
    #output_image_path = "output_annotated_image.jpg"  # Define the path to save the annotated image
    cv2.imwrite(output_image_path, frame)  # Save the annotated image

if __name__ == "__main__":
    main()
"""

print("Script is running")
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

def parse_arguments() -> argparse.Namespace:
    parser = argparse.ArgumentParser(description="YOLOv8 live")
    parser.add_argument("--image-path", required=True, help="Path to the input image")
    args = parser.parse_args()
    return args

@app.route("/process-image", methods=["POST"])
def process_image():
    try:
        print("Script is running")
        uploaded_image = request.files['image']
        print(uploaded_image)
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
            annotated_image_io = BytesIO()
            cv2.imwrite("annotated_image.jpg", image)

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

"""

from flask import Flask

app = Flask(__name__)

@app.route('/')
def hello():
    return 'Hello, World!'

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)

    """
