package com.peter.rgr

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.android.material.slider.Slider
import com.google.android.material.textfield.TextInputEditText
import org.json.JSONObject
import java.io.File
import java.io.FileWriter

class MedicalHistoryActivity : AppCompatActivity() {
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

    private val totalFields = 8
    private var filledFields = 0

    // Quality level mappings
    private val dietQualityMap = mapOf(
        "Very Poor" to 0,
        "Poor" to 2,
        "Fair" to 4,
        "Good" to 6,
        "Very Good" to 8,
        "Excellent" to 10
    )

    private val sleepQualityMap = mapOf(
        "Very Poor" to 4,
        "Poor" to 5,
        "Fair" to 6,
        "Good" to 7,
        "Very Good" to 8,
        "Excellent" to 9,
        "Perfect" to 10
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_medical_history)

        try {
            initializeViews()
            setupSpinners()
            setupNavigation()
            setupFieldListeners()
            updateProgress()
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
                updateProgress()
            }
        }

        editTextSystolicBP.addTextChangedListener(textWatcher)
        editTextDiastolicBP.addTextChangedListener(textWatcher)

        // Add listeners for checkboxes
        checkBoxDiabetes.setOnCheckedChangeListener { _, _ -> updateProgress() }
        checkBoxHypertension.setOnCheckedChangeListener { _, _ -> updateProgress() }
        checkBoxCardiovascularDisease.setOnCheckedChangeListener { _, _ -> updateProgress() }
        checkBoxHeadInjury.setOnCheckedChangeListener { _, _ -> updateProgress() }

        // Add listener for alcohol slider
        sliderAlcohol.addOnChangeListener { _, value, fromUser ->
            if (fromUser) {
                textAlcoholValue.text = "${value.toInt()} units"
                updateProgress()
            }
        }

        // Add listeners for spinners
        spinnerDietQuality.setOnItemClickListener { _, _, _, _ -> updateProgress() }
        spinnerSleepQuality.setOnItemClickListener { _, _, _, _ -> updateProgress() }
        spinnerSmoking.setOnItemClickListener { _, _, _, _ -> updateProgress() }
    }

    private fun updateProgress() {
        filledFields = listOf(
            editTextSystolicBP.text,
            editTextDiastolicBP.text,
            spinnerDietQuality.text,
            spinnerSleepQuality.text,
            spinnerSmoking.text
        ).count { !it.isNullOrEmpty() }

        // Add 1 for each checked medical condition
        if (checkBoxDiabetes.isChecked) filledFields++
        if (checkBoxHypertension.isChecked) filledFields++
        if (checkBoxCardiovascularDisease.isChecked) filledFields++
        if (checkBoxHeadInjury.isChecked) filledFields++

        val progress = (filledFields.toFloat() / totalFields * 100).toInt()
        progressBar.progress = progress
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

    private fun validateInputs(): Boolean {
        if (editTextSystolicBP.text.isNullOrEmpty()) {
            showToast("Please enter systolic blood pressure")
            return false
        }
        if (editTextDiastolicBP.text.isNullOrEmpty()) {
            showToast("Please enter diastolic blood pressure")
            return false
        }
        if (spinnerDietQuality.text.isEmpty()) {
            showToast("Please select diet quality")
            return false
        }
        if (spinnerSleepQuality.text.isEmpty()) {
            showToast("Please select sleep quality")
            return false
        }
        if (spinnerSmoking.text.isEmpty()) {
            showToast("Please select smoking status")
            return false
        }
        return true
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun saveMedicalData() {
        try {
            val medicalData = JSONObject().apply {
                put("diabetes", checkBoxDiabetes.isChecked)
                put("hypertension", checkBoxHypertension.isChecked)
                put("cardiovascularDisease", checkBoxCardiovascularDisease.isChecked)
                put("headInjury", checkBoxHeadInjury.isChecked)
                put("systolicBP", editTextSystolicBP.text.toString())
                put("diastolicBP", editTextDiastolicBP.text.toString())
                put("alcoholConsumption", sliderAlcohol.value.toInt())
                put("dietQuality", dietQualityMap[spinnerDietQuality.text.toString()] ?: 0)
                put("sleepQuality", sleepQualityMap[spinnerSleepQuality.text.toString()] ?: 4)
                put("smoking", spinnerSmoking.text.toString())
            }

            // Save to file
            val file = File(filesDir, "medical_history.json")
            FileWriter(file).use { writer ->
                writer.write(medicalData.toString())
            }

            // Save to SharedPreferences for easy access
            val sharedPreferences = getSharedPreferences("AlzheimerAssessment", MODE_PRIVATE)
            with(sharedPreferences.edit()) {
                putBoolean("diabetes", checkBoxDiabetes.isChecked)
                putBoolean("hypertension", checkBoxHypertension.isChecked)
                putBoolean("cardiovascularDisease", checkBoxCardiovascularDisease.isChecked)
                putBoolean("headInjury", checkBoxHeadInjury.isChecked)
                putString("systolicBP", editTextSystolicBP.text.toString())
                putString("diastolicBP", editTextDiastolicBP.text.toString())
                putInt("alcoholConsumption", sliderAlcohol.value.toInt())
                putInt("dietQuality", dietQualityMap[spinnerDietQuality.text.toString()] ?: 0)
                putInt("sleepQuality", sleepQualityMap[spinnerSleepQuality.text.toString()] ?: 4)
                putString("smoking", spinnerSmoking.text.toString())
                apply()
            }
        } catch (e: Exception) {
            showToast("Error saving medical data: ${e.message}")
        }
    }

    private fun setupNavigation() {
        buttonNext.setOnClickListener {
            if (validateInputs()) {
                saveMedicalData()
                val intent = Intent(this, CognitiveSymptomsActivity::class.java)
                startActivity(intent)
            }
        }

        buttonPrevious.setOnClickListener {
            finish() // Go back to previous activity
        }
    }
}