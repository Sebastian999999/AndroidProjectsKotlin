package com.hammadirfan.smdproject

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Options : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_options)
        enableEdgeToEdge()

        val DigitalListbtn: TextView = findViewById(R.id.DigitalListbtn)
        DigitalListbtn.setOnClickListener {
            val intent = Intent(this, DigitalList::class.java)
            startActivity(intent)
        }

        val ScanProductbtn: TextView = findViewById(R.id.ScanProductbtn)
        ScanProductbtn.setOnClickListener {
            val intent = Intent(this, ScanItem::class.java)
            startActivity(intent)
        }




    }
}