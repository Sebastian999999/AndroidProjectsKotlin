import android.content.Context
import android.content.Intent
import android.widget.Toast

class BroadCastReceiver {
    open fun onReceive(context: Context?, intent: Intent?) {
        Toast.makeText(context, "Broadcast Received", Toast.LENGTH_SHORT).show()
    }
}