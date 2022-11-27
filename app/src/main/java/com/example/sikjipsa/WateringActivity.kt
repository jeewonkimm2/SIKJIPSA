package com.example.sikjipsa

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TimePicker
import com.example.sikjipsa.databinding.ActivityWateringBinding
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_watering.*
import java.text.DateFormat
import java.util.*

class WateringActivity : AppCompatActivity(), TimePickerDialog.OnTimeSetListener {
    private lateinit var binding: ActivityWateringBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = ActivityWateringBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.timeBtn.setOnClickListener{
            var timePicker = TimePickerFragment()
            timePicker.show(supportFragmentManager, "Time picker")
        }
        binding.alarmCancelBtn.setOnClickListener{
            cancelAlarm()
        }
    }

    private fun cancelAlarm() {
        var alarmManager: AlarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        var intent = Intent(this, AlertReceiver:: class.java)
        var pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0)
        alarmManager.cancel(pendingIntent)
        time_text.setText("Cancel")

    }

    override fun onTimeSet(timePicker: TimePicker?, hourOfDay: Int, minute: Int) {
        var c = Calendar.getInstance()
        c.set(Calendar.HOUR_OF_DAY, hourOfDay)
        c.set(Calendar.MINUTE, minute)
        c.set(Calendar.SECOND, 0)

        updateTimeText(c)

        startAlarm(c)

    }

    private fun startAlarm(c: Calendar) {
        var alarmManager: AlarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        var intent = Intent(this, AlertReceiver:: class.java)
        var curTime = DateFormat.getTimeInstance(DateFormat.SHORT).format(c.time)
        intent.putExtra("time", curTime)
        var pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0)
        if(c.before(Calendar.getInstance())){
            c.add(Calendar.DATE, 1)
        }
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.timeInMillis, pendingIntent)
    }

    private fun updateTimeText(c: Calendar) {
        var curTime = DateFormat.getTimeInstance(DateFormat.SHORT).format(c.time)
        binding.timeText.text = "Time: "
        binding.timeText.append(curTime)
    }

}