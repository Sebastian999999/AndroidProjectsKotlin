import android.content.Context
import android.content.Intent
import android.widget.Toast
import android.content.BroadcastReceiver
class MyBroadCastReceiver:BroadcastReceiver(){
    override fun onReceive(p0: Context?, p1: Intent?) {
        if(p1.action.equals(Intent.ACTION_AIRPLANE_MODE_CHANGED)){
            var state = p1?.getStringExtra("state")
            Toast.makeText(p0,state.toString(),LENGTH_LONG).show()
        }
    }
}