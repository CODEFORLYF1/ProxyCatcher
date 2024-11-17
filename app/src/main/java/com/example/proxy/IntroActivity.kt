package com.example.proxy

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class IntroActivity : AppCompatActivity() {

    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        val logoImageView: ImageView = findViewById(R.id.logo_image)

        val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        val fadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out)

        fadeOut.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {
                // No action needed
            }

            override fun onAnimationEnd(animation: Animation?) {
                val intent = Intent(this@IntroActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }

            override fun onAnimationRepeat(animation: Animation?) {
                // No action needed
            }
        })

        // Start the audio
        mediaPlayer = MediaPlayer.create(this, R.raw.intro) // Load the audio file
        mediaPlayer?.start() // Start playing

        // Set up animations
        logoImageView.startAnimation(fadeIn)
        logoImageView.startAnimation(fadeOut)

        // Stop the audio after 4 seconds (duration of the intro animation)
        Handler().postDelayed({
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
        }, 4000) // Duration in milliseconds
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
