package com.example.sikjipsa.navigation

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.sikjipsa.R
import com.example.sikjipsa.model.ContentDTO
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_add_photo.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class AddPhotoActivity: AppCompatActivity() {
    var PICK_IMAGE_FROM_ALBUM = 0
    var storage: FirebaseStorage? = null
    var photoUri: Uri? = null
    var auth: FirebaseAuth? = null
    var firestore: FirebaseFirestore? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_photo)

        //storage init
        storage = FirebaseStorage.getInstance()
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        //open the album
/*        var launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
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
*/

        //open the album
        var photoPickerIntent= Intent(Intent.ACTION_PICK)
        photoPickerIntent.type = "image/*"
        startActivityForResult(photoPickerIntent,PICK_IMAGE_FROM_ALBUM)
/*        with(launcher) {
            photoPickerIntent.type = "image/*"
            launch(photoPickerIntent)
        }
*/*/

        //버튼 이벤트
        addphoto_btn_upload.setOnClickListener {
            contentUpload()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == PICK_IMAGE_FROM_ALBUM){
            if (resultCode == Activity.RESULT_OK){
                photoUri = data?.data
                addphoto_image.setImageURI(photoUri)
            }
            else{
                finish()
            }
        }

    }


    //사진을 Storage에 업로드
    fun contentUpload(){
        var timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "JPEG_" + timestamp + "_.png"
        val storageRef = storage?.reference?.child("images")?.child(imageFileName)

        //파일 업로드
        storageRef?.putFile(photoUri!!)?.continueWithTask { task:Task<UploadTask.TaskSnapshot> ->
            return@continueWithTask storageRef.downloadUrl
        }?.addOnSuccessListener { uri->
            Toast.makeText(this, "success", Toast.LENGTH_SHORT).show()

            var contentDTO = ContentDTO()

            // 이미지 주소 넣어주기
            contentDTO.imageUrl = uri.toString()

            // 유저 uid 넣어주기
            contentDTO.uid = auth?.currentUser?.uid

            // 유저 아이디 넣어주기
            contentDTO.userId = auth?.currentUser?.email

            // 설명 넣어주기
            contentDTO.explain = addphoto_edit_explain.text.toString()

            // 타임스태프 넣어주기
            contentDTO.timestamp = System.currentTimeMillis()

            // 값 넘겨주기
            firestore?.collection("images")?.document()?.set(contentDTO)

            setResult(Activity.RESULT_OK)
            finish()


        }

    }

}

