package com.example.sikjipsa

import android.Manifest
import android.annotation.SuppressLint
import android.app.SearchManager
import android.content.Intent
import android.graphics.*
import android.graphics.Bitmap.createBitmap
import android.graphics.Bitmap.createScaledBitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.MediaStore.Images.Media.getBitmap
import android.util.Log
import android.view.View.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import com.example.sikjipsa.databinding.ActivitySearchBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.ml.modeldownloader.CustomModel
import com.google.firebase.ml.modeldownloader.CustomModelDownloadConditions
import com.google.firebase.ml.modeldownloader.DownloadType
import com.google.firebase.ml.modeldownloader.FirebaseModelDownloader
import kotlinx.android.synthetic.main.activity_add_photo.*
import kotlinx.android.synthetic.main.activity_main.*
import org.tensorflow.lite.Interpreter
import java.io.*
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.*


class SearchActivity : AppCompatActivity() {
    var PICK_IMAGE_FROM_ALBUM = 0
    var photoURI: Uri? = null
    var keywordFromModel:String? = null
    var probFromModel:kotlin.Float = 0.0f

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





        binding.imageBtn.setOnClickListener {

            var photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            startActivityForResult(photoPickerIntent, PICK_IMAGE_FROM_ALBUM)

            binding.imageBtn.visibility = INVISIBLE
            binding.GOOGLEImg.visibility = VISIBLE
            binding.SIKJIPSAImg.visibility = VISIBLE
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

    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (requestCode == PICK_IMAGE_FROM_ALBUM) {
        if (resultCode == AppCompatActivity.RESULT_OK) {
            photoURI = data?.data
            var bitmapFinal:Bitmap?=null

            try{
                photoURI?.let{
                    if(Build.VERSION.SDK_INT<28){
                        val bitmap = MediaStore.Images.Media.getBitmap(
                            contentResolver,
                            photoURI
                        )
//                            Log.d("probabilities","1 : $bitmap")
                        bitmapFinal = bitmap
//                            Log.d("probabilities","result 1 : $bitmapFinal")

                    }else{
                        val source = ImageDecoder.createSource(contentResolver,
                            photoURI!!
                        )
                        val bitmap = ImageDecoder.decodeBitmap(source)
//                            Log.d("probabilities","2 : $bitmap")
                        bitmapFinal = bitmap
//                            Log.d("probabilities","result 2 : $bitmapFinal")
                    }

                }





                val conditions = CustomModelDownloadConditions.Builder()
                    .requireWifi()
                    .build()
                FirebaseModelDownloader.getInstance()
                    .getModel("model_fp16", DownloadType.LOCAL_MODEL_UPDATE_IN_BACKGROUND,
                        conditions)
                    .addOnSuccessListener { model: CustomModel? ->
                        var interpreter: Interpreter? = null
                        val modelFile = model?.file

                        if (modelFile != null) {
                            interpreter = Interpreter(modelFile)
                            Log.d("probabilities", "model import done")

                            val plant = getResources().getDrawable(R.drawable.monstera)
                            Log.d("probabilities","$plant")

                            Log.d("probabilities","picture import done")

                            Log.d("probabilities","${photoURI}")
                            Log.d("probabilities","${bitmapFinal}")


                            val bitmap = Bitmap.createScaledBitmap(plant.toBitmap(), 224, 224, true)

                            Log.d("probabilities","picture import done")
                            val input = ByteBuffer.allocateDirect(224*224*3*4).order(ByteOrder.nativeOrder())
                            for (y in 0 until 224) {
                                for (x in 0 until 224) {
                                    val px = bitmap.getPixel(x, y)

                                    // Get rgb
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
                            Log.d("probabilities","model is running")
                            interpreter?.run(input, modelOutput)
                            Log.d("probabilities","model running done")


                            modelOutput.rewind()
                            val probabilities = modelOutput.asFloatBuffer()


                            try {
                                Log.d("probabilities","ready to show result")
                                val reader = BufferedReader(
                                    InputStreamReader(assets.open("labels.txt")))
                                var bestLabel: String? = null
                                var bestProb: kotlin.Float = 0.0f
                                for (i in 0 until probabilities.capacity()) {
                                    val label: String = reader.readLine()
                                    val probability = probabilities.get(i)
                                    if(bestProb < probability){
                                        bestProb = probability
                                        bestLabel = label
                                    }
//                                Log.d("probabilities","$label: $probability")
                                }
//                            Log.d("probabilities","It is $bestLabel")
                                probFromModel = bestProb
                                keywordFromModel = bestLabel

//                        val snack = Snackbar.make(it, "$keywordFromModel with $probFromModel%.", Snackbar.LENGTH_SHORT)
//                        snack.show()

                                binding.GOOGLEImg.setOnClickListener {
                                    var intent = Intent(Intent.ACTION_WEB_SEARCH)
                                    intent.putExtra(SearchManager.QUERY, keywordFromModel)
                                    startActivity(intent)
                                }
                            } catch (e: IOException) {
                                // File not found?
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
    }





}

