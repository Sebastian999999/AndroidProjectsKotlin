package com.example.rcvprac1

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class NewsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)
        val bundle = intent.extras
        var newsHeading = bundle?.getString("Heading")
        var newsImage = bundle?.getInt("Image")
        var newsBody = bundle?.getString("Body")

        findViewById<TextView>(R.id.mtvnewsheading).text = newsHeading
        findViewById<TextView>(R.id.mtvnewsbody).text = newsBody
        findViewById<ImageView>(R.id.ivnewsimage).setImageResource(newsImage!!)

    }
}