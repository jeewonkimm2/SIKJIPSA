package com.example.sikjipsa

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import com.example.sikjipsa.databinding.ActivityLoginBinding
import com.example.sikjipsa.databinding.ActivityMainBinding
import com.example.sikjipsa.databinding.ActivitySearchBinding
import com.example.sikjipsa.navigation.AddPhotoActivity
import java.util.*

class SearchActivity : AppCompatActivity() {

    //    사진부분
    private lateinit var binding: ActivitySearchBinding

    //    파일 불러오기
    private val getContentImage =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri.let {
                binding.mainImg.setImageURI(uri)
            }
        }

    //    카메라 실행 후
    var pictureUri: Uri? = null
    private val getTakePicture = registerForActivityResult(ActivityResultContracts.TakePicture()) {
        if (it) {
            pictureUri.let { binding.mainImg.setImageURI(pictureUri) }
        }
    }

    //    카메라 실행 후 결과 비트맵 이미지 얻기
    private val getTakePicturePreview =
        registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
            bitmap.let {
                binding.mainImg.setImageBitmap(bitmap)
            }
        }
            val permissionList = arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )

            val requestMultiplePermission =
                registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { results ->
                    results.forEach {
                        if (!it.value) {
                            Toast.makeText(applicationContext, "권한 허용 필요", Toast.LENGTH_SHORT)
                                .show()
                            finish()
                        }
                    }
                }
//여기까지

            override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)
                setContentView(R.layout.activity_search)

                val binding = ActivitySearchBinding.inflate(layoutInflater)
                setContentView(binding.root)


//        사진부분
                requestMultiplePermission.launch(permissionList)




                binding.importBtn.setOnClickListener {
//            startActivity(Intent(this, SearchImageActivity::class.java))
                    val items = arrayOf("Camera", "Album")
                    val builder = AlertDialog.Builder(this)
                        .setTitle("Import from")
                        .setItems(items) { dialog, which ->
                            when (items[which]) {
                                "Camera" ->
                                    getTakePicture.launch(createImageFile())
                                "Album" ->
                                    getContentImage.launch("album/*")
//                        모델 넣고 결과값 추출해야함
                            }
                        }.show()
                }

                //카메라 Bitmap얻음
//                val bitmap = getTakePicturePreview.launch(null)
//                Log.d("bitmap","$bitmap")


                binding.keywordBtn.setOnClickListener {
                    startActivity(Intent(this, SearchKeywordActivity::class.java))
                }

            }

    private fun createImageFile(): Uri? {
        val now = SimpleDateFormat("yyMMdd_HHmmss").format(Date())
        val content = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "img_$now.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpg")
        }
        return contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, content)
    }
        }

