package com.peter.rgr.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.peter.rgr.data.MedicalHistory
import com.peter.rgr.repository.MedicalHistoryRepository
import kotlinx.coroutines.launch

class MedicalHistoryViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = MedicalHistoryRepository(
        application
    )
    
    private val _medicalHistory = MutableLiveData<MedicalHistory>()
    val medicalHistory: LiveData<MedicalHistory> = _medicalHistory

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String> = _error as LiveData<String>

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        loadMedicalHistory()
    }

    private fun loadMedicalHistory() {
//        _medicalHistory.value = repository.getMedicalHistory()
    }

    fun updateMedicalHistory(
        diabetes: Boolean? = null,
        hypertension: Boolean? = null,
        cardiovascularDisease: Boolean? = null,
        headInjury: Boolean? = null,
        systolicBP: Int? = null,
        diastolicBP: Int? = null,
        alcoholConsumption: Int? = null,
        confusion: Boolean? = null,
        disorientation: Boolean? = null,
        forgetfulness: Boolean? = null,
        depression: Boolean? = null,
        memoryComplaints: Boolean? = null,
        personalityChanges: Boolean? = null,
        difficultyCompletingTasks: Boolean? = null
    ) {
        val current = _medicalHistory.value ?: MedicalHistory()
        _medicalHistory.value = current.copy(
            diabetes = diabetes ?: current.diabetes,
            hypertension = hypertension ?: current.hypertension,
            cardiovascularDisease = cardiovascularDisease ?: current.cardiovascularDisease,
            headInjury = headInjury ?: current.headInjury,
            systolicBP = systolicBP ?: current.systolicBP,
            diastolicBP = diastolicBP ?: current.diastolicBP,
            alcoholConsumption = alcoholConsumption ?: current.alcoholConsumption,
            confusion = confusion ?: current.confusion,
            disorientation = disorientation ?: current.disorientation,
            forgetfulness = forgetfulness ?: current.forgetfulness,
            depression = depression ?: current.depression,
            memoryComplaints = memoryComplaints ?: current.memoryComplaints,
            personalityChanges = personalityChanges ?: current.personalityChanges,
            difficultyCompletingTasks = difficultyCompletingTasks ?: current.difficultyCompletingTasks
        )
    }

    fun validateInputs(): Boolean {
        val current = _medicalHistory.value ?: return false
        
        if (current.systolicBP <= 0) {
            _error.value = "Please enter systolic blood pressure"
            return false
        }
        if (current.diastolicBP <= 0) {
            _error.value = "Please enter diastolic blood pressure"
            return false
        }
        return true
    }

    fun saveMedicalHistory() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = repository.saveMedicalHistory(_medicalHistory.value ?: return@launch)
                result.onSuccess {
                    _error.value = null
                }.onFailure { e ->
                    _error.value = e.message
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
}