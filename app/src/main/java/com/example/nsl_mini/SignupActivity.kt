package com.example.nsl_mini

import UserData
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.nsl_mini.databinding.ActivitySignupBinding
import com.google.firebase.database.*

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.reference.child("users")

        binding.signupButton.setOnClickListener {
            val username = binding.signupUsername.text.toString().trim()
            val phoneNumber = binding.signupPhoneNumber.text.toString().trim()
            val password = binding.signupPassword.text.toString().trim()

            Log.d("SignupActivity", "Sign up button clicked")

            val usernameValidation = validateUsername(username)
            val phoneNumberValidation = validatePhoneNumber(phoneNumber)
            val passwordValidation = validatePassword(password, username)

            when {
                username.isEmpty() -> showToast("Username is required")
                phoneNumber.isEmpty() -> showToast("Phone number is required")
                password.isEmpty() -> showToast("Password is required")
                usernameValidation != null -> showToast(usernameValidation)
                phoneNumberValidation != null -> showToast(phoneNumberValidation)
                passwordValidation != null -> showToast(passwordValidation)
                else -> {
                    Log.d("SignupActivity", "Checking username and phone number")
                    checkUsernameAndPhoneNumber(username, phoneNumber) { isUnique ->
                        Log.d("SignupActivity", "Is username and phone number unique: $isUnique")
                        if (isUnique) {
                            Log.d("SignupActivity", "Username and phone number are unique, signing up user")
                            signUpUser(username, phoneNumber, password)
                        } else {
                            showToast("Username or phone number already exists")
                        }
                    }
                }
            }
        }

        binding.loginRedirect.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun validateUsername(username: String): String? {
        val specialCharactersRegex = Regex("[^a-zA-Z0-9_-]")
        return when {
            username.length !in 8..15 -> "Username must be between 8 and 15 characters"
            username.contains(" ") -> "Username must not contain spaces"
            specialCharactersRegex.containsMatchIn(username) -> "Username must not contain special characters"
            else -> null
        }
    }

    private fun validatePhoneNumber(phoneNumber: String): String? {
        val phoneNumberPattern = Regex("^\\d{10}$")
        return when {
            !phoneNumberPattern.matches(phoneNumber) -> "Phone number must be exactly 10 digits and contain only numbers"
            else -> null
        }
    }

    private fun validatePassword(password: String, username: String): String? {
        val passwordPattern = Regex("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@\$!%*?&])[A-Za-z\\d@\$!%*?&]{6,14}$")
        val commonPasswords = listOf("password", "123456", "12345678", "qwerty", "abc123", "password1")
        return when {
            password.length < 6 -> "Password must be at least 6 characters long"
            !passwordPattern.matches(password) -> "Password must include at least one capital letter, one number, and one special character, and be less than 15 characters"
            password in commonPasswords -> "Password is too common. Choose a more secure password"
            password.contains(username, ignoreCase = true) -> "Password must not contain parts of the username"
            else -> null
        }
    }

    private fun checkUsernameAndPhoneNumber(username: String, phoneNumber: String, callback: (Boolean) -> Unit) {
        databaseReference.orderByChild("username").equalTo(username)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    Log.d("SignupActivity", "Username check snapshot.exists(): ${snapshot.exists()}")
                    if (snapshot.exists()) {
                        callback(false)
                    } else {
                        databaseReference.orderByChild("phoneNumber").equalTo(phoneNumber)
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    Log.d("SignupActivity", "Phone number check snapshot.exists(): ${snapshot.exists()}")
                                    callback(!snapshot.exists())
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    showToast("Database error: ${error.message}")
                                    Log.d("SignupActivity", "Database error: ${error.message}")
                                }
                            })
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    showToast("Database error: ${error.message}")
                    Log.d("SignupActivity", "Database error: ${error.message}")
                }
            })
    }

    private fun signUpUser(username: String, phoneNumber: String, password: String) {
        val userId = databaseReference.push().key
        val user = UserData(id = userId, username = username, phoneNumber = phoneNumber, password = password, role = "user")

        Log.d("SignupActivity", "UserId generated: $userId")

        if (userId != null) {
            databaseReference.child(userId).setValue(user).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    showToast("Sign up successful")
                    Log.d("SignupActivity", "Sign up successful")
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                } else {
                    showToast("Sign up failed: ${task.exception?.message}")
                    Log.d("SignupActivity", "Sign up failed: ${task.exception?.message}")
                }
            }
        } else {
            Log.d("SignupActivity", "UserId is null")
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
