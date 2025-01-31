package com.hammadirfan.mysqlitehelper

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var add = findViewById<Button>(R.id.Add)
        add.setOnClickListener(){
            startActivity(Intent(this,Add::class.java))
        }

        var myhelper:SqliteOpenHelper = SqliteOpenHelper(this)
        myhelper.ReadContacts()
    }
}