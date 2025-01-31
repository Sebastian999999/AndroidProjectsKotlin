//package com.hammadirfan.smdproject
//
//import android.content.Intent
//import android.os.Bundle
//import android.widget.Button
//import androidx.activity.enableEdgeToEdge
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.view.ViewCompat
//import androidx.core.view.WindowInsetsCompat
//
//class ResetPassword : AppCompatActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_reset_password)
//        enableEdgeToEdge()
//
//        val resetpasswordbutton: Button = findViewById(R.id.resetpasswordbtn)
//        resetpasswordbutton.setOnClickListener {
//            val intent = Intent(this, PasswordChanged::class.java)
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
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class ResetPassword : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)
        enableEdgeToEdge()

        auth = FirebaseAuth.getInstance()

        val newPasswordEditText: EditText = findViewById(R.id.newpassword)
        val resetPasswordButton: Button = findViewById(R.id.resetpasswordbtn)

        resetPasswordButton.setOnClickListener {
            val newPassword = newPasswordEditText.text.toString().trim()

            if (newPassword.isEmpty()) {
                Toast.makeText(this, "Please enter your new password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val authUser = auth.currentUser
            authUser?.updatePassword(newPassword)
                ?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Password has been reset", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, PasswordChanged::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this, "Failed to reset password: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}
