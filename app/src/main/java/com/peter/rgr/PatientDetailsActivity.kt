package com.peter.rgr

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.peter.rgr.viewmodel.PatientDetailsViewModel

class PatientDetailsActivity : AppCompatActivity() {
    private lateinit var viewModel: PatientDetailsViewModel
    
    private lateinit var editTextAge: TextInputEditText
    private lateinit var spinnerGender: AutoCompleteTextView
    private lateinit var editTextHeight: TextInputEditText
    private lateinit var editTextWeight: TextInputEditText
    private lateinit var spinnerEducation: AutoCompleteTextView
    private lateinit var spinnerEthnicity: AutoCompleteTextView
    private lateinit var textViewBMI: TextView
    private lateinit var buttonNext: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_details)
        viewModel = ViewModelProvider(this)[PatientDetailsViewModel::class.java]
        try {
            Log.d("PatientDetailsActivity", "Starting onCreate")
            initializeViews()
            setupSpinners()
            observeViewModel()
            setupFieldListeners()
            setupNavigation()
        } catch (e: Exception) {
            Log.e("PatientDetailsActivity", "Initialization error", e)
            Toast.makeText(this, "Error initializing activity: ${e.message}", Toast.LENGTH_LONG).show()
            finish()
        }
    }
    
    private fun initializeViews() {
        editTextAge = findViewById(R.id.editTextAge)
        editTextAge = findViewById(R.id.editTextAge)
        spinnerGender = findViewById(R.id.spinnerGender)
        editTextHeight = findViewById(R.id.editTextHeight)
        editTextWeight = findViewById(R.id.editTextWeight)
        spinnerEducation = findViewById(R.id.spinnerEducation)
        spinnerEthnicity = findViewById(R.id.spinnerEthnicity)
        textViewBMI = findViewById(R.id.textViewBMI)
        buttonNext = findViewById(R.id.buttonNext)
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
    
    private fun setupFieldListeners() {
        editTextAge.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: android.text.Editable?) {
                viewModel.updatePatientDetails(age = s.toString().toIntOrNull())
            }
        })

        spinnerGender.setOnItemClickListener { _, _, _, _ ->
            viewModel.updatePatientDetails(gender = spinnerGender.text.toString())
        }

        editTextHeight.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: android.text.Editable?) {
                viewModel.updatePatientDetails(height = s.toString().toFloatOrNull())
            }
        })

        editTextWeight.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: android.text.Editable?) {
                viewModel.updatePatientDetails(weight = s.toString().toFloatOrNull())
            }
        })

        spinnerEducation.setOnItemClickListener { _, _, _, _ ->
            viewModel.updatePatientDetails(educationLevel = spinnerEducation.text.toString())
        }

        spinnerEthnicity.setOnItemClickListener { _, _, _, _ ->
            viewModel.updatePatientDetails(ethnicity = spinnerEthnicity.text.toString())
        }
    }

    private fun observeViewModel() {
        viewModel.patientDetails.observe(this, Observer { details ->
            if (editTextAge.text.toString() != (if (details.age > 0) details.age.toString() else "")) {
                editTextAge.setText(if (details.age > 0) details.age.toString() else "")
            }
            if (spinnerGender.text.toString() != details.gender) {
                spinnerGender.setText(details.gender, false)
            }
            if (editTextHeight.text.toString() != (if (details.height > 0) details.height.toString() else "")) {
                editTextHeight.setText(if (details.height > 0) details.height.toString() else "")
            }
            if (editTextWeight.text.toString() != (if (details.weight > 0) details.weight.toString() else "")) {
                editTextWeight.setText(if (details.weight > 0) details.weight.toString() else "")
            }
            if (spinnerEducation.text.toString() != details.educationLevel) {
                spinnerEducation.setText(details.educationLevel, false)
            }
            if (spinnerEthnicity.text.toString() != details.ethnicity) {
                spinnerEthnicity.setText(details.ethnicity, false)
            }
        })

        viewModel.bmi.observe(this, Observer { bmi ->
            textViewBMI.text = if (bmi > 0) "BMI: $bmi" else "BMI: --"
        })

        viewModel.error.observe(this, Observer { error ->
            error?.let { Toast.makeText(this, it, Toast.LENGTH_SHORT).show() }
        })
    }

    private fun setupNavigation() {
        buttonNext.setOnClickListener {
            if (viewModel.validateInputs()) {
                viewModel.savePatientDetails()
                val intent = Intent(this, MedicalHistoryActivity::class.java)
                startActivity(intent)
            }
        }
    }
} 