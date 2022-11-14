package com.example.sikjipsa

import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.icu.text.SimpleDateFormat
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.sikjipsa.databinding.ActivityMainBinding
import com.example.sikjipsa.databinding.FragmentGridBinding
import com.example.sikjipsa.navigation.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView
import com.google.android.material.navigation.NavigationView
import java.util.*
import java.util.jar.Manifest
//수오가 침
//지원
//후니
class MainActivity : AppCompatActivity(){




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //BottomNavigation
        initView()
        initBottomNavigation()

        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),1)
    }


    //초기 화면 action_home
    private fun initView(){
        supportFragmentManager.beginTransaction().add(R.id.main_content,DetailViewFragment()).commit()

    }    private fun initBottomNavigation(){
        binding.bottomNavigation.setOnItemSelectedListener { p0->
            when(p0.itemId){
                R.id.action_home->{
                    var detailViewFragment = DetailViewFragment()
                    supportFragmentManager.beginTransaction().replace(R.id.main_content,detailViewFragment).commit()
                    return@setOnItemSelectedListener true
                }
                R.id.action_search -> {
                    var gridFragment = GridFragment()
                    supportFragmentManager.beginTransaction().replace(R.id.main_content,gridFragment).commit()
                    return@setOnItemSelectedListener true
                }
                R.id.action_add_photo -> {

                    if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.READ_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED){
                        startActivity(Intent(this,AddPhotoActivity::class.java))
                    }
                    return@setOnItemSelectedListener true
                }
                R.id.action_favorite_alarm -> {
                    var alarmFragment = AlarmFragment()
                    supportFragmentManager.beginTransaction().replace(R.id.main_content,alarmFragment).commit()
                    return@setOnItemSelectedListener true
                }
                R.id.action_account -> {
                    var userFragment = UserFragment()
                    supportFragmentManager.beginTransaction().replace(R.id.main_content,userFragment).commit()
                    return@setOnItemSelectedListener true
                }

                else->false
            }
        }
    }



    lateinit var binding: ActivityMainBinding





}

