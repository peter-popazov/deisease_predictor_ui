package com.peter.rgr.repository

import MedicalHistory
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
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
                put("familyHistoryAlzheimers", medicalHistory.familyHistoryAlzheimers)
                put("systolicBP", medicalHistory.systolicBP)
                put("diastolicBP", medicalHistory.diastolicBP)
                put("alcoholConsumption", medicalHistory.alcoholConsumption)
                put("physicalActivity", medicalHistory.physicalActivity)
                put("dietQuality", medicalHistory.dietQuality)
                put("sleepQuality", medicalHistory.sleepQuality)
                put("smoking", medicalHistory.smoking)
            }
            sharedPreferences.edit().putString("medical_data", json.toString()).apply()
            Log.d(TAG, "Local storage save successful")

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
                familyHistoryAlzheimers = json.optBoolean("familyHistoryAlzheimers", false),
                systolicBP = json.optInt("systolicBP", 0),
                diastolicBP = json.optInt("diastolicBP", 0),
                alcoholConsumption = json.optInt("alcoholConsumption", 0),
                physicalActivity = json.optInt("physicalActivity", 0),
                dietQuality = json.optInt("dietQuality", 0),
                sleepQuality = json.optInt("sleepQuality", 0),
                smoking = json.optBoolean("smoking", false)
            )
        } else {
            Log.d(TAG, "No stored medical history found, returning empty")
            MedicalHistory()
        }
    }
}

