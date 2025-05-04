package com.peter.rgr.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.peter.rgr.data.MedicalHistory
import com.peter.rgr.data.PatientDetails
import com.peter.rgr.repository.MedicalHistoryRepository
import com.peter.rgr.repository.PatientDetailsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class ResultsViewModel(application: Application) : AndroidViewModel(application) {
    
    private val patientRepository = PatientDetailsRepository(application)
    private val medicalHistoryRepository = MedicalHistoryRepository(application)
    
    private val _predictionResult = MutableLiveData<Float>()
    val predictionResult: LiveData<Float> = _predictionResult
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error
    
    fun calculatePrediction() {
        // Get patient details and medical history
        val patientDetails = patientRepository.getPatientDetails()
        val medicalHistory = medicalHistoryRepository.getMedicalHistory()
        
        // Get memory test score from SharedPreferences
        val sharedPreferences = getApplication<Application>().getSharedPreferences("PatientData", Application.MODE_PRIVATE)
        val memoryTestScore = sharedPreferences.getInt("memoryTestScore", -1)
        
        // Send data to API for prediction
        sendRiskPredictionRequest(patientDetails, medicalHistory, memoryTestScore)
    }
    
    private fun sendRiskPredictionRequest(patientDetails: PatientDetails, medicalHistory: MedicalHistory, memoryTestScore: Int) {
        _isLoading.value = true
        _error.value = null
        
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Prepare request body
                val requestBody = createRequestBody(patientDetails, medicalHistory, memoryTestScore)
                
                // Endpoint URL - replace with your actual API endpoint
                val apiUrl = "https://your-api-endpoint.com/predict-alzheimers-risk"
                
                // Create connection and set properties
                val url = URL(apiUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json")
                connection.doOutput = true
                
                // Send request body
                connection.outputStream.use { os ->
                    os.write(requestBody.toString().toByteArray())
                    os.flush()
                }
                
                // Read response
                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // Parse response
                    val response = connection.inputStream.bufferedReader().use { it.readText() }
                    val jsonResponse = JSONObject(response)
                    val riskPercentage = jsonResponse.getDouble("risk_percentage").toFloat()
                    
                    // Update UI on main thread
                    viewModelScope.launch(Dispatchers.Main) {
                        _predictionResult.value = riskPercentage
                        _isLoading.value = false
                    }
                } else {
                    // Handle error
                    val errorMessage = connection.errorStream?.bufferedReader()?.use { it.readText() } 
                                      ?: "Unknown error occurred: HTTP $responseCode"
                    throw Exception(errorMessage)
                }
                
                connection.disconnect()
                
            } catch (e: Exception) {
                Log.e("ResultsViewModel", "API request failed", e)
                viewModelScope.launch(Dispatchers.Main) {
                    _error.value = "Failed to get prediction: ${e.message}"
                    _isLoading.value = false
                    
                    // For demo purposes, provide a fallback result
                    _predictionResult.value = 45f
                }
            }
        }
    }
    
    private fun createRequestBody(patientDetails: PatientDetails, medicalHistory: MedicalHistory, memoryTestScore: Int): JSONObject {
        val requestJson = JSONObject()
        
        // Add patient details
        val patientJson = JSONObject()
        patientJson.put("age", patientDetails.age)
        patientJson.put("gender", patientDetails.gender)
        patientJson.put("height", patientDetails.height)
        patientJson.put("weight", patientDetails.weight)
        patientJson.put("bmi", patientDetails.weight / ((patientDetails.height / 100) * (patientDetails.height / 100)))
        patientJson.put("education", patientDetails.educationLevel)
        patientJson.put("ethnicity", patientDetails.ethnicity)
        requestJson.put("patient_details", patientJson)
        
        // Add medical history
        val medicalJson = JSONObject()
        medicalJson.put("diabetes", medicalHistory.diabetes)
        medicalJson.put("hypertension", medicalHistory.hypertension)
        medicalJson.put("cardiovascular_disease", medicalHistory.cardiovascularDisease)
        medicalJson.put("head_injury", medicalHistory.headInjury)
        medicalJson.put("systolic_bp", medicalHistory.systolicBP)
        medicalJson.put("diastolic_bp", medicalHistory.diastolicBP)
        medicalJson.put("alcohol_consumption", medicalHistory.alcoholConsumption)
        requestJson.put("medical_history", medicalJson)
        
        // Add cognitive symptoms
        val cognitiveJson = JSONObject()
        cognitiveJson.put("confusion", medicalHistory.confusion)
        cognitiveJson.put("disorientation", medicalHistory.disorientation)
        cognitiveJson.put("forgetfulness", medicalHistory.forgetfulness)
        cognitiveJson.put("depression", medicalHistory.depression)
        cognitiveJson.put("memory_complaints", medicalHistory.memoryComplaints)
        cognitiveJson.put("personality_changes", medicalHistory.personalityChanges)
        cognitiveJson.put("difficulty_completing_tasks", medicalHistory.difficultyCompletingTasks)
        requestJson.put("cognitive_symptoms", cognitiveJson)
        
        // Add memory test score
        requestJson.put("memory_test_score", memoryTestScore)
        
        return requestJson
    }
}
