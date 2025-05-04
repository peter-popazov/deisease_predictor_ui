package com.peter.rgr

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.CompoundButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.checkbox.MaterialCheckBox
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cognitive_symptoms)

        initializeViews()
        setupCheckboxListeners()
        setupNavigation()
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
    }

    private fun setupCheckboxListeners() {
        // No need to update progress, just keeping the callback structure
        val checkboxListener = { _: MaterialCheckBox, _: Boolean -> }

        checkboxConfusion.setOnCheckedChangeListener(checkboxListener as ((CompoundButton, Boolean) -> Unit)?)
        checkboxDisorientation.setOnCheckedChangeListener(checkboxListener)
        checkboxForgetfulness.setOnCheckedChangeListener(checkboxListener)
        checkboxDepression.setOnCheckedChangeListener(checkboxListener)
        checkboxMemoryComplaints.setOnCheckedChangeListener(checkboxListener)
        checkboxPersonalityChanges.setOnCheckedChangeListener(checkboxListener)
        checkboxDifficultyCompletingTasks.setOnCheckedChangeListener(checkboxListener)
    }

    private fun setupNavigation() {
        buttonNext.setOnClickListener {
            try {
                if (validateInputs()) {
                    saveCognitiveData()
                    Log.d("CognitiveSymptomsActivity", "Navigating to MemoryTestActivity")
                    val intent = Intent(this, MemoryTestActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }
            } catch (e: Exception) {
                Log.e("CognitiveSymptomsActivity", "Error navigating to MemoryTestActivity", e)
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
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
