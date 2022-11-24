package com.example.sikjipsa

import android.app.SearchManager
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.sikjipsa.databinding.ActivitySearchBinding
import com.example.sikjipsa.databinding.ActivitySearchKeywordBinding
import kotlinx.android.synthetic.main.activity_search_keyword.*

class SearchKeywordActivity : AppCompatActivity() {
    lateinit var binding:ActivitySearchKeywordBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_keyword)
        val binding = ActivitySearchKeywordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var keyword: String? = intent.getStringExtra("keyword")
        Log.d("ITM","$keyword")

        binding.button.setOnClickListener {
            var intent = Intent(Intent.ACTION_WEB_SEARCH)
            intent.putExtra(SearchManager.QUERY,"$keyword")
            startActivity(intent)
        }


    }
}

