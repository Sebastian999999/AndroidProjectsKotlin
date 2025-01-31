package com.hammadirfan.smdproject

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.google.auth.oauth2.GoogleCredentials
import com.google.auth.oauth2.ServiceAccountJwtAccessCredentials
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.FileInputStream
import java.io.IOException

object NotificationUtils {
    fun sendNotification(context: Context, messageBody: String) {
        //Toast.makeText(context, "Hallo", Toast.LENGTH_SHORT).show()
        val intent = Intent(context, AddProductFragment::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val requestCode = 0
        val pendingIntent = PendingIntent.getActivity(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_IMMUTABLE,
        )

        val channelId = "fcm_default_channel"
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("FCM Message")
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_DEFAULT,
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notificationId = 0
        notificationManager.notify(notificationId, notificationBuilder.build())
    }


    fun sendFCMMessage(context: Context, deviceToken: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val inputStream = context.assets.open("smdproject-56716-firebase-adminsdk-i5xdi-c1e4337362.json")
            val credentials = GoogleCredentials.fromStream(inputStream)
            val scopedCredentials = credentials.createScoped(listOf("https://www.googleapis.com/auth/cloud-platform"))
            val jwt = scopedCredentials.refreshAccessToken().tokenValue

            val client = OkHttpClient()

            val json = JSONObject()
            val message = JSONObject()
            message.put("token", deviceToken) // replace with device token or condition for targeted messaging
            val notification = JSONObject()
            notification.put("title", "Test Title")
            notification.put("body", "Test Body")
            message.put("notification", notification)
            json.put("message", message)

            val mediaType = "application/json; charset=utf-8".toMediaType()
            val body: RequestBody = json.toString().toRequestBody(mediaType)

            val request = Request.Builder()
                .url("https://fcm.googleapis.com/v1/projects/smdproject-56716/messages:send") // replace with your project id
                .post(body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer $jwt")
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Unexpected code $response")

                println(response.body?.string())
            }


        }
    }
}