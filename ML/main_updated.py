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

    frame = cv2.imread(args.image_path)  # Read the image from the provided file path

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

    output_image_path = "C:/Users/ANNA/Desktop/Project/SE/project/ML/output_annotated_image.jpg"
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
import base64
import zlib
import logging
import os
from PIL import Image
import matplotlib.pyplot as plt
from transformers import VisionEncoderDecoderModel, ViTImageProcessor, AutoTokenizer
import torch  # Add this line
from werkzeug.utils import secure_filename

image_path = ""
detections = ""

app = Flask(__name__)

# Configure logging
logging.basicConfig(filename='app.log', level=logging.INFO, format='%(asctime)s - %(name)s - %(levelname)s - %(message)s')
logger = logging.getLogger(__name__)

ZONE_POLYGON = np.array([
    [1, 1],
    [0.5, 0],
    [0.5, 1],
    [0, 1]
])

# Define a directory for safe file uploads (Renamed to secure_upload_folder)
secure_upload_folder = 'uploads'
if not os.path.exists(secure_upload_folder):
    os.makedirs(secure_upload_folder)
app.config['UPLOAD_FOLDER'] = secure_upload_folder

# Load the image captioning model and tokenizer
model = VisionEncoderDecoderModel.from_pretrained("nlpconnect/vit-gpt2-image-captioning")
feature_extractor = ViTImageProcessor.from_pretrained("nlpconnect/vit-gpt2-image-captioning")
tokenizer = AutoTokenizer.from_pretrained("nlpconnect/vit-gpt2-image-captioning")

device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
model.to(device)

max_length = 16
num_beams = 4
gen_kwargs = {"max_length": max_length, "num_beams": num_beams}

def parse_arguments() -> argparse.Namespace:
    parser = argparse.ArgumentParser(description="YOLOv8 live")
    parser.add_argument("--image-path", required=True, help="Path to the input image")
    args = parser.parse_args()
    return args
_yolo = YOLO("yolov5s.pt")

def predict_step(image_paths):
    images = []
    for image_path in image_paths:
        i_image = Image.open(image_path)
        if i_image.mode != "RGB":
            i_image = i_image.convert(mode="RGB")
        images.append(i_image)

    pixel_values = feature_extractor(images=images, return_tensors="pt").pixel_values
    pixel_values = pixel_values.to(device)

    output_ids = model.generate(pixel_values, **gen_kwargs)

    preds = tokenizer.batch_decode(output_ids, skip_special_tokens=True)
    preds = [pred.strip() for pred in preds]
    return preds

def compress_image(image):
    try:
        logger.info("Compressing image")
        # Compress the image data before sending
        compressed_image = zlib.compress(image.tobytes())
        logger.info("Image compression completed")
        return compressed_image

    except Exception as e:
        logger.error(f"Error compressing image: {str(e)}")
        return None

@app.route("/process-image", methods=["POST"])

def process_image():
    global image_path, detections
    try:
        print("Script is running")
        uploaded_image = request.files['image']
        print(uploaded_image)
        if uploaded_image.filename != '':
            filename = secure_filename(uploaded_image.filename)
            print(filename)
            uploaded_image.save(os.path.join(app.config['UPLOAD_FOLDER'], filename))

            image_path = os.path.join(app.config['UPLOAD_FOLDER'], filename)
            #image = cv2.imdecode(np.fromstring(uploaded_image.read(), np.uint8), cv2.IMREAD_COLOR)
            image = cv2.imread(image_path)

            result = _yolo(image, agnostic_nms=True)[0]
            detections = sv.Detections.from_yolov8(result)
            labels = [
                f"{_yolo.model.names[class_id]} {confidence:0.2f}"
                for _, confidence, class_id, _
                in detections
            ]
            image = sv.BoxAnnotator(thickness=2, text_thickness=1, text_scale=0.5).annotate(image, detections, labels)

            height, width, _ = image.shape
            full_annotated_image = np.zeros((height, width * 2, 3), dtype=np.uint8)
            full_annotated_image[:, :width] = image
            full_annotated_image[:, width:] = image


            processed_image_path = "C:/Users/DELL/Desktop/ok/processed_image.jpg"  # Specify the path
            cv2.imwrite(processed_image_path, image)

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
            
            # labels_str = ', '.join(labels)

            # Return both image and labels in the response
            # return jsonify({"image": encoded_image.tobytes().decode("latin1"), "labels": labels_str})

            #image_io = BytesIO(encoded_image.tobytes())

            #image_io.seek(0)
            image_io = base64.b64encode(encoded_image.tobytes()).decode("utf-8")

            captions = predict_step([image_path])
            print(captions)
            additional_text = "Example additional text."
            #return send_file(image_io, mimetype="image/jpeg")
            return jsonify({"image": image_io, "labels" : labels, "additional_text": captions})


            
            

    except Exception as e:
        return jsonify({"error": str(e)})
    

@app.route("/process-query", methods=["POST"])

def process_query():
    cropped_object = ""
    try:
        print("Script is running")
        user_query = request.form['query']
        print(user_query)
        if image_path != '':

            image = cv2.imread(image_path)

            for bbox, confidence, class_id, _ in detections:
                print(_yolo.model.names[class_id])
                if _yolo.model.names[class_id] in user_query:
                    x, y, w, h = map(int, bbox)
                    cropped_object = image[y:y+h, x:x+w]
                    cv2.imwrite(os.path.join(app.config['UPLOAD_FOLDER'], f'object.jpg'), cropped_object)
                    break

            
            # labels_str = ', '.join(labels)

            # Return both image and labels in the response
            # return jsonify({"image": encoded_image.tobytes().decode("latin1"), "labels": labels_str})

            #image_io = BytesIO(encoded_image.tobytes())

            #image_io.seek(0)
            image_p = os.path.join(app.config['UPLOAD_FOLDER'], 'object.jpg')

            captions = predict_step([image_p])
            print(captions)
            additional_text = "Example additional text."
            #return send_file(image_io, mimetype="image/jpeg")
            return jsonify({"additional_text": captions})


            
            

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