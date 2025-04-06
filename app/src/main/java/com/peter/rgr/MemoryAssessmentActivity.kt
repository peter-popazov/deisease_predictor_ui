package com.peter.rgr

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.GridLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import java.util.*
import kotlin.random.Random

class MemoryAssessmentActivity : AppCompatActivity() {
    
    private lateinit var textViewWordList: TextView
    private lateinit var editTextRecalledWords: EditText
    private lateinit var buttonStartWordRecall: MaterialButton
    
    private lateinit var textViewSequence: TextView
    private lateinit var editTextSequenceRecall: EditText
    private lateinit var buttonStartSequence: MaterialButton
    
    private lateinit var gridPattern: GridLayout
    private lateinit var buttonStartVisual: MaterialButton
    
    private lateinit var editTextDate: EditText
    private lateinit var editTextDay: EditText
    private lateinit var editTextLocation: EditText
    
    private val wordsList = listOf(
        "HOUSE", "TREE", "CAT", "BOOK", "CHAIR",
        "PHONE", "CLOCK", "DOOR", "WINDOW", "TABLE"
    )
    private var selectedWords = mutableListOf<String>()
    private var currentSequence = ""
    private var visualPattern = mutableListOf<Int>()
    private val handler = Handler(Looper.getMainLooper())
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_memory_assessment)
        
        initializeViews()
        setupWordRecallTest()
        setupSequenceTest()
        setupVisualTest()
        setupNavigation()
    }
    
    private fun initializeViews() {
        // Word Recall Test views
        textViewWordList = findViewById(R.id.textViewWordList)
        editTextRecalledWords = findViewById(R.id.editTextRecalledWords)
        buttonStartWordRecall = findViewById(R.id.buttonStartWordRecall)
        
        // Sequence Test views
        textViewSequence = findViewById(R.id.textViewSequence)
        editTextSequenceRecall = findViewById(R.id.editTextSequenceRecall)
        buttonStartSequence = findViewById(R.id.buttonStartSequence)
        
        // Visual Test views
        gridPattern = findViewById(R.id.gridPattern)
        buttonStartVisual = findViewById(R.id.buttonStartVisual)
        
        // Orientation Test views
        editTextDate = findViewById(R.id.editTextDate)
        editTextDay = findViewById(R.id.editTextDay)
        editTextLocation = findViewById(R.id.editTextLocation)
        
        // Setup grid for visual pattern test
        setupVisualGrid()
    }
    
    private fun setupWordRecallTest() {
        buttonStartWordRecall.setOnClickListener {
            selectedWords = wordsList.shuffled().take(5).toMutableList()
            textViewWordList.text = selectedWords.joinToString(" ")
            
            // Hide words after 10 seconds
            handler.postDelayed({
                textViewWordList.text = "Try to recall the words shown"
                editTextRecalledWords.visibility = View.VISIBLE
                buttonStartWordRecall.isEnabled = false
            }, 10000)
        }
    }
    
    private fun setupSequenceTest() {
        buttonStartSequence.setOnClickListener {
            currentSequence = generateSequence()
            textViewSequence.text = currentSequence
            
            // Hide sequence after 5 seconds
            handler.postDelayed({
                textViewSequence.text = "Enter the numbers you saw"
                editTextSequenceRecall.visibility = View.VISIBLE
                buttonStartSequence.isEnabled = false
            }, 5000)
        }
    }
    
    private fun setupVisualGrid() {
        for (i in 0 until 9) {
            val button = Button(this).apply {
                layoutParams = GridLayout.LayoutParams().apply {
                    width = 0
                    height = 0
                    columnSpec = GridLayout.spec(i % 3, 1f)
                    rowSpec = GridLayout.spec(i / 3, 1f)
                    setMargins(4, 4, 4, 4)
                }
                setBackgroundColor(ContextCompat.getColor(context, android.R.color.white))
                tag = i
            }
            gridPattern.addView(button)
        }
    }
    
    private fun setupVisualTest() {
        buttonStartVisual.setOnClickListener {
            visualPattern = generateVisualPattern()
            showPattern(visualPattern)
            buttonStartVisual.isEnabled = false
            
            // Hide pattern after 5 seconds and enable grid for user input
            handler.postDelayed({
                clearPattern()
                enableGridForInput()
            }, 5000)
        }
    }
    
    private fun generateSequence(): String {
        return (1..6).joinToString("") { Random.nextInt(1, 10).toString() }
    }
    
    private fun generateVisualPattern(): MutableList<Int> {
        return (0..8).shuffled().take(4).toMutableList()
    }
    
    private fun showPattern(pattern: List<Int>) {
        pattern.forEach { index ->
            gridPattern.getChildAt(index).setBackgroundColor(
                ContextCompat.getColor(this, android.R.color.holo_blue_light)
            )
        }
    }
    
    private fun clearPattern() {
        for (i in 0 until gridPattern.childCount) {
            gridPattern.getChildAt(i).setBackgroundColor(
                ContextCompat.getColor(this, android.R.color.white)
            )
        }
    }
    
    private fun enableGridForInput() {
        val userPattern = mutableListOf<Int>()
        
        for (i in 0 until gridPattern.childCount) {
            val cell = gridPattern.getChildAt(i)
            cell.isClickable = true
            cell.setOnClickListener { view ->
                view.setBackgroundColor(
                    ContextCompat.getColor(this, android.R.color.holo_blue_light)
                )
                userPattern.add(view.tag as Int)
                
                if (userPattern.size == visualPattern.size) {
                    // Store the score
                    val score = userPattern.intersect(visualPattern.toSet()).size
                    saveVisualTestScore(score)
                    disableGrid()
                }
            }
        }
    }
    
    private fun disableGrid() {
        for (i in 0 until gridPattern.childCount) {
            gridPattern.getChildAt(i).isClickable = false
        }
    }
    
    private fun setupNavigation() {
        findViewById<MaterialButton>(R.id.buttonNext).setOnClickListener {
            if (validateAndSaveResults()) {
                startActivity(Intent(this, CognitiveAssessmentActivity::class.java))
            }
        }
        
        findViewById<MaterialButton>(R.id.buttonPrevious).setOnClickListener {
            onBackPressed()
        }
    }
    
    private fun validateAndSaveResults(): Boolean {
        // Validate word recall
        val recalledWords = editTextRecalledWords.text.toString()
            .uppercase()
            .split(Regex("\\s+"))
            .filter { it.isNotEmpty() }
        val wordRecallScore = recalledWords.count { it in selectedWords }
        
        // Validate sequence recall
        val sequenceRecall = editTextSequenceRecall.text.toString()
        val sequenceScore = if (sequenceRecall == currentSequence) 1 else 0
        
        // Validate orientation
        if (editTextDate.text.toString().isEmpty() ||
            editTextDay.text.toString().isEmpty() ||
            editTextLocation.text.toString().isEmpty()
        ) {
            return false
        }
        
        // Save all results
        val sharedPrefs = getSharedPreferences("AlzheimerAssessment", MODE_PRIVATE)
        with(sharedPrefs.edit()) {
            putInt("word_recall_score", wordRecallScore)
            putInt("sequence_score", sequenceScore)
            putString("date_recall", editTextDate.text.toString())
            putString("day_recall", editTextDay.text.toString())
            putString("location_recall", editTextLocation.text.toString())
            apply()
        }
        
        return true
    }
    
    private fun saveVisualTestScore(score: Int) {
        val sharedPrefs = getSharedPreferences("AlzheimerAssessment", MODE_PRIVATE)
        sharedPrefs.edit().putInt("visual_test_score", score).apply()
    }
    
    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
} 