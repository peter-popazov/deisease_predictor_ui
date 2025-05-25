package com.peter.rgr

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.CompoundButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.button.MaterialButton
import com.google.android.material.checkbox.MaterialCheckBox
import com.peter.rgr.viewmodel.CognitiveSymptomsViewModel
import com.peter.rgr.viewmodel.MedicalHistoryViewModel
import org.json.JSONObject
import java.io.File
import java.io.FileWriter

class CognitiveSymptomsActivity : AppCompatActivity() {
    private lateinit var viewModel: CognitiveSymptomsViewModel

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
        viewModel = ViewModelProvider(this)[CognitiveSymptomsViewModel::class.java]

        initializeViews()
        setupCheckboxListeners()
        observeViewModel()
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
        val checkboxListener = CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            when (buttonView.id) {
                R.id.checkboxConfusion -> viewModel.updateCognitiveSymptoms(memoryProblems = isChecked)
                R.id.checkboxDisorientation -> viewModel.updateCognitiveSymptoms(languageProblems = isChecked)
                R.id.checkboxForgetfulness -> viewModel.updateCognitiveSymptoms(attentionProblems = isChecked)
                R.id.checkboxDepression -> viewModel.updateCognitiveSymptoms(
                    executiveFunctionProblems = isChecked
                )

                R.id.checkboxMemoryComplaints -> viewModel.updateCognitiveSymptoms(
                    visuospatialProblems = isChecked
                )

                R.id.checkboxPersonalityChanges -> viewModel.updateCognitiveSymptoms(
                    socialCognitionProblems = isChecked
                )

                R.id.checkboxDifficultyCompletingTasks -> viewModel.updateCognitiveSymptoms(
                    difficultyCompletingTasks = isChecked
                )
            }
        }

        checkboxConfusion.setOnCheckedChangeListener(checkboxListener)
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
                if (viewModel.validateInputs()) {
                    viewModel.saveCognitiveSymptoms()
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

    private fun observeViewModel() {
        viewModel.cognitiveSymptoms.observe(this, Observer { symptoms ->
            try {
                checkboxConfusion.isChecked = symptoms.confusion
                checkboxDisorientation.isChecked = symptoms.disorientation
                checkboxForgetfulness.isChecked = symptoms.forgetfulness
                checkboxDepression.isChecked = symptoms.depression
                checkboxMemoryComplaints.isChecked = symptoms.memoryComplaints
                checkboxPersonalityChanges.isChecked = symptoms.personalityChanges
                checkboxDifficultyCompletingTasks.isChecked = symptoms.difficultyCompletingTasks
            } catch (e: Exception) {
                Log.e("CognitiveSymptomsActivity", "UI update failed", e)
                Toast.makeText(this, "UI update error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        })

        viewModel.error.observe(this) {
            it?.let { error ->
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
            }
        }
    }
}
