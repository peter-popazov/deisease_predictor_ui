package com.peter.rgr.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.peter.rgr.data.CognitiveSymptoms
import com.peter.rgr.data.MedicalHistory
import com.peter.rgr.data.PatientDetails
import com.peter.rgr.repository.CognitiveSymptomsRepository
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
    private val cognitiveSymptomsRepository = CognitiveSymptomsRepository(application)

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
        val congnitiveSymptoms = cognitiveSymptomsRepository.getCognitiveSymptoms()

        // Get memory test score from SharedPreferences
        val sharedPreferences = getApplication<Application>().getSharedPreferences(
            "PatientData",
            Application.MODE_PRIVATE
        )
        val memoryTestScore = sharedPreferences.getInt("memoryTestScore", -1)

        // Send data to API for prediction
        sendRiskPredictionRequest(patientDetails, medicalHistory, memoryTestScore, congnitiveSymptoms)
    }

    private fun sendRiskPredictionRequest(
        patientDetails: PatientDetails,
        medicalHistory: MedicalHistory,
        memoryTestScore: Int,
        cognitiveSymptoms: CognitiveSymptoms
    ) {
        _isLoading.value = true
        _error.value = null

        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Prepare request body
                val requestBody = createRequestBody(patientDetails, medicalHistory, memoryTestScore, cognitiveSymptoms)

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
                    val errorMessage =
                        connection.errorStream?.bufferedReader()?.use { it.readText() }
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

    private fun createRequestBody(
        patientDetails: PatientDetails,
        medicalHistory: MedicalHistory,
        memoryTestScore: Int,
        cognitiveSymptoms: CognitiveSymptoms
    ): JSONObject {
        val requestJson = JSONObject()

        requestJson.put("age", patientDetails.age)
        requestJson.put("gender", patientDetails.gender)
        requestJson.put("height", patientDetails.height)
        requestJson.put("weight", patientDetails.weight)
        requestJson.put(
            "bmi",
            patientDetails.weight / ((patientDetails.height / 100) * (patientDetails.height / 100))
        )
        requestJson.put("education", patientDetails.educationLevel)
        requestJson.put("ethnicity", patientDetails.ethnicity)

        requestJson.put("diabetes", medicalHistory.diabetes)
        requestJson.put("hypertension", medicalHistory.hypertension)
        requestJson.put("cardiovascular_disease", medicalHistory.cardiovascularDisease)
        requestJson.put("head_injury", medicalHistory.headInjury)
        requestJson.put("systolic_bp", medicalHistory.systolicBP)
        requestJson.put("diastolic_bp", medicalHistory.diastolicBP)
        requestJson.put("alcohol_consumption", medicalHistory.alcoholConsumption)

        requestJson.put("confusion", cognitiveSymptoms.confusion)
        requestJson.put("disorientation", cognitiveSymptoms.disorientation)
        requestJson.put("forgetfulness", cognitiveSymptoms.forgetfulness)
        requestJson.put("depression", cognitiveSymptoms.depression)
        requestJson.put("memory_complaints", cognitiveSymptoms.memoryComplaints)
        requestJson.put("personality_changes", cognitiveSymptoms.personalityChanges)
        requestJson.put("difficulty_completing_tasks", cognitiveSymptoms.difficultyCompletingTasks)

        // Add memory test score
        requestJson.put("memory_test_score", memoryTestScore)

        return requestJson
    }
}
