package com.example.sikjipsa

import android.Manifest
import android.annotation.SuppressLint
import android.app.SearchManager
import android.content.Intent
import android.graphics.*
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.sikjipsa.databinding.ActivitySearchBinding
import com.example.sikjipsa.navigation.SearchResultFragment
import com.google.firebase.ml.modeldownloader.CustomModel
import com.google.firebase.ml.modeldownloader.CustomModelDownloadConditions
import com.google.firebase.ml.modeldownloader.DownloadType
import com.google.firebase.ml.modeldownloader.FirebaseModelDownloader
import org.tensorflow.lite.Interpreter
import java.io.*
import java.nio.ByteBuffer
import java.nio.ByteOrder

class SearchActivity : AppCompatActivity() {
    var PICK_IMAGE_FROM_ALBUM = 0
    var photoURI: Uri? = null
    var keywordFromModel:String? = null
    var probFromModel:kotlin.Float = 0.0f
    private lateinit var binding: ActivitySearchBinding
//    Permission for accessing camera album
    val PERMISSIONS = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )
//    Permission code
    val PERMISSIONS_REQUEST = 100

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        If you want to search about something in keyword, click keyword button
        binding.keywordBtn.setOnClickListener {
            binding.keywordBtn.visibility = GONE
            binding.keywordTxt.visibility = VISIBLE
            binding.searchBtn.visibility = VISIBLE
        }

//        If you click search button
        binding.searchBtn.setOnClickListener {
            binding.keywordTxt.visibility = INVISIBLE
            binding.searchBtn.visibility = INVISIBLE
            binding.GOOGLE.visibility = VISIBLE
            binding.SIKJIPSA.visibility = VISIBLE
        }

//        If you wanna get result from google
        binding.GOOGLE.setOnClickListener {
            var intent = Intent(Intent.ACTION_WEB_SEARCH)
            intent.putExtra(SearchManager.QUERY,"${binding.keywordTxt.text}")
            startActivity(intent)
        }

//        Event for clicking SIKJIPSA button after setting keyword
        binding.SIKJIPSA.setOnClickListener {
            binding.GOOGLE.visibility = GONE
            binding.SIKJIPSA.visibility = GONE
            binding.imageBtn.visibility = GONE
            binding.search.visibility = GONE

//            Initializing view
            initView()

            var fragment = SearchResultFragment()
            var bundle = Bundle()
            bundle.putString("keyword","${binding.keywordTxt.text}")
//            Passing bundle with keyword data to arguments in fragment
//            Result is comes up in SearchResultFragmet
            fragment.arguments = bundle
            supportFragmentManager!!.beginTransaction()
                .replace(R.id.main_content,fragment)
                .commit()

        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
            1
        )
    }

//        If you wanna search with image recognition
        binding.imageBtn.setOnClickListener {
//            Bringing image from album
            var photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            startActivityForResult(photoPickerIntent, PICK_IMAGE_FROM_ALBUM)

            binding.imageBtn.visibility = INVISIBLE
            binding.GOOGLEImg.visibility = VISIBLE
            binding.SIKJIPSAImg.visibility = VISIBLE
        }
    }

//    Function for initializing fragment
    private fun initView() {
        supportFragmentManager.beginTransaction().add(R.id.main_content, SearchResultFragment())
            .commit()
    }

//    Importing image from album -> ML
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (requestCode == PICK_IMAGE_FROM_ALBUM) {
        if (resultCode == AppCompatActivity.RESULT_OK) {
            photoURI = data?.data
            var bitmapFinal:Bitmap?=null
            try{
                photoURI?.let{
//                    If SDK version is less than 28
                    if(Build.VERSION.SDK_INT<28){
                        val bitmap = MediaStore.Images.Media.getBitmap(
                            contentResolver,
                            photoURI
                        )
                        bitmapFinal = bitmap
                    }
//                    If SDK version is greater than 28
                    else{
                        val source = ImageDecoder.createSource(contentResolver,
                            photoURI!!
                        )
                        val bitmap = ImageDecoder.decodeBitmap(source)
                        bitmapFinal = bitmap
                    }
                }

//                Importing model called model_fp16 from Firebase Maching Learning server
                val conditions = CustomModelDownloadConditions.Builder()
                    .requireWifi()
                    .build()
                FirebaseModelDownloader.getInstance()
                    .getModel("model_fp16", DownloadType.LOCAL_MODEL_UPDATE_IN_BACKGROUND,
                        conditions)
                    .addOnSuccessListener {model: CustomModel? ->
//                        Initializing interpreter
                        var interpreter: Interpreter? = null
                        val modelFile = model?.file
                        if (modelFile != null) {
                            interpreter = Interpreter(modelFile)
//                            As image from album is HardwareBitmap, I changed it into softwareBitmap
                            var softwareBitmap = bitmapFinal!!.copy(Bitmap.Config.ARGB_8888,true)
//                            Scaling Bitmap 224*224
                            val bitmap = Bitmap.createScaledBitmap(softwareBitmap, 224, 224, true)
//                            Putting in buffer
                            val input = ByteBuffer.allocateDirect(224*224*3*4).order(ByteOrder.nativeOrder())

//                            To obtain rgb and to normalize the values
                            for (y in 0 until 224) {
                                for (x in 0 until 224) {
                                    val px = bitmap.getPixel(x, y)

                                    // Getting rgb
                                    val r = Color.red(px)
                                    val g = Color.green(px)
                                    val b = Color.blue(px)

//                                Normalizing
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
//                            Interpreter is interpreting input and extract output in modelOutput
                            interpreter?.run(input, modelOutput)
                            modelOutput.rewind()
//                            Getting probabilities for each labels
                            val probabilities = modelOutput.asFloatBuffer()

                            try {
//                                label opens to match
                                val reader = BufferedReader(
                                    InputStreamReader(assets.open("labels.txt")))
                                var bestLabel: String? = null
                                var bestProb: kotlin.Float = 0.0f
//                                Getting the highest probabilty
                                for (i in 0 until probabilities.capacity()) {
                                    val label: String = reader.readLine()
                                    val probability = probabilities.get(i)
                                    if(bestProb < probability){
                                        bestProb = probability
                                        bestLabel = label
                                    }
                                }
//                                Final result(label and probability) from model
                                probFromModel = bestProb
                                keywordFromModel = bestLabel

                            } catch (e: IOException) {
                            }
                        }
                    }
            }catch(e:Exception){
                e.printStackTrace()
            }
        } else {
            finish()
        }

    }

//    If you click GOOGLE after getting result from model
    binding.GOOGLEImg.setOnClickListener {
        var intent = Intent(Intent.ACTION_WEB_SEARCH)
        intent.putExtra(SearchManager.QUERY,"$keywordFromModel")
        startActivity(intent)
    }

//    If you click SIKJIPSA after getting result from model
    binding.SIKJIPSAImg.setOnClickListener {
        binding.search.visibility = GONE
        binding.SIKJIPSAImg.visibility = GONE
        binding.GOOGLEImg.visibility = GONE
        binding.searchBtn.visibility = GONE
        binding.keywordTxt.visibility = GONE
        binding.searchBtn.visibility = GONE
        binding.keywordBtn.visibility = GONE

//        Initializing view
        initView()

        var fragment = SearchResultFragment()
        var bundle = Bundle()
        bundle.putString("keyword","$keywordFromModel")
        //Passing bundle with keyword data to arguments in fragment
        //Result is comes up in SearchResultFragmet
        fragment.arguments = bundle
        supportFragmentManager!!.beginTransaction()
            .replace(R.id.main_content,fragment)
            .commit()
    }

    }


}

