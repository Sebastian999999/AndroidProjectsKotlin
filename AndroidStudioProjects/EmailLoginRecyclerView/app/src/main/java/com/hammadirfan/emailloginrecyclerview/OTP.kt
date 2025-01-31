package com.hammadirfan.emailloginrecyclerview

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthProvider

class OTP : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp)
        var otp = getIntent().getStringExtra("token")


        var otpp = findViewById<EditText>(R.id.otp)

        var confirm = findViewById<Button>(R.id.button)

        confirm.setOnClickListener(){
            var credential = PhoneAuthProvider.getCredential(otp!!,otpp.text.toString())
            var mAuth = FirebaseAuth.getInstance()
            mAuth.signInWithCredential(credential)
                .addOnSuccessListener {
                    var i = Intent(this, MainScreen::class.java)
                    i.flags=Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(i)
                }
                .addOnFailureListener{
                    //Log.e("Error",it.message.toString())
                    Toast.makeText(this,"Failed to Login" , Toast.LENGTH_LONG).show()
                    finish()
                }
        }
    }
}