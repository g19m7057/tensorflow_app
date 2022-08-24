package com.example.tensorflow_app

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.ViewModel
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.util.*


class MainActivityViewModel : ViewModel() {

    companion object {
        private val MEDIA_TYPE_PNG = "image/png".toMediaType()
    }

    fun request(image : Uri?): String {

        var client = OkHttpClient()

//        val img_path = "/Users/sifisokuhlemazibuko/Documents/honours/honours_project/PlantVillage/"+
//                "Peach_Healthy" + "/Peach-Healthy-IMG_4124.jpg"
//
//        var file = File(img_path)

//        val requestBody = MultipartBody.Builder()
//            .setType(MultipartBody.FORM)
//            .addFormDataPart("file", "file.png",
//                RequestBody.create(MEDIA_TYPE_PNG, file))
//            .build()

//        val request = Request.Builder()
//            .url("http://192.168.1.2:5000/predict")
//            .post(file.asRequestBody(MEDIA_TYPE_PNG))
//            .build()

        val bm = BitmapFactory.decodeFile(image.toString())
        val bao = ByteArrayOutputStream()
        bm.compress(Bitmap.CompressFormat.JPEG, 90, bao)
        val ba = bao.toByteArray()

        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("file", "file.png",
                RequestBody.create(MEDIA_TYPE_PNG, ba))
            .build()

//        todo: save the image a png and upload it automatically from storage
//              what does the API expect the image to be bitmap? png? jpg?

        var request = Request.Builder()
            .url("http://192.168.1.2:5000/")
            .post(requestBody)
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")
            return response.body!!.string()
        }
    }
}
