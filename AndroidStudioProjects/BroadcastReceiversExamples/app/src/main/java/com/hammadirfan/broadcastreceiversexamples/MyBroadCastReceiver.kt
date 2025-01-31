package com.hammadirfan.broadcastreceiversexamples

import android.content.Context
import android.content.Intent
import android.widget.Toast
import android.content.BroadcastReceiver
import android.widget.Toast.LENGTH_LONG

class MyBroadCastReceiver:BroadcastReceiver(){
    override fun onReceive(p0: Context?, p1: Intent?) {
        if(p1?.action.equals(Intent.ACTION_AIRPLANE_MODE_CHANGED)){
            var state:String? = null
            if(p1?.getBooleanExtra("state",false) == false)
            {
                 state = "Airplane Mode is Off"
            }
            else
            {
                 state = "Airplane Mode is On"
            }
            Toast.makeText(p0,state.toString(),LENGTH_LONG).show()
        }
        else if(p1?.action.equals("com.hammadirfan.broadcastreceiversexamples")){
            var text = p1?.getStringExtra("text")
            Toast.makeText(p0,text,LENGTH_LONG).show()
        }
    }
}
