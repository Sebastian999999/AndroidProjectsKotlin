package com.hammadirfan.mysqlitehelper

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Add : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)

        var save = findViewById<Button>(R.id.Save)
        var name = findViewById<EditText>(R.id.Name)
        var email = findViewById<EditText>(R.id.Email)
        var password = findViewById<EditText>(R.id.Password)
        var phno = findViewById<EditText>(R.id.Phone)

        save.setOnClickListener(){
            var helper = SqliteOpenHelper(this)
            var id = helper.insert(name.text.toString(),email.text.toString(),password.text.toString(),phno.text.toString())
            Toast.makeText(this,"Record Inserted at $id",Toast.LENGTH_LONG).show()
            /*if (id>0){
                finish()
            }*/
        }
    }
}