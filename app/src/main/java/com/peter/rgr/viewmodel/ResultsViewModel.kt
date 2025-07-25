package com.peter.rgr.viewmodel

import MedicalHistory
import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.peter.rgr.data.CognitiveSymptoms
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

    private val _recommendations = MutableLiveData<List<String>>()
    val recommendations: LiveData<List<String>> = _recommendations

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
        sendRiskPredictionRequest(
            patientDetails,
            medicalHistory,
            memoryTestScore,
            congnitiveSymptoms
        )
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
                val requestBody = createRequestBody(
                    patientDetails,
                    medicalHistory,
                    memoryTestScore,
                    cognitiveSymptoms
                )
                Log.d("ResultsViewModel", "Request Body: $requestBody")

                // Endpoint URL
                // Make sure this IP is reachable from your Android device/emulator.
                // If running on an emulator, use 10.0.2.2 for localhost.
                val apiUrl = "http://192.168.2.207:5000/predict" // <-- changed from 192.168.x.x

                // Create connection
                val url = URL(apiUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.apply {
                    requestMethod = "POST"
                    setRequestProperty("Content-Type", "application/json")
                    doOutput = true
                    connectTimeout = 15000 // Increased timeout to 15 seconds
                    readTimeout = 15000 // Increased timeout to 15 seconds
                }

                // Send request body
                connection.outputStream.use { os ->
                    os.write(requestBody.toString().toByteArray())
                    os.flush()
                }

                // Read response
                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val response = connection.inputStream.bufferedReader().use { it.readText() }
                    val jsonResponse = JSONObject(response)
                    val riskPercentage = jsonResponse.getDouble("probability").toFloat() * 100

                    // Parse recommendations as array of strings
                    val recommendationsList = mutableListOf<String>()
                    val recommendationsJson = jsonResponse.optJSONArray("recommendations")
                    if (recommendationsJson != null) {
                        for (i in 0 until recommendationsJson.length()) {
                            recommendationsList.add(recommendationsJson.optString(i))
                        }
                    } else {
                        val singleRecommendation = jsonResponse.optString("recommendations", "No recommendations available.")
                        recommendationsList.add(singleRecommendation)
                    }

                    viewModelScope.launch(Dispatchers.Main) {
                        _predictionResult.value = riskPercentage
                        _recommendations.value = recommendationsList
                    }

                    viewModelScope.launch(Dispatchers.Main) {
                        _predictionResult.value = riskPercentage
                        _isLoading.value = false
                    }
                } else {
                    val errorMessage =
                        connection.errorStream?.bufferedReader()?.use { it.readText() }
                            ?: "HTTP $responseCode: Unknown error"
                    throw Exception(errorMessage)
                }

                connection.disconnect()

            } catch (e: java.net.SocketTimeoutException) {
                Log.e("ResultsViewModel", "API request timeout", e)
                viewModelScope.launch(Dispatchers.Main) {
                    _error.value = "Connection timed out. Please check your network or server."
                    _isLoading.value = false
                    _predictionResult.value = 0f
                    _recommendations.value = listOf("Could not connect to prediction server.")
                }
            } catch (e: Exception) {
                Log.e("ResultsViewModel", "API request failed", e)
                viewModelScope.launch(Dispatchers.Main) {
                    // Add specific error message for cleartext HTTP traffic
                    if (e.message?.contains("Cleartext HTTP traffic") == true) {
                        _error.value = "Cleartext HTTP traffic is not permitted. Enable cleartext support in your network security config."
                    } else {
                        _error.value = "Failed to get prediction: ${e.message}"
                    }
                    _isLoading.value = false
                    _predictionResult.value = 45f // Fallback result
                    _recommendations.value = listOf("Error retrieving recommendations.")
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
        requestJson.put("Age", patientDetails.age)
        requestJson.put(
            "BMI",
            patientDetails.weight / ((patientDetails.height / 100) * (patientDetails.height / 100))
        )
        requestJson.put("AlcoholConsumption", medicalHistory.alcoholConsumption * 2)
        requestJson.put("PhysicalActivity", medicalHistory.physicalActivity)
        requestJson.put(
            "DietQuality",
            when (medicalHistory.dietQuality) {
                "Poor" -> 2
                "Average" -> 5
                "Good" -> 8
                "Excellent" -> 10
                else -> 0
            }
        )
        requestJson.put(
            "SleepQuality", when (medicalHistory.sleepQuality) {
                "Poor" -> 4
                "Fair" -> 6
                "Good" -> 8
                "Excellent" -> 10
                else -> 4
            }
        )
        requestJson.put("SystolicBP", medicalHistory.systolicBP)
        requestJson.put("DiastolicBP", medicalHistory.diastolicBP)
        requestJson.put("MMSE", memoryTestScore * 6)

        requestJson.put("BehavioralProblems", if (memoryTestScore == 0) "Yes" else "No")
        requestJson.put(
            "CardiovascularDisease",
            if (medicalHistory.cardiovascularDisease) "Yes" else "No"
        )
        requestJson.put("Confusion", if (cognitiveSymptoms.confusion) "Yes" else "No")
        requestJson.put("Depression", if (cognitiveSymptoms.depression) "Yes" else "No")
        requestJson.put("Diabetes", if (medicalHistory.diabetes) "Yes" else "No")
        requestJson.put(
            "DifficultyCompletingTasks",
            if (cognitiveSymptoms.difficultyCompletingTasks) "Yes" else "No"
        )
        requestJson.put("Disorientation", if (cognitiveSymptoms.disorientation) "Yes" else "No")
        requestJson.put("EducationLevel", patientDetails.educationLevel)
        requestJson.put("Ethnicity", patientDetails.ethnicity)
        requestJson.put(
            "FamilyHistoryAlzheimers",
            if (medicalHistory.familyHistoryAlzheimers) "Yes" else "No"
        )
        requestJson.put("Forgetfulness", if (cognitiveSymptoms.forgetfulness) "Yes" else "No")
        requestJson.put("Gender", patientDetails.gender)
        requestJson.put("HeadInjury", if (medicalHistory.headInjury) "Yes" else "No")
        requestJson.put("Hypertension", if (medicalHistory.hypertension) "Yes" else "No")
        requestJson.put("MemoryComplaints", if (cognitiveSymptoms.memoryComplaints) "Yes" else "No")
        requestJson.put(
            "PersonalityChanges",
            if (cognitiveSymptoms.personalityChanges) "Yes" else "No"
        )
        requestJson.put(
            "Smoking", when (medicalHistory.smoking) {
                "Never" -> "No"
                "Former" -> "Yes"
                "Current" -> "Yes"
                else -> "Yes"
            }
        )

        return requestJson
    }
}
