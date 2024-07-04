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
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import com.bumptech.glide.Glide

open class BaseActivity : AppCompatActivity() {

    lateinit var drawerLayout: DrawerLayout
    lateinit var navView: NavigationView
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var drawerToggle: ActionBarDrawerToggle

    private val prefsListener = SharedPreferences.OnSharedPreferenceChangeListener { prefs, key ->
        if (key == "profile_image_url" || key == "username") {
            Log.d("BaseActivity", "Profile data changed")
            updateNavigationHeader()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = getSharedPreferences("user_session", MODE_PRIVATE)

        // Register SharedPreferences listener
        sharedPreferences.registerOnSharedPreferenceChangeListener(prefsListener)
    }

    override fun setContentView(layoutResID: Int) {
        super.setContentView(layoutResID)
        setupDrawer()
    }

    protected fun setupDrawer() {
        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbarUser)
        setSupportActionBar(toolbar)

        // Enable home button as up
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.title = "                Gesture गुरु"

        drawerToggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

        drawerToggle.drawerArrowDrawable.color = ContextCompat.getColor(this, R.color.white)


        // Ensure navigation header is updated after setting up the drawer
        updateNavigationHeader()

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
                R.id.nav_play_quiz -> {
                    Log.d("BaseActivity", "play Quiz selected")
                    val intent = Intent(this, PlayQuizActivity::class.java)
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

        val profileImageView = navView.getHeaderView(0).findViewById<ImageView>(R.id.profileImageView)
        profileImageView.setOnClickListener {
            openUserProfile()
        }
    }

    protected fun updateNavigationHeader() {
        // Check if the activity is still valid before updating the header
        if (isDestroyed || isFinishing) {
            Log.d("BaseActivity", "Activity is destroyed or finishing, skipping update")
            return
        }

        val username = sharedPreferences.getString("username", "Username")
        val profileImageUrl = sharedPreferences.getString("profile_image_url", null)

        val headerView = navView.getHeaderView(0)
        val usernameTextViewNav = headerView.findViewById<TextView>(R.id.usernameTextViewNav)
        val profileImageView = headerView.findViewById<ImageView>(R.id.profileImageView)

        usernameTextViewNav.text = username

        if (!profileImageUrl.isNullOrEmpty()) {
            Log.d("BaseActivity", "Profile image URL: $profileImageUrl")
            Glide.with(this).load(profileImageUrl).circleCrop().into(profileImageView)
        } else {
            profileImageView.setImageResource(R.drawable.default_profile_image) // Placeholder image
            Glide.with(this).load(profileImageUrl).circleCrop().into(profileImageView)
        }
    }

    private fun logoutUser() {
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()

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

