package com.example.sikjipsa

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.sikjipsa.R
import com.example.sikjipsa.navigation.DetailViewFragment

class EditProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        initView()
    }

    private fun initView() {
        supportFragmentManager.beginTransaction().add(R.id.main_content, DetailViewFragment())
            .commit()

    }

}