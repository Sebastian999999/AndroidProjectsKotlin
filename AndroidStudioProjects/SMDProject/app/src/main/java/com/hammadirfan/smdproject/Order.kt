package com.hammadirfan.smdproject

import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import java.util.Calendar

class Order : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order)
        val DeliveryBtn = findViewById<Button>(R.id.DeliverBtn)
        val PickupBtn = findViewById<Button>(R.id.PickupBtn)
        DeliveryBtn.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.OrderOptions, OrderDeliverFragment())
                .commit()
        }

        PickupBtn.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.OrderOptions, OrderPickUpFragment())
                .commit()
        }
    }
}