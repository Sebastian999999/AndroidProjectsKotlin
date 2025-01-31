package com.hammadirfan.broadcastreceivers

import android.content.BroadcastReceiver
import android.content.ContextWrapper
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    var br:MyBroadCastReceiver? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        br = MyBroadCastReceiver()


        setContentView(R.layout.activity_main)
        var iff= IntentFilter(Intent.ACTION_AIRPLANE_MODE_CHANGED)
        registerReceiver(br,iff)
        var send = findViewById<Button>(R.id.send)
        var et = findViewById<EditText>(R.id.et)

        send.setOnClickListener {
            var i = Intent("comm.smd24.myaction")
            i.putExtra("text",et.text.toString())
            sendBroadcast(i)
        }
        br = MyBroadCastReceiver()
        var iff2= IntentFilter("comm.smd24.myaction")
        registerReceiver(br,iff2)
    }
}