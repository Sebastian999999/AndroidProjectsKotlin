package com.hammadirfan.myrvex

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView

class NewContent : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_content)

        var name = findViewById<EditText>(R.id.name)
        var email=findViewById<EditText>(R.id.email)
        var phno=findViewById<EditText>(R.id.phno)
        var age=findViewById<EditText>(R.id.age)
        var save=findViewById<Button>(R.id.savebutton)
        val imgid = R.mipmap.ic_launcher_round
        val img = findViewById<ImageView>(R.id.image)
        img.setImageResource(imgid)
        save.setOnClickListener(){
            val i = intent
            i.putExtra("name",name.text.toString())
            i.putExtra("email",email.text.toString())
            i.putExtra("phno",phno.text.toString())
            i.putExtra("age",age.text.toString())
            i.putExtra("imgid",imgid)
            setResult(RESULT_OK,i)
            finish()
        }
    }
}