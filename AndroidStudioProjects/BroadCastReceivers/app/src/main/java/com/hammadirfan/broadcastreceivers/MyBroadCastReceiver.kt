package com.hammadirfan.broadcastreceivers

import android.content.Context
import android.content.Intent
import android.widget.Toast
import android.content.BroadcastReceiver
import android.widget.Toast.LENGTH_LONG

class MyBroadCastReceiver:BroadcastReceiver(){
    override fun onReceive(p0: Context?, p1: Intent?) {
        if(p1?.action.equals(Intent.ACTION_AIRPLANE_MODE_CHANGED)){

            if(p1?.getBooleanExtra("state",false) == false)
            {
               Toast.makeText(p0,"Airplane Mode is Off",LENGTH_LONG).show()
            }
            else
            {
                Toast.makeText(p0,"Airplane Mode is On",LENGTH_LONG).show()
            }
        }
        else if(p1?.action.equals("comm.smd24.myaction")){
            var text = p1?.getStringExtra("text")
            Toast.makeText(p0,text,LENGTH_LONG).show()
        }
    }
}
