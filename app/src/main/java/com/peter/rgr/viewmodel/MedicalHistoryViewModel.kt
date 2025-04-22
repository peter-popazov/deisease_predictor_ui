package com.peter.rgr.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.peter.rgr.data.MedicalHistory
import com.peter.rgr.repository.MedicalHistoryRepository

class MedicalHistoryViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = MedicalHistoryRepository(application)
    
    private val _medicalHistory = MutableLiveData<MedicalHistory>()
    val medicalHistory: LiveData<MedicalHistory> = _medicalHistory

    private val _progress = MutableLiveData<Int>()
    val progress: LiveData<Int> = _progress

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val totalFields = 8

    init {
        loadMedicalHistory()
    }

    private fun loadMedicalHistory() {
        _medicalHistory.value = repository.getMedicalHistory()
        updateProgress()
    }

    fun updateMedicalHistory(
        diabetes: Boolean? = null,
        hypertension: Boolean? = null,
        cardiovascularDisease: Boolean? = null,
        headInjury: Boolean? = null,
        systolicBP: String? = null,
        diastolicBP: String? = null,
        alcoholConsumption: Int? = null,
        dietQuality: String? = null,
        sleepQuality: String? = null,
        smoking: String? = null
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
            dietQuality = dietQuality ?: current.dietQuality,
            sleepQuality = sleepQuality ?: current.sleepQuality,
            smoking = smoking ?: current.smoking
        )
        updateProgress()
    }

    private fun updateProgress() {
        val current = _medicalHistory.value ?: return
        var filledFields = 0

        if (current.systolicBP.isNotEmpty()) filledFields++
        if (current.diastolicBP.isNotEmpty()) filledFields++
        if (current.dietQuality.isNotEmpty()) filledFields++
        if (current.sleepQuality.isNotEmpty()) filledFields++
        if (current.smoking.isNotEmpty()) filledFields++
        if (current.diabetes) filledFields++
        if (current.hypertension) filledFields++
        if (current.cardiovascularDisease) filledFields++
        if (current.headInjury) filledFields++

        _progress.value = (filledFields.toFloat() / totalFields * 100).toInt()
    }

    fun validateInputs(): Boolean {
        val current = _medicalHistory.value ?: return false
        
        if (current.systolicBP.isEmpty()) {
            _error.value = "Please enter systolic blood pressure"
            return false
        }
        if (current.diastolicBP.isEmpty()) {
            _error.value = "Please enter diastolic blood pressure"
            return false
        }
        if (current.dietQuality.isEmpty()) {
            _error.value = "Please select diet quality"
            return false
        }
        if (current.sleepQuality.isEmpty()) {
            _error.value = "Please select sleep quality"
            return false
        }
        if (current.smoking.isEmpty()) {
            _error.value = "Please select smoking status"
            return false
        }
        return true
    }

    fun saveMedicalHistory() {
        try {
            _medicalHistory.value?.let { repository.saveMedicalHistory(it) }
        } catch (e: Exception) {
            _error.value = e.message
        }
    }
} 