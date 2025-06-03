package com.peter.rgr

import MedicalHistory
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.CompoundButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.button.MaterialButton
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.slider.Slider
import com.google.android.material.textfield.TextInputEditText
import com.peter.rgr.viewmodel.MedicalHistoryViewModel

class MedicalHistoryActivity : AppCompatActivity() {
    private lateinit var viewModel: MedicalHistoryViewModel

    private lateinit var checkBoxDiabetes: MaterialCheckBox
    private lateinit var checkBoxCardiovascularDisease: MaterialCheckBox
    private lateinit var checkBoxFamilyHistory: MaterialCheckBox
    private lateinit var checkBoxHypertension: MaterialCheckBox
    private lateinit var checkBoxHeadInjury: MaterialCheckBox
    private lateinit var editTextSystolicBP: TextInputEditText
    private lateinit var editTextDiastolicBP: TextInputEditText
    private lateinit var sliderAlcohol: Slider
    private lateinit var sliderPhysicalActivity: Slider
    private lateinit var textPhysicalActivityValue: TextView
    private lateinit var textAlcoholValue: TextView
    private lateinit var spinnerDietQuality: AutoCompleteTextView
    private lateinit var spinnerSleepQuality: AutoCompleteTextView
    private lateinit var spinnerSmoking: AutoCompleteTextView
    private lateinit var buttonNext: MaterialButton
    private lateinit var buttonPrevious: MaterialButton

    private var systolicBPWatcher: TextWatcher? = null
    private var diastolicBPWatcher: TextWatcher? = null

    private var diabetesCheckedChangeListener: CompoundButton.OnCheckedChangeListener? = null
    private var hypertensionCheckedChangeListener: CompoundButton.OnCheckedChangeListener? = null
    private var familyHistoryCheckedChangeListener: CompoundButton.OnCheckedChangeListener? = null
    private var cardiovascularCheckedChangeListener: CompoundButton.OnCheckedChangeListener? = null
    private var headInjuryCheckedChangeListener: CompoundButton.OnCheckedChangeListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_medical_history)
        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        ).get(MedicalHistoryViewModel::class.java)

        initializeViews()
        setupSpinners()
        observeViewModel() // <-- Observe before attaching listeners
        setupFieldListeners()
        setupNavigation()
    }

    private fun initializeViews() {
        checkBoxDiabetes = findViewById(R.id.checkBoxDiabetes)
        checkBoxHypertension = findViewById(R.id.checkBoxHypertension)
        checkBoxCardiovascularDisease = findViewById(R.id.checkBoxCardiovascularDisease)
        checkBoxFamilyHistory = findViewById(R.id.checkBoxFamilyHistoryAlzheimers)
        checkBoxHeadInjury = findViewById(R.id.checkBoxHeadInjury)
        editTextSystolicBP = findViewById(R.id.editTextSystolicBP)
        editTextDiastolicBP = findViewById(R.id.editTextDiastolicBP)
        sliderAlcohol = findViewById(R.id.sliderAlcohol)
        sliderPhysicalActivity = findViewById(R.id.sliderPhysicalActivity)
        textAlcoholValue = findViewById(R.id.textAlcoholValue)
        textPhysicalActivityValue = findViewById(R.id.textPhysicalActivityValue)
        spinnerDietQuality = findViewById(R.id.spinnerDietQuality)
        spinnerSleepQuality = findViewById(R.id.spinnerSleepQuality)
        spinnerSmoking = findViewById(R.id.spinnerSmoking)
        buttonNext = findViewById(R.id.buttonNext)
        buttonPrevious = findViewById(R.id.buttonPrevious)
    }

    private fun setupSpinners() {
        val dietOptions = listOf("Poor", "Average", "Good", "Excellent")
        val sleepOptions = listOf("Poor", "Fair", "Good", "Excellent")
        val smokingOptions = listOf("Never", "Former", "Current")

        spinnerDietQuality.setAdapter(ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, dietOptions))
        spinnerSleepQuality.setAdapter(ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, sleepOptions))
        spinnerSmoking.setAdapter(ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, smokingOptions))
    }

    private fun setupFieldListeners() {
        systolicBPWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val systolic = s.toString().toIntOrNull() ?: 0
                val diastolic = editTextDiastolicBP.text?.toString()?.toIntOrNull() ?: 0
                viewModel.updateMedicalHistory(systolicBP = systolic, diastolicBP = diastolic)
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }

        diastolicBPWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val systolic = editTextSystolicBP.text?.toString()?.toIntOrNull() ?: 0
                val diastolic = s.toString().toIntOrNull() ?: 0
                viewModel.updateMedicalHistory(systolicBP = systolic, diastolicBP = diastolic)
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }

        diabetesCheckedChangeListener = CompoundButton.OnCheckedChangeListener { _, isChecked ->
            viewModel.updateMedicalHistory(diabetes = isChecked)
        }

        hypertensionCheckedChangeListener = CompoundButton.OnCheckedChangeListener { _, isChecked ->
            viewModel.updateMedicalHistory(hypertension = isChecked)
        }

        cardiovascularCheckedChangeListener = CompoundButton.OnCheckedChangeListener { _, isChecked ->
            viewModel.updateMedicalHistory(cardiovascularDisease = isChecked)
        }

        familyHistoryCheckedChangeListener = CompoundButton.OnCheckedChangeListener { _, isChecked ->
            viewModel.updateMedicalHistory(familyHistoryAlzheimers = isChecked)
        }

        headInjuryCheckedChangeListener = CompoundButton.OnCheckedChangeListener { _, isChecked ->
            viewModel.updateMedicalHistory(headInjury = isChecked)
        }

        sliderAlcohol.addOnChangeListener { _, value, _ ->
            val alcoholValue = value.toInt()
            textAlcoholValue.text = "$alcoholValue units"
            viewModel.updateMedicalHistory(alcoholConsumption = alcoholValue)
        }

        sliderPhysicalActivity.addOnChangeListener { _, value, _ ->
            val physicalActivity = value.toInt()
            textPhysicalActivityValue.text = "$physicalActivity hours/week"
            viewModel.updateMedicalHistory(physicalActivity = physicalActivity)
        }

        editTextSystolicBP.addTextChangedListener(systolicBPWatcher)
        editTextDiastolicBP.addTextChangedListener(diastolicBPWatcher)

        checkBoxDiabetes.setOnCheckedChangeListener(diabetesCheckedChangeListener)
        checkBoxHypertension.setOnCheckedChangeListener(hypertensionCheckedChangeListener)
        checkBoxCardiovascularDisease.setOnCheckedChangeListener(cardiovascularCheckedChangeListener)
        checkBoxFamilyHistory.setOnCheckedChangeListener(familyHistoryCheckedChangeListener)
        checkBoxHeadInjury.setOnCheckedChangeListener(headInjuryCheckedChangeListener)

        setupSpinner(spinnerDietQuality) { viewModel.updateMedicalHistory(dietQuality = it) }
        setupSpinner(spinnerSleepQuality) { viewModel.updateMedicalHistory(sleepQuality = it) }
        setupSpinner(spinnerSmoking) { viewModel.updateMedicalHistory(smoking = it) }
    }

    private fun setupSpinner(spinner: AutoCompleteTextView, update: (String) -> Unit) {
        spinner.setOnItemClickListener { _, _, position, _ ->
            val item = spinner.adapter.getItem(position) as? String
            item?.let { update(it) }
        }
    }

    private fun observeViewModel() {
        viewModel.medicalHistory.observe(this) { history ->
            updateUI(history)
        }

        viewModel.error.observe(this) {
            it?.let { error ->
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateUI(history: MedicalHistory) {
        try {
            // Remove listeners to avoid triggering them during UI update
            editTextSystolicBP.removeTextChangedListener(systolicBPWatcher)
            editTextDiastolicBP.removeTextChangedListener(diastolicBPWatcher)
            checkBoxDiabetes.setOnCheckedChangeListener(null)
            checkBoxHypertension.setOnCheckedChangeListener(null)
            checkBoxCardiovascularDisease.setOnCheckedChangeListener(null)
            checkBoxFamilyHistory.setOnCheckedChangeListener(null)
            checkBoxHeadInjury.setOnCheckedChangeListener(null)

            // Only update UI if value has changed to avoid unnecessary resets
            if (checkBoxDiabetes.isChecked != history.diabetes)
                checkBoxDiabetes.isChecked = history.diabetes
            if (checkBoxHypertension.isChecked != history.hypertension)
                checkBoxHypertension.isChecked = history.hypertension
            if (checkBoxCardiovascularDisease.isChecked != history.cardiovascularDisease)
                checkBoxCardiovascularDisease.isChecked = history.cardiovascularDisease
            if (checkBoxFamilyHistory.isChecked != history.familyHistoryAlzheimers)
                checkBoxFamilyHistory.isChecked = history.familyHistoryAlzheimers
            if (checkBoxHeadInjury.isChecked != history.headInjury)
                checkBoxHeadInjury.isChecked = history.headInjury

            val systolicStr = if (history.systolicBP > 0) history.systolicBP.toString() else ""
            if (editTextSystolicBP.text?.toString() != systolicStr)
                editTextSystolicBP.setText(systolicStr)
            val diastolicStr = if (history.diastolicBP > 0) history.diastolicBP.toString() else ""
            if (editTextDiastolicBP.text?.toString() != diastolicStr)
                editTextDiastolicBP.setText(diastolicStr)

            if (sliderAlcohol.value != history.alcoholConsumption.toFloat())
                sliderAlcohol.value = history.alcoholConsumption.toFloat()
            textAlcoholValue.text = "${history.alcoholConsumption} units"

            if (sliderPhysicalActivity.value != history.physicalActivity.toFloat())
                sliderPhysicalActivity.value = history.physicalActivity.toFloat()
            textPhysicalActivityValue.text = "${history.physicalActivity} hours/week"

            if (spinnerDietQuality.text.toString() != history.dietQuality)
                spinnerDietQuality.setText(history.dietQuality, false)
            if (spinnerSmoking.text.toString() != history.smoking)
                spinnerSmoking.setText(history.smoking, false)
            if (spinnerSleepQuality.text.toString() != history.sleepQuality)
                spinnerSleepQuality.setText(history.sleepQuality, false)

            // Re-attach listeners
            editTextSystolicBP.addTextChangedListener(systolicBPWatcher)
            editTextDiastolicBP.addTextChangedListener(diastolicBPWatcher)
            checkBoxDiabetes.setOnCheckedChangeListener(diabetesCheckedChangeListener)
            checkBoxHypertension.setOnCheckedChangeListener(hypertensionCheckedChangeListener)
            checkBoxCardiovascularDisease.setOnCheckedChangeListener(cardiovascularCheckedChangeListener)
            checkBoxFamilyHistory.setOnCheckedChangeListener(familyHistoryCheckedChangeListener)
            checkBoxHeadInjury.setOnCheckedChangeListener(headInjuryCheckedChangeListener)

        } catch (e: Exception) {
            Log.e("MedicalHistoryActivity", "UI update failed", e)
            Toast.makeText(this, "UI update error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupNavigation() {
        buttonNext.setOnClickListener {
            if (viewModel.validateInputs()) {
                viewModel.saveMedicalHistory()
                startActivity(Intent(this, CognitiveSymptomsActivity::class.java))
            }
        }

        buttonPrevious.setOnClickListener {
            viewModel.saveMedicalHistory()
            startActivity(Intent(this, PatientDetailsActivity::class.java))
        }
    }
}
