package com.example.sikjipsa

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class AlarmReceiver: BroadcastReceiver() {

    companion object {
        const val NOTIFICATION_ID = 100
        const val NOTIFICATION_CHANNEL_ID = "1000"
    }

    override fun onReceive(context: Context, intent: Intent) {

        createNotificationChannel(context)
        notifyNotification(context)
        Log.d("ITM", "onReceive")
    }

    private fun createNotificationChannel(context: Context) {

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "기상 알림",
                NotificationManager.IMPORTANCE_HIGH
            )
            Log.d("ITM", "createNotificationChannel")
            NotificationManagerCompat.from(context).createNotificationChannel(notificationChannel)
        }
        Log.d("ITM", "createNotificationChannel: FAILED")
    }

    private fun notifyNotification(context: Context){
        with(NotificationManagerCompat.from(context)){
            val build = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setContentTitle("SIKJIPSA")
                .setContentText("Watering Time~~~!!!")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setPriority(NotificationCompat.PRIORITY_HIGH)

            notify(NOTIFICATION_ID, build.build())
            Log.d("ITM", "notifyNotification")
        }
    }
}