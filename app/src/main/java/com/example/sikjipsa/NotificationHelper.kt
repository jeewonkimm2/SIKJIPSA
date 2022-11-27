package com.example.sikjipsa

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import com.bumptech.glide.Glide.init

class NotificationHelper(base: Context?) : ContextWrapper(base) {

    private val cID = "cID"
    private val cNm = "cNm"
    init{
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            createChannel()
        }
    }

    private fun createChannel(){
        val channel = NotificationChannel(cID, cNm, NotificationManager.IMPORTANCE_DEFAULT)

        channel.enableLights(true)
        channel.enableVibration(true)
        channel.lightColor = Color.GREEN
        channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE

        getManager().createNotificationChannel(channel)
    }

    fun getManager(): NotificationManager{
        return getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    }

    fun getChannelNotification(): NotificationCompat.Builder{
        return NotificationCompat.Builder(applicationContext, cID,)
            .setContentTitle("Alarm")
            .setContentText("Watering Time~!")
            .setSmallIcon(R.drawable.ic_launcher_background)
    }
}

