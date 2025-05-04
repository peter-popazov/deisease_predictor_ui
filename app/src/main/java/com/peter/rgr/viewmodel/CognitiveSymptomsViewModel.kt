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
        otherSymptoms: String? = null
    ) {
        val current = _cognitiveSymptoms.value ?: CognitiveSymptoms()
        _cognitiveSymptoms.value = current.copy(
            memoryProblems = memoryProblems ?: current.memoryProblems,
            languageProblems = languageProblems ?: current.languageProblems,
            attentionProblems = attentionProblems ?: current.attentionProblems,
            executiveFunctionProblems = executiveFunctionProblems ?: current.executiveFunctionProblems,
            visuospatialProblems = visuospatialProblems ?: current.visuospatialProblems,
            socialCognitionProblems = socialCognitionProblems ?: current.socialCognitionProblems,
            otherSymptoms = otherSymptoms ?: current.otherSymptoms
        )
    }

    fun validateInputs(): Boolean {
        val current = _cognitiveSymptoms.value ?: return false
        
        if (!current.memoryProblems && 
            !current.languageProblems && 
            !current.attentionProblems && 
            !current.executiveFunctionProblems && 
            !current.visuospatialProblems && 
            !current.socialCognitionProblems && 
            current.otherSymptoms.isEmpty()) {
            _error.value = "Please select at least one symptom or describe other symptoms"
            return false
        }
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
