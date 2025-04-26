package com.peter.rgr

import android.os.Bundle
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.progressindicator.LinearProgressIndicator
import org.json.JSONObject
import java.io.File
import java.io.FileWriter

class CognitiveSymptomsActivity : AppCompatActivity() {
    private lateinit var checkboxConfusion: MaterialCheckBox
    private lateinit var checkboxDisorientation: MaterialCheckBox
    private lateinit var checkboxForgetfulness: MaterialCheckBox
    private lateinit var checkboxDepression: MaterialCheckBox
    private lateinit var checkboxMemoryComplaints: MaterialCheckBox
    private lateinit var checkboxPersonalityChanges: MaterialCheckBox
    private lateinit var checkboxDifficultyCompletingTasks: MaterialCheckBox
    private lateinit var buttonNext: MaterialButton
    private lateinit var buttonPrevious: MaterialButton
    private lateinit var progressBar: LinearProgressIndicator

    private val totalFields = 7 // Total number of checkboxes
    private var checkedFields = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cognitive_symptoms)

        initializeViews()
        setupCheckboxListeners()
        setupNavigation()
        updateProgress()
    }

    private fun initializeViews() {
        checkboxConfusion = findViewById(R.id.checkboxConfusion)
        checkboxDisorientation = findViewById(R.id.checkboxDisorientation)
        checkboxForgetfulness = findViewById(R.id.checkboxForgetfulness)
        checkboxDepression = findViewById(R.id.checkboxDepression)
        checkboxMemoryComplaints = findViewById(R.id.checkboxMemoryComplaints)
        checkboxPersonalityChanges = findViewById(R.id.checkboxPersonalityChanges)
        checkboxDifficultyCompletingTasks = findViewById(R.id.checkboxDifficultyCompletingTasks)
        buttonNext = findViewById(R.id.buttonNext)
        buttonPrevious = findViewById(R.id.buttonPrevious)
        progressBar = findViewById(R.id.progressBar)
    }

    private fun setupCheckboxListeners() {
        val checkboxListener = { _: MaterialCheckBox, _: Boolean ->
            updateProgress()
        }

        checkboxConfusion.setOnCheckedChangeListener(checkboxListener as ((CompoundButton, Boolean) -> Unit)?)
        checkboxDisorientation.setOnCheckedChangeListener(checkboxListener)
        checkboxForgetfulness.setOnCheckedChangeListener(checkboxListener)
        checkboxDepression.setOnCheckedChangeListener(checkboxListener)
        checkboxMemoryComplaints.setOnCheckedChangeListener(checkboxListener)
        checkboxPersonalityChanges.setOnCheckedChangeListener(checkboxListener)
        checkboxDifficultyCompletingTasks.setOnCheckedChangeListener(checkboxListener)
    }

    private fun updateProgress() {
        checkedFields = 0
        if (checkboxConfusion.isChecked) checkedFields++
        if (checkboxDisorientation.isChecked) checkedFields++
        if (checkboxForgetfulness.isChecked) checkedFields++
        if (checkboxDepression.isChecked) checkedFields++
        if (checkboxMemoryComplaints.isChecked) checkedFields++
        if (checkboxPersonalityChanges.isChecked) checkedFields++
        if (checkboxDifficultyCompletingTasks.isChecked) checkedFields++

        val progress = (checkedFields.toFloat() / totalFields.toFloat() * 100).toInt()
        progressBar.progress = progress
    }

    private fun setupNavigation() {
        buttonNext.setOnClickListener {
            if (validateInputs()) {
                saveCognitiveData()
//                val intent = Intent(this, AdditionalInfoActivity::class.java)
//                startActivity(intent)
            }
        }

        buttonPrevious.setOnClickListener {
            finish() // Go back to previous activity
        }
    }

    private fun validateInputs(): Boolean {
        // Since these are optional symptoms, we don't need to validate that any are checked
        return true
    }

    private fun saveCognitiveData() {
        val cognitiveData = JSONObject().apply {
            put("confusion", checkboxConfusion.isChecked)
            put("disorientation", checkboxDisorientation.isChecked)
            put("forgetfulness", checkboxForgetfulness.isChecked)
            put("depression", checkboxDepression.isChecked)
            put("memoryComplaints", checkboxMemoryComplaints.isChecked)
            put("personalityChanges", checkboxPersonalityChanges.isChecked)
            put("difficultyCompletingTasks", checkboxDifficultyCompletingTasks.isChecked)
        }

        // Save to file
        val file = File(filesDir, "cognitive_symptoms.json")
        FileWriter(file).use { writer ->
            writer.write(cognitiveData.toString())
        }

        // Save to SharedPreferences for easy access
        val sharedPreferences = getSharedPreferences("PatientData", MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putBoolean("confusion", checkboxConfusion.isChecked)
            putBoolean("disorientation", checkboxDisorientation.isChecked)
            putBoolean("forgetfulness", checkboxForgetfulness.isChecked)
            putBoolean("depression", checkboxDepression.isChecked)
            putBoolean("memoryComplaints", checkboxMemoryComplaints.isChecked)
            putBoolean("personalityChanges", checkboxPersonalityChanges.isChecked)
            putBoolean("difficultyCompletingTasks", checkboxDifficultyCompletingTasks.isChecked)
            apply()
        }
    }
}