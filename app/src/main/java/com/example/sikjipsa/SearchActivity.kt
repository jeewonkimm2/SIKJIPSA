package com.example.sikjipsa

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import com.example.sikjipsa.databinding.ActivityLoginBinding
import com.example.sikjipsa.databinding.ActivityMainBinding
import com.example.sikjipsa.databinding.ActivitySearchBinding
import com.example.sikjipsa.navigation.AddPhotoActivity

class SearchActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.importBtn.setOnClickListener {
//            startActivity(Intent(this, SearchImageActivity::class.java))
            val items = arrayOf("Camera", "Album")
            val builder = AlertDialog.Builder(this)
                .setTitle("Select")
                .setItems(items){dialog,which->
                    when(items[which]){
                        "Camera" -> Log.d("ITM","Camera")
                        "Album" -> Log.d("ITM", "Album")
                    }
                }.show()

        }

        binding.keywordBtn.setOnClickListener {
            startActivity(Intent(this,SearchKeywordActivity::class.java))
        }

    }
}