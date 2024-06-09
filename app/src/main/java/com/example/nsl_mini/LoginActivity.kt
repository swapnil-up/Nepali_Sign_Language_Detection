package com.example.nsl_mini

import UserData
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.nsl_mini.databinding.ActivityLoginBinding // Correct package name
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.reference.child("users")

        binding.loginButton.setOnClickListener {
            val loginUsername = binding.loginUsername.text.toString().trim()
            val loginPassword = binding.loginPassword.text.toString().trim()

            val usernameValidation = validateUsername(loginUsername)
            val passwordValidation = validatePassword(loginPassword)

            if (usernameValidation == null && passwordValidation == null) {
                loginUser(loginUsername, loginPassword)
            } else {
                usernameValidation?.let { Toast.makeText(this, it, Toast.LENGTH_SHORT).show() }
                passwordValidation?.let { Toast.makeText(this, it, Toast.LENGTH_SHORT).show() }
            }
        }

        binding.signupRedirect.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
            finish()
        }
    }

    private fun validateUsername(username: String): String? {
        return when {
            username.isEmpty() -> "Username is required"
            username.length !in 8..15 -> "Username must be between 8 and 15 characters"
            username.contains(" ") -> "Username must not contain spaces"
            !username.matches(Regex("^[a-zA-Z0-9_]+$")) -> "Username can only contain letters, numbers, and underscores"
            else -> null
        }
    }

    private fun validatePassword(password: String): String? {
        return when {
            password.isEmpty() -> "Password is required"
            password.length < 6 -> "Password must be at least 6 characters long"
            password.length > 14 -> "Password must be less than 15 characters"
            else -> null
        }
    }

    private fun loginUser(username: String, password: String) {
        if (username == "admin321" && password == "admin123") {
            // Allow the predefined admin to log in directly
            Toast.makeText(this, "Admin login successful", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, AdminActivity::class.java))
            finish()
        } else {
            databaseReference.orderByChild("username").equalTo(username)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (userSnapshot in dataSnapshot.children) {
                                val userData = userSnapshot.getValue(UserData::class.java)
                                if (userData != null && userData.password == password) {
                                    // Save user ID in SharedPreferences
                                    val sharedPreferences = getSharedPreferences("user_session", MODE_PRIVATE)
                                    val editor = sharedPreferences.edit()
                                    editor.putString("user_id", userData.id)
                                    editor.apply()

                                    Toast.makeText(this@LoginActivity, "Login successful", Toast.LENGTH_SHORT).show()
                                    redirectUser(userData.role)
                                    return
                                }
                            }
                            Toast.makeText(this@LoginActivity, "Invalid password", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this@LoginActivity, "Username not found", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        Toast.makeText(this@LoginActivity, "Database Error: ${databaseError.message}", Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }


    private fun redirectUser(role: String?) {
        when (role) {
            "admin" -> {
                startActivity(Intent(this, AdminActivity::class.java))
            }
            "user" -> {
                startActivity(Intent(this, MainActivity::class.java))
            }
            else -> {
                Toast.makeText(this, "Unknown user role", Toast.LENGTH_SHORT).show()
            }
        }
        finish()
    }
}
