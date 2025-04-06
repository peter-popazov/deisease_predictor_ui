package com.peter.rgr

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.button.MaterialButton
import org.json.JSONObject
import java.io.File
import kotlin.math.pow
import kotlin.math.round

class PatientDetailsActivity : AppCompatActivity() {
    
    private lateinit var editTextAge: TextInputEditText
    private lateinit var spinnerGender: AutoCompleteTextView
    private lateinit var editTextHeight: TextInputEditText
    private lateinit var editTextWeight: TextInputEditText
    private lateinit var spinnerEducation: AutoCompleteTextView
    private lateinit var spinnerEthnicity: AutoCompleteTextView
    private lateinit var textViewBMI: android.widget.TextView
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_details)
        
        // Initialize views
        initializeViews()
        setupSpinners()
        setupBMICalculation()
        
        // Set up navigation
        findViewById<MaterialButton>(R.id.buttonNext).setOnClickListener {
            if (validateInputs()) {
                savePersonalData()
                // Navigate to Memory Assessment screen
                startActivity(Intent(this, MemoryAssessmentActivity::class.java))
            }
        }
    }
    
    private fun initializeViews() {
        editTextAge = findViewById(R.id.editTextAge)
        spinnerGender = findViewById(R.id.spinnerGender)
        editTextHeight = findViewById(R.id.editTextHeight)
        editTextWeight = findViewById(R.id.editTextWeight)
        spinnerEducation = findViewById(R.id.spinnerEducation)
        spinnerEthnicity = findViewById(R.id.spinnerEthnicity)
        textViewBMI = findViewById(R.id.textViewBMI)
    }
    
    private fun setupSpinners() {
        // Gender options
        val genderAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, 
            arrayOf("Male", "Female"))
        spinnerGender.setAdapter(genderAdapter)
        
        // Education options
        val educationAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line,
            arrayOf("None", "High School", "Bachelor's", "Higher"))
        spinnerEducation.setAdapter(educationAdapter)
        
        // Ethnicity options
        val ethnicityAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line,
            arrayOf("Caucasian", "African American", "Asian", "Other"))
        spinnerEthnicity.setAdapter(ethnicityAdapter)
    }
    
    private fun setupBMICalculation() {
        val bmiWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                calculateBMI()
            }
        }
        
        editTextHeight.addTextChangedListener(bmiWatcher)
        editTextWeight.addTextChangedListener(bmiWatcher)
    }
    
    private fun calculateBMI() {
        val height = editTextHeight.text.toString().toFloatOrNull()
        val weight = editTextWeight.text.toString().toFloatOrNull()
        
        if (height != null && weight != null && height > 0) {
            val heightInMeters = height / 100
            val bmi = weight / (heightInMeters.pow(2))
            val roundedBMI = round(bmi * 10) / 10
            textViewBMI.text = "BMI: $roundedBMI"
        } else {
            textViewBMI.text = "BMI: --"
        }
    }
    
    private fun validateInputs(): Boolean {
        var isValid = true
        
        if (editTextAge.text.toString().trim().isEmpty()) {
            editTextAge.error = "Please enter age"
            isValid = false
        }
        
        if (spinnerGender.text.toString().trim().isEmpty()) {
            spinnerGender.error = "Please select gender"
            isValid = false
        }
        
        if (editTextHeight.text.toString().trim().isEmpty()) {
            editTextHeight.error = "Please enter height"
            isValid = false
        }
        
        if (editTextWeight.text.toString().trim().isEmpty()) {
            editTextWeight.error = "Please enter weight"
            isValid = false
        }
        
        if (spinnerEducation.text.toString().trim().isEmpty()) {
            spinnerEducation.error = "Please select education level"
            isValid = false
        }
        
        if (spinnerEthnicity.text.toString().trim().isEmpty()) {
            spinnerEthnicity.error = "Please select ethnicity"
            isValid = false
        }
        
        return isValid
    }
    
    private fun savePersonalData() {
        val height = editTextHeight.text.toString().toFloatOrNull() ?: 0f
        val weight = editTextWeight.text.toString().toFloatOrNull() ?: 0f
        val bmi = if (height > 0) {
            val heightInMeters = height / 100
            round((weight / (heightInMeters.pow(2))) * 10) / 10
        } else 0.0
        
        val jsonObject = JSONObject().apply {
            put("age", editTextAge.text.toString().toIntOrNull() ?: 0)
            put("gender", spinnerGender.text.toString())
            put("height", height)
            put("weight", weight)
            put("bmi", bmi)
            put("educationLevel", spinnerEducation.text.toString())
            put("ethnicity", spinnerEthnicity.text.toString())
        }
        
        // Save to file
        val file = File(filesDir, "personal_data.json")
        file.writeText(jsonObject.toString())
        
        // Also save to shared preferences for easy access
        val sharedPrefs = getSharedPreferences("AlzheimerAssessment", MODE_PRIVATE)
        with(sharedPrefs.edit()) {
            putString("personal_data", jsonObject.toString())
            apply()
        }
    }
} 