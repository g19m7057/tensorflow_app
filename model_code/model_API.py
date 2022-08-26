from json import load
import json
from flask import Flask
import io
import numpy as np
import tensorflow as tf
from PIL import Image
from flask import Flask, jsonify, request 
import cv2

model_path = "/Users/sifisokuhlemazibuko/Documents/honours/honours_project/models/tensorflow_model_VGG16.h5"
label_path = "/Users/sifisokuhlemazibuko/AndroidStudioProjects/tensorflow_app/app/src/main/assets/plant_classes.txt"

with open(label_path) as f:
    classnames = f.readlines()

def convert(ls):
    return ls[0].split(",")

classes = convert(classnames)

model = tf.keras.models.load_model(model_path, compile=False)

def predict(image):
    probabilities = model.predict(np.asarray(image))[0]
    class_idx = np.argmax(probabilities)
    return {classes[class_idx]: probabilities[class_idx]}

app = Flask(__name__)

def load_image(img):
    img = Image.open(io.BytesIO(img))
    img = np.array(img)
    img = cv2.resize(img, (255, 255))
    img = np.expand_dims(img, 0)
    return img 


@app.route('/predict', methods=['POST'])
def detect():
    # Catch the image file from a POST request
    if 'file' not in request.files:
        print(request)
        return "Please try again. The Image doesn't exist"
    
    file = request.files.get('file')
    print(request)

    if not file:
        return

    img_bytes = file.read()
    img = load_image(img_bytes)
    # cv2.imshow("image", img)
    result = predict(img/255)

    confidence = str(round(list(result.values())[0] * 100, 2))

    dictionary = {
        "prediction": list(result.keys())[0],
        "confidence": confidence,
    }
    
    # Serializing json
    result = json.dumps(dictionary, indent=2)

    # Return on a JSON format
    return result
    # return jsonify(list(result.keys())[0], str(list(result.values())[0] * 100))


@app.route("/")
def main():
    return "hello"


if __name__ == '__main__':
    app.run(host='0.0.0.0', debug=True)