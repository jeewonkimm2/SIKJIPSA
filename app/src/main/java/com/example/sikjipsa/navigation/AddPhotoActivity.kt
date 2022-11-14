package com.example.sikjipsa.navigation

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.*
import androidx.appcompat.app.AppCompatActivity
import com.example.sikjipsa.R
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_add_photo.*
import java.text.SimpleDateFormat
import java.util.*


class AddPhotoActivity: AppCompatActivity() {
    var PICK_IMAGE_FROM_ALBUM = 0
    var storage: FirebaseStorage? = null
    var photoUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_photo)

        //storage init
        storage = FirebaseStorage.getInstance()

        //open the album

        var launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
                result->
            if(result.resultCode == PICK_IMAGE_FROM_ALBUM){
                if(result.resultCode == Activity.RESULT_OK){
                    //This is path to the selected image
                    photoUri = result.data?.data
                    addphoto_image.setImageURI(photoUri)
                }else{
                    finish()

                }
            }
        }

        var photoPickerIntent= Intent(Intent.ACTION_PICK)
        photoPickerIntent.type = "image/*"
        with(launcher) {
            photoPickerIntent.type = "image/*"
            launch(photoPickerIntent)
        }

        //add image upload event
        addphoto_btn_upload.setOnClickListener {
            contentUpload()
        }

    }

    fun contentUpload(){
        var timestamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timestamp + "_.png"
        val storageRef = storage?.reference?.child("images")?.child(imageFileName)
        storageRef?.putFile(photoUri!!)?.addOnSuccessListener {
            Toast.makeText(this, "Upload Success", Toast.LENGTH_SHORT).show()
        }

    }}