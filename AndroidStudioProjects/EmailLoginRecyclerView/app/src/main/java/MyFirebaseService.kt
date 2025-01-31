import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.hammadirfan.emailloginrecyclerview.R

class MyFirebaseService:FirebaseMessagingService(){
    override fun onNewToken(token: String) {
        Log.d("Token",token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.e("Message",remoteMessage.notification?.body.toString())

        createNotificationChannel()
        var builder = NotificationCompat.Builder(this, "myNotification")
            .setSmallIcon(android.R.drawable.ic_notification_overlay)
            .setContentTitle("My notification")
            .setContentText("Much longer text that cannot fit one line...")
            .setStyle(
                NotificationCompat.BigTextStyle()
                .bigText("Much longer text that cannot fit one line..."))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        builder.build()
    }

    fun createNotificationChannel() {
        //super.createNotificationChannel(channelId, channelName, channelDescription)
        if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
            val name = "myNotification"
            val descriptionText = "This is my notification"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("myNotification", name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}