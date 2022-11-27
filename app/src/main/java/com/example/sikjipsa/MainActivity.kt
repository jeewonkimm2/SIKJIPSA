package com.example.sikjipsa

import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.sikjipsa.databinding.ActivityMainBinding
//import com.example.sikjipsa.databinding.FragmentGridBinding
import com.example.sikjipsa.navigation.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.lang.Exception
import java.net.URL
import java.util.*
import java.util.jar.Manifest

//수오 브랜치 재확인
//지원
//후니test
//지원브랜치테스트
class MainActivity : AppCompatActivity() {

    val CITY: String = "seoul,kr"
    val API: String = "a5b5e79e80276333510e3446c4d3498b"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //BottomNavigation
        initView()
        initBottomNavigation()

        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
            1
        )

        //set default screen (메인 화면에 DetailViewFragment 뜨도록)
        bottom_navigation.selectedItemId = R.id.action_home
    }


    //초기 화면 action_home
    private fun initView() {
        supportFragmentManager.beginTransaction().add(R.id.main_content, DetailViewFragment())
            .commit()

    }











    private fun initBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { p0 ->
            when (p0.itemId) {
                R.id.action_home -> {
                    var detailViewFragment = DetailViewFragment()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_content, detailViewFragment).commit()
                    return@setOnItemSelectedListener true
                }
                R.id.action_search -> {
                    startActivity(Intent(this, SearchActivity::class.java))
                    return@setOnItemSelectedListener true
                }
                R.id.action_add_photo -> {
                    if (ContextCompat.checkSelfPermission(
                            this,
                            android.Manifest.permission.READ_EXTERNAL_STORAGE
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        startActivity(Intent(this, AddPhotoActivity::class.java))
                    }
                    return@setOnItemSelectedListener true
                }
                R.id.action_favorite_alarm -> {
                    var alarmFragment = AlarmFragment()
                    val toBack = supportFragmentManager.beginTransaction()
                    weatherTask().execute()
                    toBack.replace(R.id.main_content, alarmFragment)
                    toBack.addToBackStack(null)
                    toBack.commit()
                    return@setOnItemSelectedListener true
                }
                R.id.action_account -> {
                    var userFragment = UserFragment()
                    val toBack = supportFragmentManager.beginTransaction()
                    toBack.replace(R.id.main_content, userFragment)
                    toBack.addToBackStack(null)
                    toBack.commit()

                    //********* 알람 실행은 우선 됨
//                    startActivity(Intent(this, WateringActivity::class.java))
                    //********* 레이아웃 조정 필요/


                    return@setOnItemSelectedListener true
                }

                else -> false
            }
        }
    }


    lateinit var binding: ActivityMainBinding

    inner class weatherTask() : AsyncTask<String, Void, String>() {
        override fun onPreExecute() {
            super.onPreExecute()

        }

        override fun doInBackground(vararg p0: String?): String? {
            var response: String?
            try {
                response =
                    URL("https://api.openweathermap.org/data/2.5/weather?q=$CITY&units=metric&appid=$API")
                        .readText(Charsets.UTF_8)
            } catch (e: Exception) {
                response = null
            }
            return response
        }


        override fun onPostExecute(result: String?) {

            super.onPostExecute(result)
            try {
                val jsonObj = JSONObject(result)
                val main = jsonObj.getJSONObject("main")
                val sys = jsonObj.getJSONObject("sys")
                val wind = jsonObj.getJSONObject("wind")
                val weather = jsonObj.getJSONArray("weather").getJSONObject(0)
                val updatedAt: Long = jsonObj.getLong("dt")
                val updateAtText = "Updated at: " + java.text.SimpleDateFormat(
                    "dd/mm/yyyy hh:mm a",
                    Locale.ENGLISH
                ).format(
                    Date(updatedAt * 1000)
                )
                val temp = main.getString("temp") + "C"
                val tempMin = "Min Temp: " + main.getString("temp_min") + "C"
                val tempMax = "Max Temp: " + main.getString("temp_max") + "C"
                val pressure = main.getString("pressure")
                val humidity = main.getString("humidity")
                val sunrise: Long = sys.getLong("sunrise")
                val sunset: Long = sys.getLong("sunset")
                val windSpeed = wind.getString("speed")
                val weatherDescription = weather.getString("description")
                val address = jsonObj.getString("name") + ", " + sys.getString("country")



                findViewById<TextView>(R.id.weatherTxt).text = weatherDescription
                findViewById<TextView>(R.id.locationTxt).text = address
                findViewById<TextView>(R.id.temperatureTxt).text = temp
                findViewById<TextView>(R.id.sunriseTime).text = SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(sunrise * 1000))
                findViewById<TextView>(R.id.sunsetTime).text = SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(sunset * 1000))
                findViewById<TextView>(R.id.humidity).text = humidity
                findViewById<TextView>(R.id.wind).text = windSpeed
            } catch (e: Exception) {

            }
        }
    }


}

