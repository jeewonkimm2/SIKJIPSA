package com.example.sikjipsa.navigation

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.sikjipsa.LoginActivity
import com.example.sikjipsa.R
import com.example.sikjipsa.WateringActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_user.view.*

class UserFragment : Fragment(){
    //수오 유저 로그아웃
    var fragmentView: View? = null
    var firestore: FirebaseFirestore? = null
    var uid : String? = null
    var auth: FirebaseAuth? = null
    var currentUserId: String? = null
    
    
    private var editBtn: Button? = null
    private var watering: Button? = null




    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //지훈 mypage view이름 수오가 fragmentView로 바꿈
        fragmentView = LayoutInflater.from(activity).inflate(R.layout.fragment_user,container,false)
        //수오
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        currentUserId = auth?.currentUser?.uid

        //if(uid == currentUserId){
            //내 계정일 때는 로그아웃
            fragmentView?.button_signout?.setOnClickListener{
                auth?.signOut()
                startActivity(Intent(activity, LoginActivity::class.java))
                activity?.finish()
            }
        //}
        
//        프로필편집 화면 전환
        editBtn = fragmentView?.findViewById(com.example.sikjipsa.R.id.editBtn)

//        editBtn.setOnClickListener {
//            val intent =
//                Intent(activity, EditProfileActivity::class.java) //fragment라서 activity intent와는 다른 방식
//            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
//            startActivity(intent)
//        }


        watering = fragmentView?.findViewById(R.id.wateringBtn)
        watering?.setOnClickListener{
            startActivity(Intent(context, WateringActivity::class.java))
        }



        return fragmentView


    }
}