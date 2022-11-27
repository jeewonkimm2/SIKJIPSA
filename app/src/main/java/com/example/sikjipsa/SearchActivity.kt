package com.example.sikjipsa

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.SearchManager
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.ImageDecoder
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.example.sikjipsa.databinding.ActivitySearchBinding
import com.example.sikjipsa.navigation.AddPhotoActivity
import com.example.sikjipsa.navigation.AlarmFragment
import com.example.sikjipsa.navigation.DetailViewFragment
import com.example.sikjipsa.navigation.UserFragment
import com.google.firebase.ml.modeldownloader.CustomModel
import com.google.firebase.ml.modeldownloader.CustomModelDownloadConditions
import com.google.firebase.ml.modeldownloader.DownloadType
import com.google.firebase.ml.modeldownloader.FirebaseModelDownloader
import kotlinx.android.synthetic.main.activity_add_photo.*
import kotlinx.android.synthetic.main.activity_main.*
import org.tensorflow.lite.Interpreter
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.lang.Byte
import java.lang.Float
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.*

class SearchActivity : AppCompatActivity() {
    var PICK_IMAGE_FROM_ALBUM = 0
    var photoURI: Uri? = null

    //    사진부분
    private lateinit var binding: ActivitySearchBinding

    val PERMISSIONS = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

    val PERMISSIONS_REQUEST = 100

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.mainBtn.setOnClickListener {
            var photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            startActivityForResult(photoPickerIntent, PICK_IMAGE_FROM_ALBUM)

            fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
                super.onActivityResult(requestCode, resultCode, data)
                if (requestCode == PICK_IMAGE_FROM_ALBUM) {
                    if (resultCode == RESULT_OK) {
                        photoURI = data?.data
                    } else {
                        finish()
                    }
                }
            }
        }



        binding.keywordBtn.setOnClickListener {
            binding.keywordBtn.visibility = GONE
            binding.keywordTxt.visibility = VISIBLE
            binding.searchBtn.visibility = VISIBLE
        }
        binding.searchBtn.setOnClickListener {
            binding.keywordTxt.visibility = INVISIBLE
            binding.searchBtn.visibility = INVISIBLE
            binding.GOOGLE.visibility = VISIBLE
            binding.SIKJIPSA.visibility = VISIBLE
        }

        binding.GOOGLE.setOnClickListener {
            var intent = Intent(Intent.ACTION_WEB_SEARCH)
            intent.putExtra(SearchManager.QUERY,"${binding.keywordTxt.text}")
            startActivity(intent)
        }

        binding.SIKJIPSA.setOnClickListener {

//            필터링된 화면 뜨게 하고 싶음!
        }



        val conditions = CustomModelDownloadConditions.Builder()
            .requireWifi()  // Also possible: .requireCharging() and .requireDeviceIdle()
            .build()
        FirebaseModelDownloader.getInstance()
            .getModel("model_fp16", DownloadType.LOCAL_MODEL_UPDATE_IN_BACKGROUND,
                conditions)
            .addOnSuccessListener { model: CustomModel? ->
                // Download complete. Depending on your app, you could enable the ML
                // feature, or switch from the local model to the remote model, etc.

                // The CustomModel object contains the local path of the model file,
                // which you can use to instantiate a TensorFlow Lite interpreter.
                var interpreter:Interpreter?=null
                val modelFile = model?.file
                if (modelFile != null) {
                    interpreter = Interpreter(modelFile)
                    Log.d("probabilities","model import done")

                    val plant = getResources().getDrawable(R.drawable.monstera)

                    val bitmap = Bitmap.createScaledBitmap(plant.toBitmap(), 224, 224, true)
                    val input = ByteBuffer.allocateDirect(224*224*3*4).order(ByteOrder.nativeOrder())
                    for (y in 0 until 224) {
                        for (x in 0 until 224) {
                            val px = bitmap.getPixel(x, y)

                            // Get channel values from the pixel value.
                            val r = Color.red(px)
                            val g = Color.green(px)
                            val b = Color.blue(px)

                            // Normalize channel values to [-1.0, 1.0]. This requirement depends on the model.
                            // For example, some models might require values to be normalized to the range
                            // [0.0, 1.0] instead.
                            val rf = (r - 127) / 255f
                            val gf = (g - 127) / 255f
                            val bf = (b - 127) / 255f

                            input.putFloat(rf)
                            input.putFloat(gf)
                            input.putFloat(bf)
                        }
                    }

                    val bufferSize = 18 * java.lang.Float.SIZE / java.lang.Byte.SIZE
                    val modelOutput = ByteBuffer.allocateDirect(bufferSize).order(ByteOrder.nativeOrder())
                    Log.d("probabilities","model is running")
                    interpreter?.run(input, modelOutput)
                    Log.d("probabilities","model running done")


                    modelOutput.rewind()
                    val probabilities = modelOutput.asFloatBuffer()


                    try {
                        Log.d("probabilities","ready to show result")
                        val reader = BufferedReader(
                            InputStreamReader(assets.open("labels.txt")))
                        for (i in 0 until probabilities.capacity()) {
                            val label: String = reader.readLine()
                            val probability = probabilities.get(i)
                            Log.d("probabilities","$label: $probability")
                        }
                    } catch (e: IOException) {
                        // File not found?
                    }
                }

            }

        }



}