package com.example.sikjipsa


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import com.example.sikjipsa.databinding.ActivityMyPostBinding
import com.example.sikjipsa.navigation.*

class MyPostActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_post)
        lateinit var binding: ActivityMyPostBinding
        binding = ActivityMyPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()

//        Allowing permission
        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
            1
        )
    }

//    Initializing with DetailViewForMyPostFragment
    private fun initView() {
        supportFragmentManager.beginTransaction().add(R.id.main_content, DetailViewForMyPostFragment())
            .commit()
    }


}

