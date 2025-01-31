package com.hammadirfan.emailloginrecyclerview

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val email = findViewById<EditText>(R.id.Email)
        val password = findViewById<EditText>(R.id.Password)
        val phone = findViewById<EditText>(R.id.Phone)
        val esignup = findViewById<Button>(R.id.esignup)
        val psignup = findViewById<Button>(R.id.psignup)
        val esignin = findViewById<Button>(R.id.esignin)
        var mAuth = FirebaseAuth.getInstance()
        esignup.setOnClickListener(){
            mAuth.createUserWithEmailAndPassword(
                email.text.toString(), password.text.toString())
                .addOnSuccessListener {
                    startActivity(Intent(this, MainScreen::class.java))
                    Toast.makeText(this,"Successfully Signed up",Toast.LENGTH_LONG).show()
                    finish()
                }
                .addOnFailureListener{
                    Log.e("Error",it.message.toString())
                    Toast.makeText(this,"Failure to Sign up",Toast.LENGTH_LONG).show()
                }
        }
        if(mAuth.currentUser != null){
            startActivity(Intent(this, MainScreen::class.java))
            finish()
        }
        esignin.setOnClickListener(){
            mAuth.signInWithEmailAndPassword(
                email.text.toString(), password.text.toString())
                .addOnSuccessListener {
                    startActivity(Intent(this, MainScreen::class.java))
                    Toast.makeText(this,"Succesfully Signed in",Toast.LENGTH_LONG).show()
                    finish()
                }
                .addOnFailureListener{
                    Log.e("Error",it.message.toString())
                    Toast.makeText(this,"Failure to Sign in",Toast.LENGTH_LONG).show()
                }
        }
        if(mAuth.currentUser != null){
            startActivity(Intent(this, MainScreen::class.java))
            finish()
        }

        var callbacks = object:PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
            override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                startActivity(Intent(this@MainActivity, MainScreen::class.java))
            }

            override fun onVerificationFailed(p0: com.google.firebase.FirebaseException) {
                Toast.makeText(this@MainActivity,"Verification Failed",Toast.LENGTH_LONG).show()
            }

            override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
                super.onCodeSent(p0, p1)
                val intent = Intent(this@MainActivity,OTP::class.java)
                intent.putExtra("token",p0)
                startActivity(intent)
            }
        }
        psignup.setOnClickListener() {
            var option = PhoneAuthOptions.newBuilder()
                .setPhoneNumber(phone.text.toString())
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(callbacks)
                .build()
            PhoneAuthProvider.verifyPhoneNumber(option)

//            val intent = Intent(this,OTP::class.java)
//            intent.putExtra("phone",phone.text.toString())
//            startActivity(intent)
        }

    }
}