package com.example.sikjipsa

import android.content.Intent
import android.content.pm.PackageManager
import android.icu.text.SimpleDateFormat
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.example.sikjipsa.databinding.ActivityMainBinding
import com.example.sikjipsa.databinding.ActivityMyPostBinding
//import com.example.sikjipsa.databinding.FragmentGridBinding
import com.example.sikjipsa.navigation.*
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.lang.Exception
import java.net.URL
import java.util.*

class MyPostActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_post)
        lateinit var binding: ActivityMyPostBinding
        binding = ActivityMyPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()

        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
            1
        )
    }

    private fun initView() {
        supportFragmentManager.beginTransaction().add(R.id.main_content, DetailViewForMyPostFragment())
            .commit()
    }






}

