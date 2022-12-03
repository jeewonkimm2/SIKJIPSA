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
    //    Realtime DB object
    private lateinit var mDBRef: DatabaseReference

    var nickname:String? = null




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_photo)

        //storage initiate
        storage = FirebaseStorage.getInstance()
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        // DB object
        mDBRef = Firebase.database.reference
        //Get nicknames in DB
        mDBRef.child("user").child(auth?.currentUser?.uid.toString()).child("nickname").get().addOnSuccessListener {
            nickname = it.value.toString()
            Log.d("debugnickname","$nickname")
        }


        //Open the album
        var photoPickerIntent= Intent(Intent.ACTION_PICK)
        photoPickerIntent.type = "image/*"
        startActivityForResult(photoPickerIntent,PICK_IMAGE_FROM_ALBUM)


        //Run a function contentUpload() when a button is pressed
        addphoto_btn_upload.setOnClickListener {
            contentUpload()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //Put pictures in addphoto_image if selected from album
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


    //Upload picture in Storage
    fun contentUpload(){
        var timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageRef = storage?.reference?.child("images")?.child("${auth?.currentUser?.uid.toString()},${timestamp.toString()}")


        //upload data in stoarge(DB)
        storageRef?.putFile(photoUri!!)?.continueWithTask { task:Task<UploadTask.TaskSnapshot> ->
            return@continueWithTask storageRef.downloadUrl
        }?.addOnSuccessListener { uri->
            Toast.makeText(this, "success", Toast.LENGTH_SHORT).show()

            //Data Class Declaration
            var contentDTO = ContentDTO()

            //Enter Image Address
            contentDTO.imageUrl = uri.toString()

            //Enter user uid
            contentDTO.uid = auth?.currentUser?.uid

            //Enter user ID (email)
            contentDTO.userId = auth?.currentUser?.email

            //Enter user-entered description (writing)
            contentDTO.explain = addphoto_edit_explain.text.toString()

            //Enter nickname
            contentDTO.nickname = nickname

            //Enter timestamp
            contentDTO.timestamp = System.currentTimeMillis()

            //Hand over values to the Firestore
            firestore?.collection("images")?.document("${contentDTO.uid.toString()}${contentDTO.timestamp.toString()}")?.set(contentDTO)


            setResult(RESULT_OK)
            finish()
            }


        }

    }



