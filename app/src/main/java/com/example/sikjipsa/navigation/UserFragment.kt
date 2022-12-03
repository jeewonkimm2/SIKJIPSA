package com.example.sikjipsa.navigation

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.sikjipsa.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_user.view.*

class UserFragment : Fragment(){

//    Initializing parameters and allocating values
    var fragmentView: View? = null
    var firestore: FirebaseFirestore? = null
    var uid : String? = null
    var auth: FirebaseAuth? = null
    var mDBRef: DatabaseReference = FirebaseDatabase.getInstance().reference
    private var watering: Button? = null
    private var mypostBtn: Button? = null
    private var name: String? = null
    private var editpic: Button? = null
    private var editnickname: Button? = null
    private var edittextnickname:TextView? = null
    private var donebtn: Button? = null
    private var imageUri : Uri? = null
    private var profile: ImageView? = null
    private var hellouser: TextView? = null

//    For profile picture change
    private val getContent =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result: ActivityResult ->
            if(result.resultCode == AppCompatActivity.RESULT_OK){
                imageUri = result.data?.data
                profile?.setImageURI(imageUri)
//                Deleting existing picture and setting new picture
                FirebaseStorage.getInstance().reference.child("profilepic").child(uid.toString()).delete().addOnSuccessListener {
                    FirebaseStorage.getInstance().reference.child("profilepic").child(uid.toString()).putFile(imageUri!!).addOnCanceledListener {
                        FirebaseStorage.getInstance().reference.child("profilepic").child(uid.toString()).downloadUrl.addOnSuccessListener {
                            val photoUri:Uri = it
                            Toast.makeText(requireContext(), "Profile picture is changed.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

//        Connecting actual vales
        fragmentView = LayoutInflater.from(activity).inflate(R.layout.fragment_user,container,false)
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        uid = auth?.currentUser?.uid
        mDBRef = FirebaseDatabase.getInstance().reference
        //        프로필편집 화면 전환
        name = fragmentView?.findViewById<TextView>(R.id.name).toString()
        watering = fragmentView?.findViewById(R.id.wateringBtn)
        mypostBtn = fragmentView?.findViewById(R.id.mypostBtn)
        editpic = fragmentView?.findViewById(R.id.editpic)
        editnickname = fragmentView?.findViewById(R.id.editnickname)
        edittextnickname = fragmentView?.findViewById(R.id.edittextnickname)
        donebtn = fragmentView?.findViewById(R.id.donebtn)
        profile = fragmentView?.findViewById<ImageView>(R.id.profile)
        hellouser = fragmentView?.findViewById<TextView>(R.id.hellouser)

//        Nickname for my page
        var nickname : String? = null
//        Receiving current user's nickname and set the screen
        mDBRef.child("user").child(uid.toString()).child("nickname").get().addOnSuccessListener {
            nickname = it.value.toString()
            Log.d("nickname","$nickname")
            fragmentView?.name?.text = nickname
            fragmentView?.hellouser?.text = "Hello, $nickname!"
        }

//        Logout
        fragmentView?.button_signout?.setOnClickListener{
            auth?.signOut()
            startActivity(Intent(activity, LoginActivity::class.java))
            activity?.finish()
        }

//        Set profile image
        val fs = FirebaseStorage.getInstance()
        fs.getReference().child("profilepic").child(uid.toString()).downloadUrl.addOnSuccessListener { it ->
            var imageUrl = it
            Glide.with(this).load(imageUrl).into(profile!!)
        }

//        Event if you click edit profile picture button
        editpic?.setOnClickListener {
            val intentImage = Intent(Intent.ACTION_PICK)
            intentImage.type = MediaStore.Images.Media.CONTENT_TYPE
            getContent.launch(intentImage)
        }

//        Event if you click edit nickname button
        editnickname?.setOnClickListener {
            editnickname?.visibility = INVISIBLE
            edittextnickname?.visibility = VISIBLE
            donebtn?.visibility = VISIBLE
        }

//        If you click done button after setting nickname
        donebtn?.setOnClickListener {
            val newnick:String = edittextnickname?.text.toString()
            mDBRef.child("user/${uid.toString()}/nickname").setValue("$newnick")
            var nickname : String? = null
//            Extracting current user's nickname from realtime database
            mDBRef.child("user").child(uid.toString()).child("nickname").get().addOnSuccessListener {
                nickname = it.value.toString()
                Log.d("nickname","$nickname")
                fragmentView?.name?.text = nickname
                fragmentView?.hellouser?.text = "Hello, $nickname!"
//                If you change your nickname, your nicknames on all posts are changed. Also the values of field in firestore are changed.
                firestore?.collection("images")?.get()?.addOnSuccessListener {
                        documents ->
                    for(document in documents){
                        var tmp:String = document.id.toString()
                        if(tmp.contains("$uid")){
                            firestore?.collection("images")?.document("$tmp")?.update("nickname", "$nickname")
                        }
                    }
                }
            }

            edittextnickname?.visibility = INVISIBLE
            donebtn?.visibility = INVISIBLE
            editnickname?.visibility = VISIBLE
        }

//        If you click My Post button, you are moving to MyPostActivity
        mypostBtn?.setOnClickListener {
            startActivity(Intent(context, MyPostActivity::class.java))
        }

        //        If you click Watering button, you are moving to MyPostActivity
        watering?.setOnClickListener{
            startActivity(Intent(context, WateringActivity::class.java))
        }


        return fragmentView

    }


}