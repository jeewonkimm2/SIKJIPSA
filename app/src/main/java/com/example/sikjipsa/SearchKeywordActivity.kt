package com.example.sikjipsa

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.sikjipsa.databinding.ActivitySearchBinding
import com.example.sikjipsa.databinding.ActivitySearchKeywordBinding
import kotlinx.android.synthetic.main.activity_search_keyword.*

class SearchKeywordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_keyword)
        val binding = ActivitySearchKeywordBinding.inflate(layoutInflater)

        binding.textView.text = intent.getStringExtra("keyword")

    }
}

