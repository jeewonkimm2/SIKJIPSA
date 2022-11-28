package com.example.sikjipsa.navigation

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.sikjipsa.R
import com.example.sikjipsa.SignUpActivity
import com.example.sikjipsa.User
import com.example.sikjipsa.model.ContentDTO
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_add_photo.*
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlinx.android.synthetic.main.activity_sign_up.view.*
import kotlinx.android.synthetic.main.item_detail.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class AddPhotoActivity: AppCompatActivity() {
    var PICK_IMAGE_FROM_ALBUM = 0
    var storage: FirebaseStorage? = null
    var photoUri: Uri? = null
    var auth: FirebaseAuth? = null
    var firestore: FirebaseFirestore? = null
    //    DB객체
    private lateinit var mDBRef: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_photo)

        //storage initiate
        storage = FirebaseStorage.getInstance()
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        //        DB 초기화
        mDBRef = Firebase.database.reference


        //open the album
        var photoPickerIntent= Intent(Intent.ACTION_PICK)
        photoPickerIntent.type = "image/*"
        startActivityForResult(photoPickerIntent,PICK_IMAGE_FROM_ALBUM)


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
//                addphoto_image.setImageURI(photoUri)
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
//        val storageRef = storage?.reference?.child("images")?.child(imageFileName)
        val storageRef = storage?.reference?.child("images")?.child("${auth?.currentUser?.uid.toString()},${timestamp.toString()}")


        //서버 stoarge(DB)에 파일 업로드
        storageRef?.putFile(photoUri!!)?.continueWithTask { task:Task<UploadTask.TaskSnapshot> ->
            return@continueWithTask storageRef.downloadUrl
        }?.addOnSuccessListener { uri->
            Toast.makeText(this, "success", Toast.LENGTH_SHORT).show()

            //
            var contentDTO = ContentDTO()

            // 이미지 주소 넣어주기
            contentDTO.imageUrl = uri.toString()

            // 유저 uid 넣어주기
            contentDTO.uid = auth?.currentUser?.uid

            // 유저 아이디 넣어주기(이메일)
            contentDTO.userId = auth?.currentUser?.email

            // 유저가 입력한 설명(글) 넣어주기
            contentDTO.explain = addphoto_edit_explain.text.toString()

            var nickname:String
//            닉네임 추가 수정중
            mDBRef.child("user").child(auth?.currentUser?.uid.toString()).child("nickname").get().addOnSuccessListener {
                nickname = it.value.toString()
                Log.d("debugnickname","$nickname")
                contentDTO.nickname = nickname
                Log.d("debugnickname","${contentDTO.nickname}")
            }

            // 타임스태프 넣어주기
            contentDTO.timestamp = System.currentTimeMillis()

            // 값 넘겨주기
            firestore?.collection("images")?.document("${contentDTO.uid.toString()}${contentDTO.timestamp.toString()}")?.set(contentDTO)


            setResult(RESULT_OK)
            finish()
            }


        }

    }



