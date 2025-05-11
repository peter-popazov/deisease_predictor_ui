package com.peter.rgr.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.peter.rgr.data.CognitiveSymptoms
import com.peter.rgr.repository.CognitiveSymptomsRepository

class CognitiveSymptomsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = CognitiveSymptomsRepository(application)
    
    private val _cognitiveSymptoms = MutableLiveData<CognitiveSymptoms>()
    val cognitiveSymptoms: LiveData<CognitiveSymptoms> = _cognitiveSymptoms

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    init {
        loadCognitiveSymptoms()
    }

    private fun loadCognitiveSymptoms() {
        _cognitiveSymptoms.value = repository.getCognitiveSymptoms()
    }

    fun updateCognitiveSymptoms(
        memoryProblems: Boolean? = null,
        languageProblems: Boolean? = null,
        attentionProblems: Boolean? = null,
        executiveFunctionProblems: Boolean? = null,
        visuospatialProblems: Boolean? = null,
        socialCognitionProblems: Boolean? = null,
        difficultyCompletingTasks: Boolean? = null
    ) {
        val current = _cognitiveSymptoms.value ?: CognitiveSymptoms()
        _cognitiveSymptoms.value = current.copy(
            confusion = memoryProblems ?: current.confusion,
            disorientation = languageProblems ?: current.disorientation,
            forgetfulness = attentionProblems ?: current.forgetfulness,
            depression = executiveFunctionProblems ?: current.depression,
            memoryComplaints = visuospatialProblems ?: current.memoryComplaints,
            personalityChanges = socialCognitionProblems ?: current.personalityChanges,
            difficultyCompletingTasks = difficultyCompletingTasks ?: current.difficultyCompletingTasks
        )
    }

    fun validateInputs(): Boolean {
        val current = _cognitiveSymptoms.value ?: return false

        return true
    }

    fun saveCognitiveSymptoms() {
        try {
            _cognitiveSymptoms.value?.let { repository.saveCognitiveSymptoms(it) }
        } catch (e: Exception) {
            _error.value = e.message
        }
    }
}
