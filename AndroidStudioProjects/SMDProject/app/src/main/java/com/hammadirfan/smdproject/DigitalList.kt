package com.hammadirfan.smdproject

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat


class DigitalList : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_digital_list)

        val ViewListbtn: TextView = findViewById(R.id.ViewListbtn)
        ViewListbtn.setOnClickListener {
            val intent = Intent(this, Cart::class.java)
            startActivity(intent)
        }

        val CreateListbtn: TextView = findViewById(R.id.CreateListbtn)
        CreateListbtn.setOnClickListener {
            val intent = Intent(this, Item::class.java)
            startActivity(intent)
        }

        val PreviousHistorybtn: TextView = findViewById(R.id.PreviousHistorybtn)
        PreviousHistorybtn.setOnClickListener {
            val intent = Intent(this, VisitHistory::class.java)
            startActivity(intent)
        }


    }
}