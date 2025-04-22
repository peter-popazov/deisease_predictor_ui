package com.peter.rgr

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.google.android.material.button.MaterialButton
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.android.material.slider.Slider
import com.google.android.material.textfield.TextInputEditText
import com.peter.rgr.viewmodel.MedicalHistoryViewModel

class MedicalHistoryActivity : AppCompatActivity() {
    private val viewModel: MedicalHistoryViewModel by viewModels()
    
    private lateinit var checkBoxDiabetes: MaterialCheckBox
    private lateinit var checkBoxHypertension: MaterialCheckBox
    private lateinit var checkBoxCardiovascularDisease: MaterialCheckBox
    private lateinit var checkBoxHeadInjury: MaterialCheckBox
    private lateinit var editTextSystolicBP: TextInputEditText
    private lateinit var editTextDiastolicBP: TextInputEditText
    private lateinit var sliderAlcohol: Slider
    private lateinit var textAlcoholValue: TextView
    private lateinit var spinnerDietQuality: AutoCompleteTextView
    private lateinit var spinnerSleepQuality: AutoCompleteTextView
    private lateinit var spinnerSmoking: AutoCompleteTextView
    private lateinit var buttonNext: MaterialButton
    private lateinit var buttonPrevious: MaterialButton
    private lateinit var progressBar: LinearProgressIndicator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_medical_history)

        try {
            initializeViews()
            setupSpinners()
            setupNavigation()
            setupFieldListeners()
            observeViewModel()
        } catch (e: Exception) {
            Toast.makeText(this, "Error initializing activity: ${e.message}", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun initializeViews() {
        checkBoxDiabetes = findViewById(R.id.checkBoxDiabetes)
        checkBoxHypertension = findViewById(R.id.checkBoxHypertension)
        checkBoxCardiovascularDisease = findViewById(R.id.checkBoxCardiovascularDisease)
        checkBoxHeadInjury = findViewById(R.id.checkBoxHeadInjury)
        editTextSystolicBP = findViewById(R.id.editTextSystolicBP)
        editTextDiastolicBP = findViewById(R.id.editTextDiastolicBP)
        sliderAlcohol = findViewById(R.id.sliderAlcohol)
        textAlcoholValue = findViewById(R.id.textAlcoholValue)
        spinnerDietQuality = findViewById(R.id.spinnerDietQuality)
        spinnerSleepQuality = findViewById(R.id.spinnerSleepQuality)
        spinnerSmoking = findViewById(R.id.spinnerSmoking)
        buttonNext = findViewById(R.id.buttonNext)
        buttonPrevious = findViewById(R.id.buttonPrevious)
        progressBar = findViewById(R.id.progressBar)
    }

    private fun setupFieldListeners() {
        val textWatcher = object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: android.text.Editable?) {
                viewModel.updateMedicalHistory(
                    systolicBP = editTextSystolicBP.text.toString(),
                    diastolicBP = editTextDiastolicBP.text.toString()
                )
            }
        }

        editTextSystolicBP.addTextChangedListener(textWatcher)
        editTextDiastolicBP.addTextChangedListener(textWatcher)

        checkBoxDiabetes.setOnCheckedChangeListener { _, isChecked ->
            viewModel.updateMedicalHistory(diabetes = isChecked)
        }
        checkBoxHypertension.setOnCheckedChangeListener { _, isChecked ->
            viewModel.updateMedicalHistory(hypertension = isChecked)
        }
        checkBoxCardiovascularDisease.setOnCheckedChangeListener { _, isChecked ->
            viewModel.updateMedicalHistory(cardiovascularDisease = isChecked)
        }
        checkBoxHeadInjury.setOnCheckedChangeListener { _, isChecked ->
            viewModel.updateMedicalHistory(headInjury = isChecked)
        }

        sliderAlcohol.addOnChangeListener { _, value, fromUser ->
            if (fromUser) {
                textAlcoholValue.text = "${value.toInt()} units"
                viewModel.updateMedicalHistory(alcoholConsumption = value.toInt())
            }
        }

        spinnerDietQuality.setOnItemClickListener { _, _, _, _ ->
            viewModel.updateMedicalHistory(dietQuality = spinnerDietQuality.text.toString())
        }
        spinnerSleepQuality.setOnItemClickListener { _, _, _, _ ->
            viewModel.updateMedicalHistory(sleepQuality = spinnerSleepQuality.text.toString())
        }
        spinnerSmoking.setOnItemClickListener { _, _, _, _ ->
            viewModel.updateMedicalHistory(smoking = spinnerSmoking.text.toString())
        }
    }

    private fun observeViewModel() {
        viewModel.medicalHistory.observe(this, Observer { medicalHistory ->
            checkBoxDiabetes.isChecked = medicalHistory.diabetes
            checkBoxHypertension.isChecked = medicalHistory.hypertension
            checkBoxCardiovascularDisease.isChecked = medicalHistory.cardiovascularDisease
            checkBoxHeadInjury.isChecked = medicalHistory.headInjury
            editTextSystolicBP.setText(medicalHistory.systolicBP)
            editTextDiastolicBP.setText(medicalHistory.diastolicBP)
            sliderAlcohol.value = medicalHistory.alcoholConsumption.toFloat()
            textAlcoholValue.text = "${medicalHistory.alcoholConsumption} units"
            spinnerDietQuality.setText(medicalHistory.dietQuality, false)
            spinnerSleepQuality.setText(medicalHistory.sleepQuality, false)
            spinnerSmoking.setText(medicalHistory.smoking, false)
        })

        viewModel.progress.observe(this, Observer { progress ->
            progressBar.progress = progress
        })

        viewModel.error.observe(this, Observer { error ->
            error?.let { Toast.makeText(this, it, Toast.LENGTH_SHORT).show() }
        })
    }

    private fun setupSpinners() {
        val dietOptions = arrayOf("Very Poor", "Poor", "Fair", "Good", "Very Good", "Excellent")
        val sleepOptions = arrayOf("Very Poor", "Poor", "Fair", "Good", "Very Good", "Excellent", "Perfect")
        val smokingOptions = arrayOf("Never", "Former", "Current")

        val dietAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, dietOptions)
        val sleepAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, sleepOptions)
        val smokingAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, smokingOptions)

        spinnerDietQuality.setAdapter(dietAdapter)
        spinnerSleepQuality.setAdapter(sleepAdapter)
        spinnerSmoking.setAdapter(smokingAdapter)
    }

    private fun setupNavigation() {
        buttonNext.setOnClickListener {
            if (viewModel.validateInputs()) {
                viewModel.saveMedicalHistory()
                val intent = Intent(this, CognitiveSymptomsActivity::class.java)
                startActivity(intent)
            }
        }

        buttonPrevious.setOnClickListener {
            finish()
        }
    }
}