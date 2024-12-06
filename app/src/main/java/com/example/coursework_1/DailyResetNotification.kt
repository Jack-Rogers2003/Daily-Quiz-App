package com.example.coursework_1

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent

object DailyResetNotification {

    private fun createNotificationChannel(context: Context) {
        val channel = NotificationChannel(
            "daily_notification_channel",
            "Alarm Notifications",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Channel for alarm notifications"
        }
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }


    fun scheduleNotification(context: Context, resetTime: String?) {
        createNotificationChannel(context)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Intent to trigger the BroadcastReceiver
        val intent = Intent(context, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Calculate the trigger time (10 seconds from now)
        val triggerTime = android.icu.util.Calendar.getInstance().apply {
            resetTime?.split(":")?.get(0)?.let {
                set(android.icu.util.Calendar.HOUR_OF_DAY, it.toInt())
                set(android.icu.util.Calendar.MINUTE, resetTime.split(":")[1].toInt())
            }
        }
        // Schedule the alarm
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime.timeInMillis, pendingIntent)
    }

    fun updateAlarm(context: Context, newTime: String) {
        cancelAlarm(context)
        scheduleNotification(context, newTime)
    }

    fun cancelAlarm(context: Context) {
        val intent = Intent(context, NotificationReceiver::class.java)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()
    }

}