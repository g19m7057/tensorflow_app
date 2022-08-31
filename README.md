# tensorflow_app
My Tensorflow application to detect plant diseases using a TF model.

The model is trained on kaggle useig nthe plantvillage dataset found on kaggle with 
50 000+ images of 38 different classes of plant species and their corresponding diseases.

The model API takes the saved model hosted on the local machine, the model is a VGG16 model
trained using transfer learning. 
The API serves the model to the Android application, which takes as input an image of a plant 
then sends it to the backend, the model in the backend takes the image for prediction returning 
a prediction and confidence score. The result is returned to the application as a JSON response
which the application parses and displays to the user.
