package com.example.tensorflow_app

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.media.ThumbnailUtils
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.tensorflow_app.databinding.ActivityMainBinding
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import org.json.JSONObject
import org.json.JSONTokener
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.task.vision.detector.ObjectDetector
import java.io.ByteArrayOutputStream
import java.io.IOException
import kotlin.system.exitProcess


open class MainActivity : AppCompatActivity() {

    companion object {
        private val MEDIA_TYPE_PNG = "image/*".toMediaType()
        private const val imageRequestCode = 100
        private const val requestImageCapture = 1
        @SuppressLint("StaticFieldLeak")
        private lateinit var binding: ActivityMainBinding
        private const val imageSize = 256
    }

    @Suppress("LocalVariableName")
    override fun onCreate(savedInstanceState: Bundle?) {


        val SDK_INT = Build.VERSION.SDK_INT
        if (SDK_INT > 8) {
            val policy = ThreadPolicy.Builder()
                .permitAll().build()
            StrictMode.setThreadPolicy(policy)
        }

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        binding.take.setOnClickListener {
            dispatchTakePictureIntent()
        }
        binding.uploadButton.setOnClickListener {
            pickImage()
        }
    }

//  pick an image from gallery
    private fun pickImage(){
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, imageRequestCode)
    }

//  using camera to take pictures
    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            startActivityForResult(takePictureIntent, requestImageCapture)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(
                this, "Ops could not open camera, Allow in settings", Toast.LENGTH_LONG
            ).show()
            exitProcess(1)
        }
    }

    private fun detect(bitmap: Bitmap?): String {
        var client = OkHttpClient()

        val bos = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.JPEG, 0, bos)
        var img = bos.toByteArray()

        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("file", "file.png",
                RequestBody.create(MEDIA_TYPE_PNG, img))
            .build()

        var request = Request.Builder()
            .url("http://192.168.1.2:5000/predict")
            .post(requestBody)
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")
            return response.body!!.string()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun displayResult(text: String): String {
        val jsonObject = JSONTokener(text).nextValue() as JSONObject
        val confidence = jsonObject.getString("confidence")
        val prediction = jsonObject.getString("prediction")
        prediction.filterNot{it == '\''}
        return "${prediction}.\nConfidence: ${confidence}%."
    } // displayResults

    private fun sendMessage(string: String, bitmap: Bitmap?) {
        val message : String = displayResult(string)
        val intent = Intent(this, MainActivity2::class.java).apply {
            putExtra("results", message)
            putExtra("bitmap", bitmap)
        }
        startActivity(intent)
    } // send to next activity

//    private fun detect(bitmap: Bitmap): String {
//
//        val options = ObjectDetector.ObjectDetectorOptions.builder()
//            .setMaxResults(1)
//            .setScoreThreshold(0.1f)
//            .build()
//
//        val detector = ObjectDetector.createFromFileAndOptions(this,"lite-model_efficientdet_lite0_detection_metadata_1.tflite", options)
//        val image = TensorImage.fromBitmap(bitmap)
//        val results = detector.detect(image)
//
//        val json = JSONObject()
//        results.map {
//            val cat = it.categories.first()
//            val text = cat.label
//            val con = cat.score
//            json.put("confidence", (cat.score*100).roundToInt())
//            json.put("prediction", cat.label)
//        }
//        return json.toString()
//    } // detect for locally hosted model

    private fun scaleImage(bitmap: Bitmap): Bitmap {
        return ThumbnailUtils.extractThumbnail(bitmap, imageSize, imageSize)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if ((requestCode == requestImageCapture || requestCode == imageRequestCode) && resultCode == RESULT_OK) {

            // if taking picture
            if (requestCode == requestImageCapture) {
                var bitmap = data?.extras?.get("data") as Bitmap
                bitmap = scaleImage(bitmap)
                sendMessage(detect(scaleImage(bitmap)), bitmap)
            }

            // if requesting image from gallery
            if (requestCode == imageRequestCode) {
                var image = data?.data
                var bitmap = MediaStore.Images.Media.getBitmap(
                    this.contentResolver,
                    image
                ) // convert to bitmap from Uri

                bitmap = scaleImage(bitmap)
                sendMessage(detect(scaleImage(bitmap)), bitmap)
            }
        }
    }
}




