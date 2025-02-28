package com.example.foodsafetyapp

import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.example.foodsafetyapp.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginFragment : Fragment(R.layout.fragment_login) {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: FragmentLoginBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentLoginBinding.bind(view)
        auth = FirebaseAuth.getInstance()

        // Check if this is a logout action
        val isLoggingOut = arguments?.getBoolean("logout", false) ?: false

        if (isLoggingOut) {
            performLogout()
            return
        }

        // If not logging out and user is already logged in, navigate to home
        if (auth.currentUser != null) {
            navigateToHome()
            return
        }

        binding.tvSignupPrompt.setOnClickListener {

        }
        // Setup login UI and listeners
        setupLoginUI()
    }

    private fun setupLoginUI() {
        binding.btnLogin.visibility = View.VISIBLE

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (validateInput(email, password)) {
                binding.progressBar.visibility = View.VISIBLE
                loginUser(email, password)
            }
        }

        binding.tvSignupPrompt.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_signupFragment)
        }
    }

    private fun performLogout() {
        auth.signOut()
        // Clear any user data if needed

        // Make sure login UI is visible
        binding.btnLogin.visibility = View.VISIBLE
        binding.progressBar.visibility = View.GONE

        // Clear input fields
        binding.etEmail.text?.clear()
        binding.etPassword.text?.clear()

        Toast.makeText(context, "Logged out successfully", Toast.LENGTH_SHORT).show()
    }


    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                //binding.progressBar.visibility = View.GONE
                if (task.isSuccessful) {
                    navigateToHome()
                } else {
                    showError(task.exception?.message ?: "Login failed")
                }
            }
    }

    private fun validateInput(email: String, password: String): Boolean {
        if (email.isEmpty()) {
            binding.tilEmail.error = "Email is required"
            return false
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmail.error = "Enter valid email"
            return false
        }
        if (password.isEmpty()) {
            binding.tilPassword.error = "Password is required"
            return false
        }
        if (password.length < 6) {
            binding.tilPassword.error = "Password must be at least 6 characters"
            return false
        }
        return true
    }

    private fun navigateToHome() {
        // Use NavHostFragment.findNavController() to reliably retrieve the NavController.
        findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
    }

    private fun navigateToSignup() {
        // Use NavHostFragment.findNavController() to reliably retrieve the NavController.
        findNavController().navigate(R.id.action_loginFragment_to_signupFragment)
    }
    private fun showError(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }
}
