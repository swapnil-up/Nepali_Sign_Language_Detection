package com.example.nsl_mini

import android.Manifest
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.core.view.GravityCompat
import android.widget.ImageButton
import android.util.Log
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.view.TextureView
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.startActivity
import com.bumptech.glide.Glide

open class BaseActivity : AppCompatActivity() {

    lateinit var drawerLayout: DrawerLayout
    lateinit var navView: NavigationView
    private val CAMERA_PERMISSION_REQUEST_CODE = 100


    private lateinit var sharedPreferences: SharedPreferences
    private val prefsListener = SharedPreferences.OnSharedPreferenceChangeListener { prefs, key ->
        if (key == "profile_image_url") {
            Log.d("MainActivity", "Profile image URL changed")
            loadProfileImage()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = getSharedPreferences("user_session", MODE_PRIVATE)

        // Register SharedPreferences listener
        sharedPreferences.registerOnSharedPreferenceChangeListener(prefsListener)
    }

    fun setupDrawer() {
        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)
        sharedPreferences = getSharedPreferences("user_session", MODE_PRIVATE)
        // Register SharedPreferences listener
        sharedPreferences.registerOnSharedPreferenceChangeListener(prefsListener)

        val openDrawerButton = findViewById<ImageButton>(R.id.openDrawerButton)
        openDrawerButton?.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    Log.d("BaseActivity", "Home selected")
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    drawerLayout.closeDrawer(GravityCompat.START)
                }

                R.id.nav_learn -> {
                    Log.d("BaseActivity", "Learn selected")
                    val intent = Intent(this, LearnActivity::class.java)
                    startActivity(intent)
                    drawerLayout.closeDrawer(GravityCompat.START)
                }

                R.id.nav_upload_and_learn -> {
                    Log.d("BaseActivity", "Upload and Learn selected")
                    val intent = Intent(this, PhotoModelActivity::class.java)
                    startActivity(intent)
                    drawerLayout.closeDrawer(GravityCompat.START)
                }

                R.id.nav_logout -> {
                    Log.d("BaseActivity", "Logout selected")
                    logoutUser()
                }
            }
            true
        }


        // Profile image click listener
        val profileImageView =
            navView.getHeaderView(0).findViewById<ImageView>(R.id.profileImageView)
        profileImageView.setOnClickListener {
            openUserProfile()
        }

        // Load user profile image
        loadProfileImage()
    }


    private fun loadProfileImage() {
        val profileImageUrl = sharedPreferences.getString("profile_image_url", null)
        val profileImageView =
            navView.getHeaderView(0).findViewById<ImageView>(R.id.profileImageView)
        if (!profileImageUrl.isNullOrEmpty()) {
            Log.d(
                "MainActivity",
                "Profile image URL: $profileImageUrl"
            )  // Log the profile image URL
            Glide.with(this).load(profileImageUrl).circleCrop()
                .into(profileImageView)
        } else {
            Log.d("MainActivity", "Profile image URL is null or empty")
        }
    }

    private fun logoutUser() {
        // Clear user session or perform any necessary logout operations
        // For example, clear shared preferences
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()

        // Navigate to LoginActivity
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun openUserProfile() {
        val intent = Intent(this, UserProfileActivity::class.java)
        startActivity(intent)
    }
}

