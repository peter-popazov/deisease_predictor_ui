package com.peter.rgr

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.slider.Slider

class CognitiveAssessmentActivity : AppCompatActivity() {
    
    private lateinit var sliderProblemSolving: Slider
    private lateinit var spinnerLanguageSkills: Spinner
    private lateinit var editTextAttentionSpan: EditText
    private lateinit var sliderDecisionMaking: Slider
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cognitive_assessment)
        
        // Initialize views
        sliderProblemSolving = findViewById(R.id.sliderProblemSolving)
        spinnerLanguageSkills = findViewById(R.id.spinnerLanguageSkills)
        editTextAttentionSpan = findViewById(R.id.editTextAttentionSpan)
        sliderDecisionMaking = findViewById(R.id.sliderDecisionMaking)
        
        // Set up spinner
        ArrayAdapter.createFromResource(
            this,
            R.array.language_difficulty_options,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerLanguageSkills.adapter = adapter
        }
        
        // Set up navigation
        findViewById<MaterialButton>(R.id.buttonNext).setOnClickListener {
            if (validateInputs()) {
                saveAssessmentData()
                startActivity(Intent(this, ResultsActivity::class.java))
            }
        }
        
        findViewById<MaterialButton>(R.id.buttonPrevious).setOnClickListener {
            onBackPressed()
        }
    }
    
    private fun validateInputs(): Boolean {
        if (editTextAttentionSpan.text.toString().trim().isEmpty()) {
            editTextAttentionSpan.error = "Please enter duration"
            return false
        }
        
        val attentionSpan = editTextAttentionSpan.text.toString().toIntOrNull()
        if (attentionSpan == null || attentionSpan <= 0) {
            editTextAttentionSpan.error = "Please enter a valid duration"
            return false
        }
        
        return true
    }
    
    private fun saveAssessmentData() {
        // Save assessment data to shared preferences or database
        val sharedPrefs = getSharedPreferences("AlzheimerAssessment", MODE_PRIVATE)
        with(sharedPrefs.edit()) {
            putFloat("problem_solving_score", sliderProblemSolving.value)
            putInt("language_skills", spinnerLanguageSkills.selectedItemPosition)
            putInt("attention_span", editTextAttentionSpan.text.toString().toInt())
            putFloat("decision_making", sliderDecisionMaking.value)
            apply()
        }
    }
} 