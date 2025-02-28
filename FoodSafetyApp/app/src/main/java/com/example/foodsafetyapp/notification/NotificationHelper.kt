package com.example.foodsafetyapp.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.foodsafetyapp.MainActivity
import com.example.foodsafetyapp.R
import com.example.foodsafetyapp.models.AlertSeverity
import java.util.Random

object NotificationHelper {
    private const val CHANNEL_ID = "food_safety_alerts"
    private const val CHANNEL_NAME = "Food Safety Alerts"
    private const val CHANNEL_DESCRIPTION = "Notifications about food safety issues"

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = CHANNEL_DESCRIPTION
                enableLights(true)
                lightColor = Color.RED
                enableVibration(true)
            }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showNotification(
        context: Context,
        title: String,
        message: String,
        severity: AlertSeverity
    ) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val color = when (severity) {
            AlertSeverity.LOW -> Color.GREEN
            AlertSeverity.MEDIUM -> Color.YELLOW
            AlertSeverity.HIGH -> Color.RED
        }

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.notification)  // Make sure to create this icon
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setColor(color)

        try {
            with(NotificationManagerCompat.from(context)) {
                // Use a random ID so multiple notifications can be displayed
                notify(Random().nextInt(1000), builder.build())
            }
        } catch (e: SecurityException) {
            // Handle missing notification permission in Android 13+
            e.printStackTrace()
        }
    }
}