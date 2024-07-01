// UserProfileActivity.kt
package com.example.nsl_mini

import UserData
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.nsl_mini.databinding.ActivityUserProfileBinding
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class UserProfileActivity : BaseActivity() {

    private lateinit var binding: ActivityUserProfileBinding
    private lateinit var databaseReference: DatabaseReference
    private lateinit var storageReference: StorageReference
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupDrawer()

        // Initialize Firebase references
        databaseReference = FirebaseDatabase.getInstance().reference.child("users")
        storageReference = FirebaseStorage.getInstance().reference.child("profile_images")

        // Retrieve user ID from SharedPreferences
        val sharedPreferences = getSharedPreferences("user_session", MODE_PRIVATE)
        val userId = sharedPreferences.getString("user_id", null)

        if (userId != null) {
            loadUserData(userId)
        } else {
            Log.d("UserProfileActivity", "User ID not found in SharedPreferences")
        }

        // Upload photo button click listener
        binding.uploadPhotoButton.setOnClickListener {
            openGallery()
        }
    }

    private fun loadUserData(userId: String) {
        // Query Realtime Database for user data
        databaseReference.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val userData = snapshot.getValue(UserData::class.java)
                    // Populate UI elements with user data
                    userData?.let {
                        binding.usernameTextView.text = it.username
                        binding.phoneNumberTextView.text = it.phoneNumber
                        binding.roleTextView.text = it.role
                        if (!it.profileImageUrl.isNullOrEmpty()) {
                            Glide.with(this@UserProfileActivity)
                                .load(it.profileImageUrl)
                                .into(binding.profileImageView)
                        }
                    }
                } else {
                    // Document does not exist
                    Log.d("UserProfileActivity", "User data not found")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle any errors
                Log.e("UserProfileActivity", "Error getting user data", error.toException())
            }
        })
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        galleryLauncher.launch(intent)
    }

    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                imageUri = result.data!!.data
                binding.profileImageView.setImageURI(imageUri)  // Show selected image immediately
                uploadImage()
            }
        }

    private fun uploadImage() {
        imageUri?.let { uri ->
            val sharedPreferences = getSharedPreferences("user_session", MODE_PRIVATE)
            val userId = sharedPreferences.getString("user_id", null) ?: return
            val filePath = storageReference.child("profile_images").child("$userId.jpg")

            val uploadTask = filePath.putFile(uri)
            uploadTask.continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                filePath.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    val userRef = databaseReference.child(userId)
                    userRef.child("profileImageUrl").setValue(downloadUri.toString())
                        .addOnSuccessListener {
                            // Save profile image URL to shared preferences
                            sharedPreferences.edit().putString("profile_image_url", downloadUri.toString()).apply()

                            Toast.makeText(this, "Photo uploaded successfully", Toast.LENGTH_SHORT)
                                .show()
                            Log.d("UserProfileActivity", "Photo URL saved successfully")
                        }.addOnFailureListener { e ->
                            Toast.makeText(
                                this,
                                "Failed to save photo URL: ${e.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                            Log.e("UserProfileActivity", "Failed to save photo URL", e)
                        }
                } else {
                    // Handle failures
                    Toast.makeText(
                        this,
                        "Photo upload failed: ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e("UserProfileActivity", "Photo upload failed", task.exception)
                }
            }
        }
    }
}
