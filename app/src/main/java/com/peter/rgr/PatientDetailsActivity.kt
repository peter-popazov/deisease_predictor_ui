package com.peter.rgr

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class PatientDetailsActivity : AppCompatActivity() {
    
    private lateinit var editTextFullName: EditText
    private lateinit var editTextDateOfBirth: EditText
    private lateinit var editTextBloodType: EditText
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_details)
        
        // Initialize views
        editTextFullName = findViewById(R.id.editTextFullName)
        editTextDateOfBirth = findViewById(R.id.editTextDateOfBirth)
        editTextBloodType = findViewById(R.id.editTextBloodType)
        
        // Set up navigation
        findViewById<MaterialButton>(R.id.buttonNext).setOnClickListener {
            if (validateInputs()) {
                savePatientData()
                // Navigate to Memory Assessment screen
                startActivity(Intent(this, MemoryAssessmentActivity::class.java))
            }
        }
    }
    
    private fun validateInputs(): Boolean {
        var isValid = true
        
        if (editTextFullName.text.toString().trim().isEmpty()) {
            editTextFullName.error = "Please enter full name"
            isValid = false
        }
        
        if (editTextDateOfBirth.text.toString().trim().isEmpty()) {
            editTextDateOfBirth.error = "Please enter date of birth"
            isValid = false
        }
        
        if (editTextBloodType.text.toString().trim().isEmpty()) {
            editTextBloodType.error = "Please enter blood type"
            isValid = false
        }
        
        return isValid
    }
    
    private fun savePatientData() {
        // Save patient data to shared preferences
        val sharedPrefs = getSharedPreferences("AlzheimerAssessment", MODE_PRIVATE)
        with(sharedPrefs.edit()) {
            putString("full_name", editTextFullName.text.toString())
            putString("date_of_birth", editTextDateOfBirth.text.toString())
            putString("blood_type", editTextBloodType.text.toString())
            apply()
        }
    }
} 