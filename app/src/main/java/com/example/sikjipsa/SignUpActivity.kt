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

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    var storage: FirebaseStorage? = null
    var firestore: FirebaseFirestore? = null
    var PICK_IMAGE_FROM_ALBUM = 0
    var photoURI: Uri? = null
    lateinit var mAuth: FirebaseAuth
    private lateinit var mDBRef: DatabaseReference
    //    Permission list
    val PERMISSIONS = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )
    val PERMISSIONS_REQUEST = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        val binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        Initiazlizing values
        mAuth = FirebaseAuth.getInstance()
        mDBRef = Firebase.database.reference
        storage = FirebaseStorage.getInstance()
        firestore = FirebaseFirestore.getInstance()

//        If you click profile picture, you can set profile image importing from album
        binding.pic.setOnClickListener {
            var photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            startActivityForResult(photoPickerIntent, PICK_IMAGE_FROM_ALBUM)
        }

//        If you click after inputting all the personal information
        binding.signup.setOnClickListener {
            val name = binding.nameEdit.text.toString()
            val email = binding.registerEmail.text.toString()
            val password = binding.registerPassword.text.toString()
            val nickname = binding.registerNickname.text.toString()
//            If there is no empty value, they can signup finally
            if(email.isNotEmpty()&&password.isNotEmpty()&&nickname.isNotEmpty()&&name.isNotEmpty()){
                signUp(name,email,password,nickname)
            }
        }

    }

//    Getting image from album
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

//    If you are signed up, your information will be stored in realtime database
    private fun signUp(name: String, email: String, password: String, nickname:String){
    mAuth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                // signup success
                Toast.makeText(this, "Welcome to our app!", Toast.LENGTH_SHORT).show()
                val intent: Intent = Intent(this, MainActivity::class.java)
//                Adding personal information in realtime database
                addUserToDatabase(name, email, mAuth.currentUser?.uid!!, nickname)
                startActivity(intent)
            } else {
                // signup fail
                Toast.makeText(this, "Something is wrong", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun addUserToDatabase(name: String, email: String, uid:String,nickname:String){
        //Getting currentuser's uid
        var uid = mAuth.currentUser?.uid
        mDBRef.child("user").child(uid.toString()).setValue(User(name,email,uid,nickname))
//        Uploading profile picture on storage
        val storageRef = storage?.reference?.child("profilepic")?.child(uid.toString())
        storageRef?.putFile(photoURI!!)?.continueWithTask { task:Task<UploadTask.TaskSnapshot> ->
            return@continueWithTask storageRef.downloadUrl
        }?.addOnSuccessListener { uri->
//                    Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show()
            //putting profile image into ContentDTO
            var contentDTO = ContentDTO()
            contentDTO.profile = uri.toString()
            setResult(RESULT_OK)
        }
    }
}