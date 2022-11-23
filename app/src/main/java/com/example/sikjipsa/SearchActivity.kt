package com.example.sikjipsa

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.ImageDecoder
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import com.example.sikjipsa.databinding.ActivitySearchBinding
import com.google.firebase.ml.modeldownloader.CustomModel
import com.google.firebase.ml.modeldownloader.CustomModelDownloadConditions
import com.google.firebase.ml.modeldownloader.DownloadType
import com.google.firebase.ml.modeldownloader.FirebaseModelDownloader
import kotlinx.android.synthetic.main.activity_add_photo.*
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
                        addphoto_image.setImageURI(photoURI)
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


//        모델 입력 출력 결과
//        1 input(s):
//        [  1 224 224   3] <class 'numpy.uint8'>
//
//        1 output(s):
//        [ 1 18] <class 'numpy.uint8'>

        var interpreter:Interpreter?=null

//        모델 다운, 인터프리터 초기화
        val conditions = CustomModelDownloadConditions.Builder()
            .requireWifi()  // Also possible: .requireCharging() and .requireDeviceIdle()
            .build()
        FirebaseModelDownloader.getInstance()
            .getModel("flowerClassifier", DownloadType.LOCAL_MODEL_UPDATE_IN_BACKGROUND,
                conditions)
            .addOnSuccessListener { model: CustomModel? ->
                val modelFile = model?.file
                if (modelFile != null) {
                    Log.d("tflite성공","모델 import성공")
                    interpreter = Interpreter(modelFile)
                }
            }


        val plant = getResources().getDrawable(R.drawable.monstera)
        val bitmap = Bitmap.createScaledBitmap(plant.toBitmap(), 224, 224, true)

//        Log.d("typecheck","${bitmap.javaClass.name}")
//        android.graphics.Bitmap

//        val input = ByteBuffer.allocateDirect(224*224*3*4).order(ByteOrder.nativeOrder())
        val input = Array(1) { Array(224) { Array(224) { IntArray(3) } } }

        val batchNum = 0
            for (x in 0 until 223) {
                for (y in 0 until 223) {
                    val pixel: Int = bitmap.getPixel(x, y)
                    input[batchNum][x][y][0] = Color.red(pixel)
                    input[batchNum][x][y][1] = Color.green(pixel)
                    input[batchNum][x][y][2] = Color.blue(pixel)
                }
            }

//        for (y in 0 until 224) {
//            for (x in 0 until 224) {
//                val px = bitmap.getPixel(x, y)
//
//                // Get channel values from the pixel value.
//                val r:Int = Color.red(px)
//                val g:Int = Color.green(px)
//                val b:Int = Color.blue(px)
//
//                // Normalize channel values to [-1.0, 1.0]. This requirement depends on the model.
//                // For example, some models might require values to be normalized to the range
//                // [0.0, 1.0] instead.
////                val rf = (r - 127) / 255
////                val gf = (g - 127) / 255
////                val bf = (b - 127) / 255
////
////                input.putInt(rf)
////                input.putInt(gf)
////                input.putInt(bf)
//                input.putInt(r)
//                input.putInt(g)
//                input.putInt(b)
//
//            }
//        }



//        val bufferSize = 18 * Integer.SIZE / java.lang.Byte.SIZE
//        val modelOutput = ByteBuffer.allocateDirect(bufferSize).order(ByteOrder.nativeOrder())
        val output = Array(1) { IntArray(18) }

        interpreter?.run(input, output)
        Log.d("tflite성공","$input $output")
        for(i in 0..17){
            Log.d("tflite성공","${output[0][i]}")
        }

//        Log.d("tflite성공","$input $modelOutput")
//        for(i in 0 until 71){
//            Log.d("tflite성공","${modelOutput[i]}")
//        }



//        modelOutput.rewind()
////        probabilties.get(19)부터 에러
//        val probabilities = modelOutput.asIntBuffer()
//
//        try {
////            라벨 읽기
////            reader.readLine() : 라벨읽음
//            val reader = BufferedReader(
//                InputStreamReader(assets.open("labels.txt")))
//            for (i in 1 until probabilities.capacity()) {
//                val label: String = reader.readLine()
//                val probability = probabilities.get(i)
//                Log.d("출력", "$label: $probability")
////                println("$label: $probability")
//            }
//        } catch (e: IOException) {
//            // File not found?
//            Log.d("출력", "File not found?")
//        }



















//        여기서부터새로시작
//        val input = Array(1) { Array(224) { Array(224) { IntArray(3) } } }
//        val output = Array(1) { IntArray(18) }



//        val batchNum = 0
//            for (x in 0 until 223) {
//                for (y in 0 until 223) {
//                    val pixel: Int = bitmap.getPixel(x, y)
//                    input[batchNum][x][y][0] = Color.red(pixel)
//                    input[batchNum][x][y][1] = Color.green(pixel)
//                    input[batchNum][x][y][2] = Color.blue(pixel)
//                }
//            }
//            ----여기까진 문제 없음




//        val batchNum = 0
//        for (x in 0 until 223) {
//            for (y in 0 until 223) {
//                val pixel: Int = bitmap.getPixel(x, y)
//                input[batchNum][x][y][0] = Color.red(pixel)
//                input[batchNum][x][y][1] = Color.green(pixel)
//                input[batchNum][x][y][2] = Color.blue(pixel)
//            }
//        }
//
//        //        var interpreter: Interpreter?=null
//        val conditions = CustomModelDownloadConditions.Builder()
//            .requireWifi()  // Also possible: .requireCharging() and .requireDeviceIdle()
//            .build()
//        FirebaseModelDownloader.getInstance()
//            .getModel("flowerClassifier", DownloadType.LOCAL_MODEL_UPDATE_IN_BACKGROUND,
//                conditions)
//            .addOnSuccessListener {
//                    model: CustomModel? ->
//                val modelFile = model?.file
//                var interpreter:Interpreter?=null
//                if (modelFile != null) {
//                    Log.d("tflite성공","모델 import성공")
//                    interpreter = Interpreter(modelFile)
////                    interpreter.run(input,output)
////                    Log.d("tflite성공","interpreter run성공")
////                    for(i in 0 until 17){
////                        Log.d("tflite성공","${output[0][i]}")
////                    }
//
//                }
//            }










    }
}