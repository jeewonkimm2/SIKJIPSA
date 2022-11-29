package com.example.sikjipsa

import android.Manifest
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.sikjipsa.databinding.ActivitySignUpBinding
import com.example.sikjipsa.model.ContentDTO
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import java.util.*

class SignUpActivity : AppCompatActivity() {


    private lateinit var binding: ActivitySignUpBinding
    var storage: FirebaseStorage? = null
    var firestore: FirebaseFirestore? = null


    val PERMISSIONS = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

    var PICK_IMAGE_FROM_ALBUM = 0
    var photoURI: Uri? = null

    private lateinit var currentuser:FirebaseUser

    val PERMISSIONS_REQUEST = 100


//    바인딩 객체
//    Firebase 객체
    lateinit var mAuth: FirebaseAuth
    //    DB객체
    private lateinit var mDBRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        val binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        인증 초기화

        mAuth = FirebaseAuth.getInstance()
        //        DB 초기화
        mDBRef = Firebase.database.reference

        storage = FirebaseStorage.getInstance()

        firestore = FirebaseFirestore.getInstance()

        //등록 과정에서 충돌나는거 해결(수오) 여기서 밑에 코드 선언하면 null이어서 등록이 안됨
        //currentuser = mAuth.currentUser!!


        binding.pic.setOnClickListener {
            var photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            startActivityForResult(photoPickerIntent, PICK_IMAGE_FROM_ALBUM)
        }

        binding.signup.setOnClickListener {
            val name = binding.nameEdit.text.toString()
            val email = binding.registerEmail.text.toString()
            val password = binding.registerPassword.text.toString()
            val nickname = binding.registerNickname.text.toString()
            //여기서 사진 파일로 추가해야됨
            if(email.isNotEmpty()&&password.isNotEmpty()){
                signUp(name,email,password,nickname)
            }




        }



    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_FROM_ALBUM) {
            if (resultCode == RESULT_OK) {
                photoURI = data?.data

            } else {
                Log.d("Type","fail")
                finish()
            }
        }
    }




//    회원가입 : password 최소 6자
    private fun signUp(name: String, email: String, password: String, nickname:String){
    mAuth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                // success
                Toast.makeText(this, "Welcome to our app!", Toast.LENGTH_SHORT).show()
                val intent: Intent = Intent(this, MainActivity::class.java)
//                DB추가
                addUserToDatabase(name, email, mAuth.currentUser?.uid!!, nickname)
                startActivity(intent)
            } else {
                // fail
                Toast.makeText(this, "Something is wrong", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun addUserToDatabase(name: String, email: String, uid:String,nickname:String){
/*        var uid = currentuser.uid
        mDBRef.child("user").child(uid).setValue(User(name,email,uid,nickname))
//        Uploading profile picture on storage
        val storageRef = storage?.reference?.child("profilepic")?.child(uid)
        storageRef?.putFile(photoURI!!)?.continueWithTask { task:Task<UploadTask.TaskSnapshot> ->
            return@continueWithTask storageRef.downloadUrl
        }?.addOnSuccessListener { uri->
//                    Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show()
            setResult(RESULT_OK)
        }
*/

        //기존 지원이 코드 위에거에서 수오가 재수정함
        var uid = mAuth.currentUser?.uid
        mDBRef.child("user").child(uid.toString()).setValue(User(name,email,uid,nickname))
//        Uploading profile picture on storage
        val storageRef = storage?.reference?.child("profilepic")?.child(uid.toString())
        storageRef?.putFile(photoURI!!)?.continueWithTask { task:Task<UploadTask.TaskSnapshot> ->
            return@continueWithTask storageRef.downloadUrl
        }?.addOnSuccessListener { uri->
//                    Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show()
            //프로필 이미지를 ContentDTO에 넣기
            var contentDTO = ContentDTO()
            contentDTO.profile = uri.toString()
            setResult(RESULT_OK)
        }

    }
}