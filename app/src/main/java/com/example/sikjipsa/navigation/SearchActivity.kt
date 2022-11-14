package com.example.sikjipsa.navigation

import android.content.ContentValues
import android.content.Context
import android.icu.text.SimpleDateFormat
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import com.example.sikjipsa.R
import com.example.sikjipsa.databinding.FragmentGridBinding
import java.util.*

class SearchActivity : AppCompatActivity() {

//            지원수정 - 사진불러오기
    lateinit var binding2: FragmentGridBinding

    private val getContentImage = registerForActivityResult(ActivityResultContracts.GetContent()){
        uri -> uri.let {binding2.mainImg.setImageURI(uri)}
    }
    var pictureUri: Uri? = null
    private val getTakePicture = registerForActivityResult(ActivityResultContracts.TakePicture()){
        if(it){
            pictureUri.let {binding2.mainImg.setImageURI(pictureUri)}
            }
    }
    private val getTakePicturePreview = registerForActivityResult(ActivityResultContracts.TakePicturePreview()){
        bitmap -> bitmap.let{binding2.mainImg.setImageBitmap(bitmap)}
    }
//    여기까지 새로 추가
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
    }



        private fun openDialog(context: Context) {
        val dialogLayout = layoutInflater.inflate(R.layout.dialog_select_image, null)
        val dialogBuild = AlertDialog.Builder(context).apply {
            setView(dialogLayout)
        }
        val dialog = dialogBuild.create().apply { show() }

        val cameraAddBtn = dialogLayout.findViewById<Button>(R.id.dialog_btn_camera)
        val fileAddBtn = dialogLayout.findViewById<Button>(R.id.dialog_btn_file)

        cameraAddBtn.setOnClickListener {
            // 1. TakePicture
            pictureUri = createImageFile()
            getTakePicture.launch(pictureUri)   // Require Uri

            // 2. TakePicturePreview
//            getTakePicturePreview.launch(null)    // Bitmap get

            dialog.dismiss()
        }
        fileAddBtn.setOnClickListener {
            getContentImage.launch("image/*")
            dialog.dismiss()
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