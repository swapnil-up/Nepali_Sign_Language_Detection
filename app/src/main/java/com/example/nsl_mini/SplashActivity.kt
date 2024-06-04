package com.example.nsl_mini

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    private var hasStartedNextActivity = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val videoView: VideoView = findViewById(R.id.splashVideo)
        val videoUri = Uri.parse("android.resource://" + packageName + "/" + R.raw.splash_video)

        videoView.setVideoURI(videoUri)
        videoView.setOnCompletionListener {
            startNextActivity()
        }
        videoView.start()

        // In case the video doesn't play correctly, fallback to a handler
        Handler(Looper.getMainLooper()).postDelayed({
            if (!videoView.isPlaying) {
                startNextActivity()
            }
        }, 6000) // Adjust the delay to be a bit longer than the video duration if necessary
    }

    private fun startNextActivity() {
        if (!hasStartedNextActivity) {
            hasStartedNextActivity = true
            val intent = Intent(this@SplashActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
