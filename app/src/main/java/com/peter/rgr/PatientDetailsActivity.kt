package com.peter.rgr

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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

    private var isUpdatingFromViewModel = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_details)

        viewModel = ViewModelProvider(this)[PatientDetailsViewModel::class.java]

        try {
            initializeViews()
            setupSpinners()
            setupFieldListeners()
            observeViewModel()
            setupNavigation()
        } catch (e: Exception) {
            Log.e("PatientDetailsActivity", "Initialization error", e)
            Toast.makeText(this, "Error initializing activity: ${e.message}", Toast.LENGTH_LONG)
                .show()
            finish()
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
        buttonNext = findViewById(R.id.buttonNext)
    }

    private fun setupSpinners() {
        spinnerGender.setAdapter(
            ArrayAdapter(
                this, android.R.layout.simple_dropdown_item_1line,
                arrayOf("Male", "Female")
            )
        )

        spinnerEducation.setAdapter(
            ArrayAdapter(
                this, android.R.layout.simple_dropdown_item_1line,
                arrayOf("None", "High School", "Bachelor's", "Higher")
            )
        )

        spinnerEthnicity.setAdapter(
            ArrayAdapter(
                this, android.R.layout.simple_dropdown_item_1line,
                arrayOf("Caucasian", "African American", "Asian", "Other")
            )
        )
    }

    private fun setupFieldListeners() {
        editTextAge.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (isUpdatingFromViewModel) return
                val age = s.toString().toIntOrNull()
                if (age == null || age < 0) {
                    editTextAge.error = "Invalid age"
                } else {
                    viewModel.updatePatientDetails(age = age)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        editTextHeight.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val height = s.toString().toFloatOrNull()
                viewModel.updatePatientDetails(height = height)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        editTextWeight.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (isUpdatingFromViewModel) return
                val weight = s.toString().toFloatOrNull()
                viewModel.updatePatientDetails(weight = weight)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        spinnerGender.setOnItemClickListener { parent, _, position, _ ->
            val gender = parent.getItemAtPosition(position).toString()
            viewModel.updatePatientDetails(gender = gender)
        }

        spinnerEducation.setOnItemClickListener { parent, _, position, _ ->
            val education = parent.getItemAtPosition(position).toString()
            viewModel.updatePatientDetails(educationLevel = education)
        }

        spinnerEthnicity.setOnItemClickListener { parent, _, position, _ ->
            val ethnicity = parent.getItemAtPosition(position).toString()
            viewModel.updatePatientDetails(ethnicity = ethnicity)
        }
    }

    private fun observeViewModel() {
        viewModel.bmi.observe(this) { bmi ->
            textViewBMI.text = "BMI: %.2f".format(bmi)
        }

        viewModel.patientDetails.observe(this) { details ->
            Log.d("PatientDetailsActivity", "Observed patient details: $details")

            isUpdatingFromViewModel = true

            if (editTextAge.text.toString() != (details.age.takeIf { it > 0 }?.toString() ?: "")) {
                editTextAge.setText(details.age.takeIf { it > 0 }?.toString() ?: "")
            }

            if (spinnerGender.text.toString() != details.gender) {
                spinnerGender.setText(details.gender, false)
            }

            if (editTextHeight.text.toString() != (details.height.takeIf { it > 0 }?.toString() ?: "")) {
                editTextHeight.setText(details.height.takeIf { it > 0 }?.toString() ?: "")
            }

            if (editTextWeight.text.toString() != (details.weight.takeIf { it > 0 }?.toString() ?: "")) {
                editTextWeight.setText(details.weight.takeIf { it > 0 }?.toString() ?: "")
            }

            if (spinnerEducation.text.toString() != details.educationLevel) {
                spinnerEducation.setText(details.educationLevel, false)
            }

            if (spinnerEthnicity.text.toString() != details.ethnicity) {
                spinnerEthnicity.setText(details.ethnicity, false)
            }

            isUpdatingFromViewModel = false
        }

    }


    private fun setupNavigation() {
        buttonNext.setOnClickListener {
            if (viewModel.validateInputs()) {
                viewModel.savePatientDetails()
                val intent = Intent(this, MedicalHistoryActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Please fill out all required fields.", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
}