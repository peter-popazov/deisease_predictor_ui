package com.peter.rgr

import android.content.Intent
import android.os.Bundle
import android.text.TextWatcher
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.CompoundButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.button.MaterialButton
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.android.material.slider.Slider
import com.google.android.material.textfield.TextInputEditText
import com.peter.rgr.viewmodel.MedicalHistoryViewModel

class MedicalHistoryActivity : AppCompatActivity() {
    private lateinit var viewModel: MedicalHistoryViewModel

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

    private var systolicBPWatcher: TextWatcher? = null
    private var diastolicBPWatcher: TextWatcher? = null

    private var diabetesCheckedChangeListener: CompoundButton.OnCheckedChangeListener? = null
    private var hypertensionCheckedChangeListener: CompoundButton.OnCheckedChangeListener? = null
    private var cardiovascularCheckedChangeListener: CompoundButton.OnCheckedChangeListener? = null
    private var headInjuryCheckedChangeListener: CompoundButton.OnCheckedChangeListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_medical_history)
        viewModel = ViewModelProvider(this)[MedicalHistoryViewModel::class.java]

        try {
            initializeViews()
            setupSpinners()
            setupFieldListeners()
            observeViewModel()
            setupNavigation()
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
        // Initialize text watchers
        systolicBPWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: android.text.Editable?) {
                viewModel.updateMedicalHistory(
                    systolicBP = s.toString().toIntOrNull() ?: 0,
                    diastolicBP = editTextDiastolicBP.text.toString().toIntOrNull() ?: 0
                )
            }
        }

        diastolicBPWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: android.text.Editable?) {
                viewModel.updateMedicalHistory(
                    systolicBP = editTextSystolicBP.text.toString().toIntOrNull() ?: 0,
                    diastolicBP = s.toString().toIntOrNull() ?: 0
                )
            }
        }

        // Initialize checkbox listeners
        diabetesCheckedChangeListener = CompoundButton.OnCheckedChangeListener { _, isChecked ->
            viewModel.updateMedicalHistory(diabetes = isChecked)
        }

        hypertensionCheckedChangeListener = CompoundButton.OnCheckedChangeListener { _, isChecked ->
            viewModel.updateMedicalHistory(hypertension = isChecked)
        }

        cardiovascularCheckedChangeListener = CompoundButton.OnCheckedChangeListener { _, isChecked ->
            viewModel.updateMedicalHistory(cardiovascularDisease = isChecked)
        }

        headInjuryCheckedChangeListener = CompoundButton.OnCheckedChangeListener { _, isChecked ->
            viewModel.updateMedicalHistory(headInjury = isChecked)
        }

        // Attach listeners
        editTextSystolicBP.addTextChangedListener(systolicBPWatcher)
        editTextDiastolicBP.addTextChangedListener(diastolicBPWatcher)

        checkBoxDiabetes.setOnCheckedChangeListener(diabetesCheckedChangeListener)
        checkBoxHypertension.setOnCheckedChangeListener(hypertensionCheckedChangeListener)
        checkBoxCardiovascularDisease.setOnCheckedChangeListener(cardiovascularCheckedChangeListener)
        checkBoxHeadInjury.setOnCheckedChangeListener(headInjuryCheckedChangeListener)

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
            try {
                // Temporarily detach listeners
                editTextSystolicBP.removeTextChangedListener(systolicBPWatcher)
                editTextDiastolicBP.removeTextChangedListener(diastolicBPWatcher)

                checkBoxDiabetes.setOnCheckedChangeListener(null)
                checkBoxHypertension.setOnCheckedChangeListener(null)
                checkBoxCardiovascularDisease.setOnCheckedChangeListener(null)
                checkBoxHeadInjury.setOnCheckedChangeListener(null)

                // Update UI
                checkBoxDiabetes.isChecked = medicalHistory.diabetes
                checkBoxHypertension.isChecked = medicalHistory.hypertension
                checkBoxCardiovascularDisease.isChecked = medicalHistory.cardiovascularDisease
                checkBoxHeadInjury.isChecked = medicalHistory.headInjury

                editTextSystolicBP.setText(
                    if (medicalHistory.systolicBP > 0) medicalHistory.systolicBP.toString() else ""
                )
                editTextDiastolicBP.setText(
                    if (medicalHistory.diastolicBP > 0) medicalHistory.diastolicBP.toString() else ""
                )

                sliderAlcohol.value = medicalHistory.alcoholConsumption.toFloat()
                textAlcoholValue.text = "${medicalHistory.alcoholConsumption} units"

                spinnerDietQuality.setText(
                    if (medicalHistory.dietQuality.isNotEmpty()) medicalHistory.dietQuality else "",
                    false
                )
                spinnerSleepQuality.setText(
                    if (medicalHistory.sleepQuality.isNotEmpty()) medicalHistory.sleepQuality else "",
                    false
                )
                spinnerSmoking.setText(
                    if (medicalHistory.smoking.isNotEmpty()) medicalHistory.smoking else "",
                    false
                )

                // Reattach listeners
                editTextSystolicBP.addTextChangedListener(systolicBPWatcher)
                editTextDiastolicBP.addTextChangedListener(diastolicBPWatcher)

                checkBoxDiabetes.setOnCheckedChangeListener(diabetesCheckedChangeListener)
                checkBoxHypertension.setOnCheckedChangeListener(hypertensionCheckedChangeListener)
                checkBoxCardiovascularDisease.setOnCheckedChangeListener(cardiovascularCheckedChangeListener)
                checkBoxHeadInjury.setOnCheckedChangeListener(headInjuryCheckedChangeListener)
            } catch (e: Exception) {
                Log.e("MedicalHistoryActivity", "Error updating UI", e)
                Toast.makeText(this, "Error updating UI: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        })

        viewModel.progress.observe(this, Observer { progress ->
            try {
                progressBar.progress = progress
            } catch (e: Exception) {
                Log.e("MedicalHistoryActivity", "Error updating progress", e)
            }
        })

        viewModel.error.observe(this, Observer { error ->
            error?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
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