@file:Suppress("FunctionName")

package com.example.tensorflow_app

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.graphics.Bitmap
import android.view.View
import com.example.tensorflow_app.databinding.ActivityMain2Binding
import org.json.JSONObject
import org.json.JSONTokener

class MainActivity2 : AppCompatActivity() {
    companion object{
        @SuppressLint("StaticFieldLeak")
        private lateinit var binding: ActivityMain2Binding
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain2Binding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        display_Results()
    } // onCreate

    fun display_Results(){
        val result = intent.getStringExtra("results")
        val bitmap = intent.getParcelableExtra<Bitmap>("bitmap")
        set_image(bitmap)
        set_Prediction(result)
    }
    fun backHome(view: View){
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun set_image(image: Bitmap?){
        binding.imageView.setImageBitmap(image)
    } // set_Image

    private fun set_Prediction(results : String?){
        binding.textView.text = results
    } // set_Prediction
}