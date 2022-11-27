package com.example.sikjipsa

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import kotlin.coroutines.coroutineContext

class AlertReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val notificationHelper: NotificationHelper = NotificationHelper(context)
        var time = intent?.extras?.getString("time")
        val nb: NotificationCompat.Builder = notificationHelper.getChannelNotification(time)

        notificationHelper.getManager().notify(1, nb.build())
    }
}