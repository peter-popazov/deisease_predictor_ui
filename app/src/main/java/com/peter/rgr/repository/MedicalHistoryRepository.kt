package com.peter.rgr.repository

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.edit
import com.peter.rgr.api.models.CognitiveSymptoms
import com.peter.rgr.api.models.MedicalHistoryRequest
import com.peter.rgr.data.MedicalHistory
import org.json.JSONObject

class MedicalHistoryRepository(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("medical_history", Context.MODE_PRIVATE)
    private val TAG = "MedicalHistoryRepo"

    fun saveMedicalHistory(medicalHistory: MedicalHistory): Result<Unit> {
        return try {
            Log.d(TAG, "Starting to save medical history")

            // Save locally
            Log.d(TAG, "Saving to local storage")
            val json = JSONObject().apply {
                put("diabetes", medicalHistory.diabetes)
                put("hypertension", medicalHistory.hypertension)
                put("cardiovascularDisease", medicalHistory.cardiovascularDisease)
                put("headInjury", medicalHistory.headInjury)
                put("systolicBP", medicalHistory.systolicBP)
                put("diastolicBP", medicalHistory.diastolicBP)
                put("alcoholConsumption", medicalHistory.alcoholConsumption)
                put("confusion", medicalHistory.confusion)
                put("disorientation", medicalHistory.disorientation)
                put("forgetfulness", medicalHistory.forgetfulness)
                put("depression", medicalHistory.depression)
                put("memoryComplaints", medicalHistory.memoryComplaints)
                put("personalityChanges", medicalHistory.personalityChanges)
                put("difficultyCompletingTasks", medicalHistory.difficultyCompletingTasks)
            }
            sharedPreferences.edit() { putString("medical_data", json.toString()) }
            Log.d(TAG, "Local storage save successful")

            // Save to API
            Log.d(TAG, "Preparing API request")
            val request = MedicalHistoryRequest(
                diabetes = medicalHistory.diabetes,
                hypertension = medicalHistory.hypertension,
                cardiovascularDisease = medicalHistory.cardiovascularDisease,
                headInjury = medicalHistory.headInjury,
                systolicBP = medicalHistory.systolicBP,
                diastolicBP = medicalHistory.diastolicBP,
                alcoholConsumption = medicalHistory.alcoholConsumption,
                cognitiveSymptoms = CognitiveSymptoms(
                    confusion = medicalHistory.confusion,
                    disorientation = medicalHistory.disorientation,
                    forgetfulness = medicalHistory.forgetfulness,
                    depression = medicalHistory.depression,
                    memoryComplaints = medicalHistory.memoryComplaints,
                    personalityChanges = medicalHistory.personalityChanges,
                    difficultyCompletingTasks = medicalHistory.difficultyCompletingTasks
                )
            )
            Log.d(TAG, "Sending API request: $request")

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error saving medical history", e)
            Result.failure(e)
        }
    }

    fun getMedicalHistory(): MedicalHistory {
        Log.d(TAG, "Retrieving medical history from local storage")
        val jsonString = sharedPreferences.getString("medical_data", null)
        return if (jsonString != null) {
            Log.d(TAG, "Found stored medical history")
            val json = JSONObject(jsonString)
            MedicalHistory(
                diabetes = json.optBoolean("diabetes", false),
                hypertension = json.optBoolean("hypertension", false),
                cardiovascularDisease = json.optBoolean("cardiovascularDisease", false),
                headInjury = json.optBoolean("headInjury", false),
                systolicBP = json.optInt("systolicBP", 0),
                diastolicBP = json.optInt("diastolicBP", 0),
                alcoholConsumption = json.optInt("alcoholConsumption", 0),
                confusion = json.optBoolean("confusion", false),
                disorientation = json.optBoolean("disorientation", false),
                forgetfulness = json.optBoolean("forgetfulness", false),
                depression = json.optBoolean("depression", false),
                memoryComplaints = json.optBoolean("memoryComplaints", false),
                personalityChanges = json.optBoolean("personalityChanges", false),
                difficultyCompletingTasks = json.optBoolean("difficultyCompletingTasks", false)
            )
        } else {
            Log.d(TAG, "No stored medical history found, returning empty")
            MedicalHistory()
        }
    }
}