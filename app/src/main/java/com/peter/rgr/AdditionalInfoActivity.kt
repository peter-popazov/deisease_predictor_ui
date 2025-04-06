package com.peter.rgr

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.slider.Slider

class AdditionalInfoActivity : AppCompatActivity() {
    
    private lateinit var editTextAge: EditText
    private lateinit var radioGroupGender: RadioGroup
    private lateinit var editTextEducation: EditText
    private lateinit var sliderSES: Slider
    private lateinit var sliderMMSE: Slider
    private lateinit var spinnerCDR: Spinner
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_additional_info)
        
        initializeViews()
        setupCDRSpinner()
        setupNavigation()
    }
    
    private fun initializeViews() {
        editTextAge = findViewById(R.id.editTextAge)
        radioGroupGender = findViewById(R.id.radioGroupGender)
        editTextEducation = findViewById(R.id.editTextEducation)
        sliderSES = findViewById(R.id.sliderSES)
        sliderMMSE = findViewById(R.id.sliderMMSE)
        spinnerCDR = findViewById(R.id.spinnerCDR)
    }
    
    private fun setupCDRSpinner() {
        val cdrOptions = arrayOf("0 - Normal", "0.5 - Very Mild", "1 - Mild", "2 - Moderate", "3 - Severe")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, cdrOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCDR.adapter = adapter
    }
    
    private fun setupNavigation() {
        findViewById<MaterialButton>(R.id.buttonPrevious).setOnClickListener {
            onBackPressed()
        }
        
        findViewById<MaterialButton>(R.id.buttonCalculate).setOnClickListener {
            if (validateInputs()) {
                calculateAndShowResults()
            }
        }
    }
    
    private fun validateInputs(): Boolean {
        if (editTextAge.text.toString().isEmpty()) {
            editTextAge.error = "Please enter age"
            return false
        }
        
        val age = editTextAge.text.toString().toInt()
        if (age < 40 || age > 90) {
            editTextAge.error = "Age must be between 40 and 90"
            return false
        }
        
        if (radioGroupGender.checkedRadioButtonId == -1) {
            Toast.makeText(this, "Please select gender", Toast.LENGTH_SHORT).show()
            return false
        }
        
        if (editTextEducation.text.toString().isEmpty()) {
            editTextEducation.error = "Please enter years of education"
            return false
        }
        
        return true
    }
    
    private fun calculateAndShowResults() {
        // Get test scores from shared preferences
        val sharedPrefs = getSharedPreferences("AlzheimerAssessment", MODE_PRIVATE)
        val wordRecallScore = sharedPrefs.getInt("word_recall_score", 0)
        val sequenceScore = sharedPrefs.getInt("sequence_score", 0)
        val visualScore = sharedPrefs.getInt("visual_test_score", 0)
        
        // Calculate memory and orientation scores
        val memoryScore = calculateMemoryScore(wordRecallScore, sequenceScore)
        val orientationScore = calculateOrientationScore()
        
        // Get cognitive score from previous assessment
        val cognitiveScore = sharedPrefs.getInt("cognitive_score", 0)
        
        // Get user inputs
        val age = editTextAge.text.toString().toInt()
        val gender = if (radioGroupGender.checkedRadioButtonId == R.id.radioMale) 0 else 1
        val education = editTextEducation.text.toString().toInt()
        val ses = sliderSES.value.toInt()
        val mmse = sliderMMSE.value.toInt()
        val cdr = spinnerCDR.selectedItemPosition.toFloat() / 2 // Convert to CDR scale (0-3)
        
        // Calculate risk using the predictor
        val result = AlzheimerPredictor.calculateRisk(
            age = age,
            gender = gender,
            education = education,
            socioEconomicStatus = ses,
            mmseScore = mmse,
            cdScore = cdr.toInt(),
            memoryScore = memoryScore,
            orientationScore = orientationScore,
            visualScore = visualScore,
            cognitiveScore = cognitiveScore
        )
        
        // Save results
        with(sharedPrefs.edit()) {
            putString("risk_level", result.riskLevel.toString())
            putFloat("risk_probability", result.probability.toFloat())
            putStringSet("recommendations", result.recommendations.toSet())
            apply()
        }
        
        // Navigate to results screen
        startActivity(Intent(this, ResultsActivity::class.java))
    }
    
    private fun calculateMemoryScore(wordRecall: Int, sequence: Int): Int {
        // Convert word recall (0-5) and sequence (0-1) to 0-100 scale
        val wordScore = (wordRecall.toDouble() / 5 * 100).toInt()
        val sequenceScore = sequence * 100
        
        return (wordScore * 0.7 + sequenceScore * 0.3).toInt()
    }
    
    private fun calculateOrientationScore(): Int {
        val sharedPrefs = getSharedPreferences("AlzheimerAssessment", MODE_PRIVATE)
        val dateRecall = !sharedPrefs.getString("date_recall", "").isNullOrEmpty()
        val dayRecall = !sharedPrefs.getString("day_recall", "").isNullOrEmpty()
        val locationRecall = !sharedPrefs.getString("location_recall", "").isNullOrEmpty()
        
        // Calculate percentage of correct orientation answers
        val correctAnswers = listOf(dateRecall, dayRecall, locationRecall).count { it }
        return (correctAnswers.toDouble() / 3 * 100).toInt()
    }
} 