//package com.hammadirfan.smdproject
//
//import android.content.Intent
//import android.os.Bundle
//import android.widget.Button
//import android.widget.TextView
//import androidx.activity.enableEdgeToEdge
//import com.google.android.material.snackbar.Snackbar
//import androidx.appcompat.app.AppCompatActivity
//import androidx.navigation.findNavController
//import androidx.navigation.ui.AppBarConfiguration
//import androidx.navigation.ui.navigateUp
//import androidx.navigation.ui.setupActionBarWithNavController
//import com.hammadirfan.smdproject.databinding.ActivitySignInBinding
//
//class SignIn : AppCompatActivity() {
//
//    private lateinit var binding: ActivitySignInBinding
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        binding = ActivitySignInBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//        enableEdgeToEdge()
//
//        val signinbutton: Button = findViewById(R.id.signinbtn)
//        signinbutton.setOnClickListener {
//            val intent = Intent(this, Options::class.java)
//            startActivity(intent)
//        }
//
//        val forgotpass: TextView = findViewById(R.id.forgotpassword)
//        forgotpass.setOnClickListener {
//            val intent = Intent(this, forgotpassword::class.java)
//            startActivity(intent)
//        }
//
//        val signupbutton: TextView = findViewById(R.id.signup)
//        signupbutton.setOnClickListener {
//            val intent = Intent(this, SignUp::class.java)
//            startActivity(intent)
//        }
//
//
//
//
//
//    }
//}
//
//
//package com.hammadirfan.smdproject
//
//import android.content.Intent
//import android.os.Bundle
//import android.widget.Button
//import android.widget.EditText
//import android.widget.TextView
//import android.widget.Toast
//import androidx.activity.enableEdgeToEdge
//import androidx.appcompat.app.AppCompatActivity
//import com.google.firebase.auth.FirebaseAuth
//import com.hammadirfan.smdproject.databinding.ActivitySignInBinding
//
//class SignIn : AppCompatActivity() {
//
//    private lateinit var binding: ActivitySignInBinding
//    private lateinit var auth: FirebaseAuth
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivitySignInBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//        enableEdgeToEdge()
//
//        auth = FirebaseAuth.getInstance()
//
//        val emailEditText: EditText = findViewById(R.id.email)
//        val passwordEditText: EditText = findViewById(R.id.password)
//        val signinButton: Button = findViewById(R.id.signinbtn)
//        val forgotPassTextView: TextView = findViewById(R.id.forgotpassword)
//        val signupTextView: TextView = findViewById(R.id.signup)
//
//        signinButton.setOnClickListener {
//            val email = emailEditText.text.toString().trim()
//            val password = passwordEditText.text.toString().trim()
//
//            if (email.isEmpty() || password.isEmpty()) {
//                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
//                return@setOnClickListener
//            }
//
//            auth.signInWithEmailAndPassword(email, password)
//                .addOnCompleteListener { task ->
//                    if (task.isSuccessful) {
//                        Toast.makeText(this, "Sign in successful", Toast.LENGTH_SHORT).show()
//                        val intent = Intent(this, Options::class.java)
//                        startActivity(intent)
//                        finish()
//                    } else {
//                        Toast.makeText(this, "Sign in failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
//                    }
//                }
//        }
//
//        forgotPassTextView.setOnClickListener {
//            val intent = Intent(this, forgotpassword::class.java)
//            startActivity(intent)
//        }
//
//        signupTextView.setOnClickListener {
//            val intent = Intent(this, SignUp::class.java)
//            startActivity(intent)
//        }
//    }
//}

//package com.hammadirfan.smdproject
//
//import android.content.Intent
//import android.os.Bundle
//import android.util.Log
//import android.widget.Button
//import android.widget.TextView
//import androidx.activity.enableEdgeToEdge
//import androidx.appcompat.app.AppCompatActivity
//import com.google.android.material.snackbar.Snackbar
//import com.hammadirfan.smdproject.databinding.ActivitySignInBinding
//
//class SignIn : AppCompatActivity() {
//
//    private lateinit var binding: ActivitySignInBinding
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        binding = ActivitySignInBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//        enableEdgeToEdge()
//
//        try {
//            val signinButton: Button = findViewById(R.id.signinbtn)
//            signinButton.setOnClickListener {
//                val intent = Intent(this, Options::class.java)
//                startActivity(intent)
//            }
//
//            val forgotPass: TextView = findViewById(R.id.forgotpassword)
//            forgotPass.setOnClickListener {
//                val intent = Intent(this, forgotpassword::class.java)
//                startActivity(intent)
//            }
//
//            val signupButton: TextView = findViewById(R.id.signup)
//            signupButton.setOnClickListener {
//                val intent = Intent(this, SignUp::class.java)
//                startActivity(intent)
//            }
//        } catch (e: Exception) {
//            Log.e("SignIn", "Error initializing views: ${e.message}")
//            Snackbar.make(binding.root, "An error occurred: ${e.message}", Snackbar.LENGTH_LONG).show()
//        }
//    }
//}

package com.hammadirfan.smdproject

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.hammadirfan.smdproject.databinding.ActivitySignInBinding

class SignIn : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()

        auth = FirebaseAuth.getInstance()

        val emailEditText: EditText = findViewById(R.id.email)
        val passwordEditText: EditText = findViewById(R.id.password)
        val signinButton: Button = findViewById(R.id.signinbtn)
        val forgotPass: TextView = findViewById(R.id.forgotpassword)
        val signupButton: TextView = findViewById(R.id.signup)

        signinButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Snackbar.make(binding.root, "Please enter email and password", Snackbar.LENGTH_LONG).show()
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Log.d("SignIn", "signInWithEmail:success")
                        val user = auth.currentUser
                        var intent:Intent = Intent(this, Options::class.java)
                        if (email=="h@gmail.com"){
                            intent = Intent(this, ManagerDashboard::class.java)
                        }
                        startActivity(intent)
                        finish()
                    } else {
                        Log.w("SignIn", "signInWithEmail:failure", task.exception)
                        Snackbar.make(binding.root, "Authentication failed: ${task.exception?.message}", Snackbar.LENGTH_LONG).show()
                    }
                }
        }

        forgotPass.setOnClickListener {
            val intent = Intent(this, forgotpassword::class.java)
            startActivity(intent)
        }

        signupButton.setOnClickListener {
            val intent = Intent(this, SignUp::class.java)
            startActivity(intent)
        }
    }
}
