package com.example.proxy

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class GameActivity : AppCompatActivity() {

    private lateinit var textBox: TextView
    private lateinit var proxyButton: Button
    private lateinit var lifeTextView: TextView
    private lateinit var resultTextView: TextView
    private lateinit var guessField: EditText
    private lateinit var gifImageView: ImageView
    private val handler = Handler()
    private var rollNumber = 2341600
    private val rollNumberMin = 2341601
    private val rollNumberMax = 2341615
    private var proxyNumber: Int = -1
    private var guessedCorrectly = false
    private var mediaPlayer: MediaPlayer? = null
    private var lives = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        textBox = findViewById(R.id.text_box)
        proxyButton = findViewById(R.id.button_proxy)
        lifeTextView = findViewById(R.id.life_text)
        resultTextView = findViewById(R.id.result_text)
        guessField = findViewById(R.id.guess_field)
        gifImageView = findViewById(R.id.gif_view)

        // Initialize game state
        startGame()

        proxyButton.setOnClickListener {
            checkGuess(rollNumber - 1)
        }

        findViewById<Button>(R.id.submit_button).setOnClickListener {
            val guess = guessField.text.toString().toIntOrNull()
            if (guess != null) {
                checkGuess(guess)
            } else {
                Toast.makeText(this, "Please enter a valid roll number", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun startGame() {
        rollNumber = rollNumberMin
        proxyNumber = (rollNumberMin + 1..rollNumberMax).random() // Ensures proxy number is never the first roll number
        guessedCorrectly = false
        lives = 3
        updateLives()
        guessField.visibility = View.GONE // Ensure the guess field is hidden initially
        startRollNumberUpdater()
    }

    private fun startRollNumberUpdater() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                if (rollNumber <= rollNumberMax) {
                    textBox.text = "Roll number $rollNumber"
                    playVoiceForRollNumber(rollNumber)
                    rollNumber++
                    if (rollNumber <= rollNumberMax) {
                        handler.postDelayed(this, 5000)
                    } else {
                        proxyButton.isEnabled = false
                        guessField.visibility = View.VISIBLE // Show guess field after 15th voice
                        findViewById<Button>(R.id.submit_button).visibility = View.VISIBLE
                        guessField.isEnabled = true
                    }
                }
            }
        }, 0)
    }

    private fun playVoiceForRollNumber(rollNumber: Int) {
        mediaPlayer?.release()
        val voiceResId = resources.getIdentifier("roll_$rollNumber", "raw", packageName)
        if (voiceResId != 0) {
            mediaPlayer = MediaPlayer.create(this, voiceResId)
            mediaPlayer?.setOnCompletionListener {
                if (rollNumber == proxyNumber && !guessedCorrectly) {
                    mediaPlayer?.start()
                }
            }
            mediaPlayer?.start()
        } else {
            Log.e("GameActivity", "Voice resource for roll number $rollNumber not found")
        }
    }

    private fun checkGuess(guess: Int) {
        if (guess == proxyNumber) {
            playCorrectSound()
            showCelebration()
        } else {
            lives--
            updateLives()
            if (lives > 0) {
                resultTextView.text = "Incorrect! You have $lives lives remaining."
            } else {
                playLoserSound()
                resultTextView.text = "No lives left! Redirecting to the home page..."
                handler.postDelayed({
                    finish() // Redirect to the home screen after losing all lives
                }, 3000)
            }
        }
    }

    private fun playCorrectSound() {
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer.create(this, R.raw.correct) // Ensure you have correct.mp3 in res/raw
        mediaPlayer?.start()
    }

    private fun playLoserSound() {
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer.create(this, R.raw.loser) // Ensure you have loser.mp3 in res/raw
        mediaPlayer?.start()
    }

    private fun showCelebration() {
        resultTextView.text = "Congratulations! You guessed correctly!"
        gifImageView.setImageResource(R.drawable.celebration)
        gifImageView.visibility = ImageView.VISIBLE
        handler.postDelayed({
            // Redirect to home screen after showing the GIF
            finish()
        }, 3000)
    }

    private fun updateLives() {
        lifeTextView.text = "Lives: ${"❤".repeat(lives)}"
    }

    private fun restartGame() {
        resultTextView.text = ""
        gifImageView.visibility = ImageView.GONE
        guessField.isEnabled = false
        findViewById<Button>(R.id.submit_button).visibility = View.GONE
        startGame()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
