package com.peter.rgr

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import kotlin.random.Random

class MemoryTestActivity : AppCompatActivity() {

    private lateinit var numberTextView: TextView
    private lateinit var userInput: EditText
    private lateinit var submitAnswer: Button
    private lateinit var scoreTextView: TextView
    private lateinit var startTestButton: Button
    private lateinit var nextButton: Button
    private var generatedNumber: String = ""
    private lateinit var buttonNext: MaterialButton
    private lateinit var buttonPrevious: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            setContentView(R.layout.activity_memory_test)
            Log.d("MemoryTestActivity", "Activity created")

            numberTextView = findViewById(R.id.numberTextView)
            userInput = findViewById(R.id.userInput)
            submitAnswer = findViewById(R.id.submitAnswer)
            scoreTextView = findViewById(R.id.scoreTextView)
            startTestButton = findViewById(R.id.startTestButton)
            nextButton = findViewById(R.id.nextButton)

            startTestButton.setOnClickListener {
                startMemoryTest()
            }

            submitAnswer.setOnClickListener {
                checkAnswer()
            }

            nextButton.setOnClickListener {
                // Navigate to the next activity
                finish()
            }

            // Initial state
            userInput.visibility = View.GONE
            submitAnswer.visibility = View.GONE
            nextButton.visibility = View.GONE

        } catch (e: Exception) {
            Log.e("MemoryTestActivity", "Error in onCreate", e)
            Toast.makeText(this, "Error initializing memory test: ${e.message}", Toast.LENGTH_LONG)
                .show()
        }
    }

    private fun startMemoryTest() {
        try {
            // Generate 5-digit number
            generatedNumber = (1..5).map { Random.nextInt(0, 10) }.joinToString("")
            numberTextView.text = generatedNumber
            userInput.text.clear()
            scoreTextView.text = ""
            userInput.visibility = View.GONE
            submitAnswer.visibility = View.GONE
            nextButton.visibility = View.GONE
            startTestButton.isEnabled = false

            // Show number for 3 seconds
            Handler(Looper.getMainLooper()).postDelayed({
                numberTextView.text = "Now type the number:"
                userInput.visibility = View.VISIBLE
                submitAnswer.visibility = View.VISIBLE
                startTestButton.isEnabled = true
            }, 3000)
        } catch (e: Exception) {
            Log.e("MemoryTestActivity", "Error starting test", e)
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkAnswer() {
        try {
            val input = userInput.text.toString()
            var score = 0
            for (i in input.indices) {
                if (i < generatedNumber.length && input[i] == generatedNumber[i]) {
                    score++
                }
            }
            scoreTextView.text =
                "Correct: $generatedNumber\nYour Score: $score / ${generatedNumber.length}"

            // Save score to SharedPreferences
            val sharedPreferences = getSharedPreferences("PatientData", MODE_PRIVATE)
            sharedPreferences.edit().putInt("memoryTestScore", score).apply()

            // Show next button
            nextButton.visibility = View.VISIBLE
        } catch (e: Exception) {
            Log.e("MemoryTestActivity", "Error checking answer", e)
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupNavigation() {
        buttonNext.setOnClickListener {
            // todo peoceed to result activity
//            val intent = Intent(this, MemoryTestActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)

        }
    }
}
