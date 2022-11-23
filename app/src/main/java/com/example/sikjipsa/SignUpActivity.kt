package com.example.sikjipsa

import android.Manifest
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.sikjipsa.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_add_photo.*
import kotlinx.android.synthetic.main.activity_sign_up.*
import java.util.*

class SignUpActivity : AppCompatActivity() {


    private lateinit var binding: ActivitySignUpBinding

    val PERMISSIONS = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

    var PICK_IMAGE_FROM_ALBUM = 0
    var photoURI: Uri? = null

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



        binding.pic.setOnClickListener {
            var photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            startActivityForResult(photoPickerIntent, PICK_IMAGE_FROM_ALBUM)
            Log.d("Type","what")

        }

        binding.signup.setOnClickListener {
            val name = binding.nameEdit.text.toString()
            val email = binding.registerEmail.text.toString()
            val password = binding.registerPassword.text.toString()
            val nickname = binding.registerNickname.text.toString()
            var pic:String = photoURI.toString()
            //여기서 사진 파일로 추가해야됨
            if(email.isNotEmpty()&&password.isNotEmpty()){
                signUp(name,email,password,nickname,pic)

            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_FROM_ALBUM) {
            if (resultCode == RESULT_OK) {
                photoURI = data?.data
                Log.d("Type","${photoURI.toString()}")
//                        pic = photoURI
            } else {
                Log.d("Type","fail")
                finish()
            }
        }
    }




//    회원가입 : password 최소 6자
    private fun signUp(name: String, email: String, password: String, nickname:String, pic:String){
    mAuth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                // success
                Toast.makeText(this, "Welcome to our app!", Toast.LENGTH_SHORT).show()
                val intent: Intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
//                DB추가
//                addUserToDatabase(name, email, mAuth.currentUser?.uid!!)
                addUserToDatabase(name,email,password,nickname, pic)
            } else {
                // fail
                Toast.makeText(this, "Something is wrong", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun addUserToDatabase(name: String, email: String, password: String,nickname:String,pic:String){
        mDBRef.child("user").child(name).setValue(User(name,email,password,nickname,pic))
    }


}