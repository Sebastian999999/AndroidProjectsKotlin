package com.example.myapplication1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import de.hdodenhof.circleimageview.CircleImageView
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var btn = findViewById<CircleImageView>(R.id.main)

        btn.setOnClickListener{
            startActivity(
                Intent(this,
                    MainActivity2::class.java)
            )
        }
    }
}