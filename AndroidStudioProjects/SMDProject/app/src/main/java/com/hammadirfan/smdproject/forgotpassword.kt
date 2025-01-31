//package com.hammadirfan.smdproject
//
//import android.content.Intent
//import android.os.Bundle
//import android.widget.Button
//import android.widget.TextView
//import androidx.activity.enableEdgeToEdge
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.view.ViewCompat
//import androidx.core.view.WindowInsetsCompat
//
//class forgotpassword : AppCompatActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_forgotpassword)
//        enableEdgeToEdge()
//
//        val sendcodebutton: Button = findViewById(R.id.sendcodebtn)
//        sendcodebutton.setOnClickListener {
//            val intent = Intent(this, ResetPassword::class.java)
//            startActivity(intent)
//        }
//
//        val loginredirect: TextView = findViewById(R.id.LoginPageRedirectText)
//        loginredirect.setOnClickListener {
//            val intent = Intent(this, SignIn::class.java)
//            startActivity(intent)
//        }
//
//    }
//}

package com.hammadirfan.smdproject

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class forgotpassword : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgotpassword)
        enableEdgeToEdge()

        auth = FirebaseAuth.getInstance()

        val emailEditText: EditText = findViewById(R.id.email)
        val sendCodeButton: Button = findViewById(R.id.sendcodebtn)
        val loginRedirect: TextView = findViewById(R.id.LoginPageRedirectText)

        sendCodeButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()

            if (email.isEmpty()) {
                Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Password reset email sent", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, ResetPassword::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, "Failed to send reset email: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        loginRedirect.setOnClickListener {
            val intent = Intent(this, SignIn::class.java)
            startActivity(intent)
        }
    }
}
