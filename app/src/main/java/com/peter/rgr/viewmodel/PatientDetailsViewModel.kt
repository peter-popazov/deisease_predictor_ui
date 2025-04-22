package com.peter.rgr.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.peter.rgr.data.PatientDetails
import com.peter.rgr.repository.PatientDetailsRepository
import kotlin.Float
import kotlin.math.pow
import kotlin.math.round

class PatientDetailsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = PatientDetailsRepository(application)
    
    private val _patientDetails = MutableLiveData<PatientDetails>()
    val patientDetails: LiveData<PatientDetails> = _patientDetails

    private val _bmi = MutableLiveData<Double>()
    val bmi: LiveData<Double> = _bmi

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    init {
        loadPatientDetails()
    }

    private fun loadPatientDetails() {
        _patientDetails.value = repository.getPatientDetails()
        calculateBMI()
    }

    fun updatePatientDetails(
        age: Int? = null,
        gender: String? = null,
        height: Float? = null,
        weight: Float? = null,
        educationLevel: String? = null,
        ethnicity: String? = null
    ) {
        val current = _patientDetails.value ?: PatientDetails()
        _patientDetails.value = current.copy(
            age = age ?: current.age,
            gender = gender ?: current.gender,
            height = height ?: current.height,
            weight = weight ?: current.weight,
            educationLevel = educationLevel ?: current.educationLevel,
            ethnicity = ethnicity ?: current.ethnicity
        )
        calculateBMI()
    }

    private fun calculateBMI() {
        val current = _patientDetails.value ?: return
        if (current.height > 0) {
            val heightInMeters = current.height / 100
            val bmi = current.weight / (heightInMeters.pow(2))
            _bmi.value = (round(bmi * 10) / 10).toDouble()
        } else {
            _bmi.value = 0.0
        }
    }

    fun validateInputs(): Boolean {
        val current = _patientDetails.value ?: return false
        
        if (current.age <= 0) {
            _error.value = "Please enter age"
            return false
        }
        if (current.gender.isEmpty()) {
            _error.value = "Please select gender"
            return false
        }
        if (current.height <= 0) {
            _error.value = "Please enter height"
            return false
        }
        if (current.weight <= 0) {
            _error.value = "Please enter weight"
            return false
        }
        if (current.educationLevel.isEmpty()) {
            _error.value = "Please select education level"
            return false
        }
        if (current.ethnicity.isEmpty()) {
            _error.value = "Please select ethnicity"
            return false
        }
        return true
    }

    fun savePatientDetails() {
        try {
            _patientDetails.value?.let { repository.savePatientDetails(it) }
        } catch (e: Exception) {
            _error.value = e.message
        }
    }
} 