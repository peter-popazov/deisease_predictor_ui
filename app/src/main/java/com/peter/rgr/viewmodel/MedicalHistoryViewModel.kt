package com.peter.rgr.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.peter.rgr.api.MedicalHistoryAPI
import com.peter.rgr.api.RetrofitClient
import com.peter.rgr.data.MedicalHistory
import com.peter.rgr.repository.MedicalHistoryRepository
import kotlinx.coroutines.launch

class MedicalHistoryViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = MedicalHistoryRepository(
        application,
        RetrofitClient.create(MedicalHistoryAPI::class.java)
    )
    
    private val _medicalHistory = MutableLiveData<MedicalHistory>()
    val medicalHistory: LiveData<MedicalHistory> = _medicalHistory

    private val _progress = MutableLiveData<Int>()
    val progress: LiveData<Int> = _progress

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val totalFields = 12 // Updated to include all fields

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
        systolicBP: Int? = null,
        diastolicBP: Int? = null,
        alcoholConsumption: Boolean? = null,
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
        updateProgress()
    }

    private fun updateProgress() {
        val current = _medicalHistory.value ?: return
        var filledFields = 0

        if (current.systolicBP > 0) filledFields++
        if (current.diastolicBP > 0) filledFields++
        if (current.diabetes) filledFields++
        if (current.hypertension) filledFields++
        if (current.cardiovascularDisease) filledFields++
        if (current.headInjury) filledFields++
        if (current.alcoholConsumption) filledFields++
        if (current.confusion) filledFields++
        if (current.disorientation) filledFields++
        if (current.forgetfulness) filledFields++
        if (current.depression) filledFields++
        if (current.memoryComplaints) filledFields++
        if (current.personalityChanges) filledFields++
        if (current.difficultyCompletingTasks) filledFields++

        _progress.value = (filledFields.toFloat() / totalFields * 100).toInt()
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