package com.hammadirfan.myrvex

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class ViewContent : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_content)
        var name = findViewById<TextView>(R.id.name)
        var phone = findViewById<TextView>(R.id.phno)
        var email = findViewById<TextView>(R.id.email)
        var age = findViewById<TextView>(R.id.age)

        name.text=intent.getStringExtra("name")
        phone.text=intent.getStringExtra("phone")
        email.text=intent.getStringExtra("email")
        age.text=intent.getStringExtra("age")

        var close=findViewById<TextView>(R.id.closebutton)

        close.setOnClickListener(){
            finish()
        }
    }
}