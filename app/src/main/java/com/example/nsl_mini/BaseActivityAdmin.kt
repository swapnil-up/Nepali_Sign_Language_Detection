package com.example.nsl_mini

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.core.view.GravityCompat
import android.util.Log
import android.content.Intent
import android.widget.ImageView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.content.ContextCompat

open class BaseActivityAdmin : AppCompatActivity() {

    lateinit var drawerLayout: DrawerLayout
    lateinit var navView: NavigationView
    private lateinit var drawerToggle: ActionBarDrawerToggle

    override fun setContentView(layoutResID: Int) {
        super.setContentView(layoutResID)
        setupDrawerAdmin()
    }

    protected fun setupDrawerAdmin() {
        drawerLayout = findViewById(R.id.drawerLayout)
        navView = findViewById(R.id.navigationViewAdmin)

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbarAdmin)
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

        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_homea -> {
                    Log.d("BaseActivity", "Home selected")
                    val intent = Intent(this, AdminActivity::class.java)
                    startActivity(intent)
                    drawerLayout.closeDrawer(GravityCompat.START)
                }
                R.id.nav_addquiz -> {
                    // Navigate to add quiz question activity
                    val intent = Intent(this, AddQuizActivity::class.java)
                    startActivity(intent)
                }
                R.id.nav_view_quiz -> {
                    // Navigate to add quiz question activity
                    val intent = Intent(this, ViewQuizzesActivity::class.java)
                    startActivity(intent)
                }

                R.id.nav_logout -> {
                    Log.d("BaseActivity", "Logout selected")
                    logoutUser()
                }
                else -> false
            }

            true
        }

    }


    private fun logoutUser() {

        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

}

