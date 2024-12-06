package com.example.coursework_1

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat

class NotificationReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        // Build the notification
        val notification = NotificationCompat.Builder(context, "daily_notification_channel")
            .setSmallIcon(R.drawable.pfp)  // Use an actual icon here
            .setContentTitle("Daily Reminder")
            .setContentText("Time to Play!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
        UserInformation.setDatabase("playedToday", "false")
        // Send the notification
        notificationManager.notify(1001, notification)
    }
}