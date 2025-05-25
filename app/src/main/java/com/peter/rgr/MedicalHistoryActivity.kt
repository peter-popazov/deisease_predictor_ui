package com.peter.rgr

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
import androidx.lifecycle.Observer
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
        viewModel = ViewModelProvider(this)[MedicalHistoryViewModel::class.java]

        try {
            initializeViews()
            setupSpinners()
            setupFieldListeners()
            observeViewModel()
            setupNavigation()
        } catch (e: Exception) {
            Toast.makeText(this, "Error initializing activity: ${e.message}", Toast.LENGTH_LONG)
                .show()
            finish()
        }
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

        spinnerDietQuality.setAdapter(
            ArrayAdapter(
                this,
                android.R.layout.simple_dropdown_item_1line,
                dietOptions
            )
        )
        spinnerSleepQuality.setAdapter(
            ArrayAdapter(
                this,
                android.R.layout.simple_dropdown_item_1line,
                sleepOptions
            )
        )
        spinnerSmoking.setAdapter(
            ArrayAdapter(
                this,
                android.R.layout.simple_dropdown_item_1line,
                smokingOptions
            )
        )
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

        cardiovascularCheckedChangeListener =
            CompoundButton.OnCheckedChangeListener { _, isChecked ->
                viewModel.updateMedicalHistory(cardiovascularDisease = isChecked)
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

        spinnerDietQuality.setOnItemClickListener { _, _, position, _ ->
            (spinnerDietQuality.adapter?.getItem(position) as? String)?.let { selectedItem ->
                viewModel.updateMedicalHistory(dietQuality = selectedItem)
            }
        }

        spinnerSleepQuality.setOnItemClickListener { _, _, position, _ ->
            (spinnerSleepQuality.adapter?.getItem(position) as? String)?.let { selectedItem ->
                viewModel.updateMedicalHistory(sleepQuality = selectedItem)
            }
        }

        spinnerSmoking.setOnItemClickListener { _, _, position, _ ->
            viewModel.updateMedicalHistory(
                smoking = (spinnerSmoking.adapter?.getItem(position) as? String) ?: ""
            )
        }
    }

    private fun observeViewModel() {
        viewModel.medicalHistory.observe(this, Observer { history ->
            try {
                editTextSystolicBP.removeTextChangedListener(systolicBPWatcher)
                editTextDiastolicBP.removeTextChangedListener(diastolicBPWatcher)

                checkBoxDiabetes.setOnCheckedChangeListener(null)
                checkBoxHypertension.setOnCheckedChangeListener(null)
                checkBoxCardiovascularDisease.setOnCheckedChangeListener(null)
                checkBoxFamilyHistory.setOnCheckedChangeListener(null)
                checkBoxHeadInjury.setOnCheckedChangeListener(null)

                checkBoxDiabetes.isChecked = history.diabetes
                checkBoxHypertension.isChecked = history.hypertension
                checkBoxCardiovascularDisease.isChecked = history.cardiovascularDisease
                checkBoxFamilyHistory.isChecked = history.familyHistoryAlzheimers
                checkBoxHeadInjury.isChecked = history.headInjury

                editTextSystolicBP.setText(if (history.systolicBP > 0) history.systolicBP.toString() else "")
                editTextDiastolicBP.setText(if (history.diastolicBP > 0) history.diastolicBP.toString() else "")

                sliderAlcohol.value = history.alcoholConsumption.toFloat()
                textAlcoholValue.text = "${history.alcoholConsumption} units"

                sliderPhysicalActivity.value = history.physicalActivity.toFloat()
                textPhysicalActivityValue.text = "${history.physicalActivity} hours/week"

                spinnerDietQuality.setText(history.dietQuality)
                spinnerSmoking.setText(history.smoking)
                spinnerSleepQuality.setText(history.sleepQuality)

                editTextSystolicBP.addTextChangedListener(systolicBPWatcher)
                editTextDiastolicBP.addTextChangedListener(diastolicBPWatcher)

                checkBoxDiabetes.setOnCheckedChangeListener(diabetesCheckedChangeListener)
                checkBoxHypertension.setOnCheckedChangeListener(hypertensionCheckedChangeListener)
                checkBoxCardiovascularDisease.setOnCheckedChangeListener(
                    cardiovascularCheckedChangeListener
                )
                checkBoxFamilyHistory.setOnCheckedChangeListener(familyHistoryCheckedChangeListener)
                checkBoxHeadInjury.setOnCheckedChangeListener(headInjuryCheckedChangeListener)

            } catch (e: Exception) {
                Log.e("MedicalHistoryActivity", "UI update failed", e)
                Toast.makeText(this, "UI update error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        })

        viewModel.error.observe(this) {
            it?.let { error ->
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
            }
        }
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
            viewModel.saveMedicalHistory()
            val intent = Intent(this, PatientDetailsActivity::class.java)
            startActivity(intent)
        }
    }
}
