package com.example.sikjipsa.navigation

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import com.example.sikjipsa.R
import kotlinx.android.synthetic.main.fragment_user.*


class UserFragment : Fragment() {

    private var editBtn: Button? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = LayoutInflater.from(activity).inflate(R.layout.fragment_user, container, false)

//        프로필편집 화면 전환
        editBtn = view.findViewById(com.example.sikjipsa.R.id.editBtn)
//        editBtn.setOnClickListener {
//            val intent =
//                Intent(activity, EditProfileActivity::class.java) //fragment라서 activity intent와는 다른 방식
//            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
//            startActivity(intent)
//        }
        return view
    }
}