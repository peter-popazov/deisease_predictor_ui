package com.peter.rgr.repository

import android.content.Context
import android.content.SharedPreferences
import com.peter.rgr.data.MedicalHistory
import org.json.JSONObject

class MedicalHistoryRepository(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("medical_history", Context.MODE_PRIVATE)

    fun saveMedicalHistory(medicalHistory: MedicalHistory) {
        val json = JSONObject().apply {
            put("diabetes", medicalHistory.diabetes)
            put("hypertension", medicalHistory.hypertension)
            put("cardiovascularDisease", medicalHistory.cardiovascularDisease)
            put("headInjury", medicalHistory.headInjury)
            put("systolicBP", medicalHistory.systolicBP)
            put("diastolicBP", medicalHistory.diastolicBP)
            put("alcoholConsumption", medicalHistory.alcoholConsumption)
            put("dietQuality", medicalHistory.dietQuality)
            put("sleepQuality", medicalHistory.sleepQuality)
            put("smoking", medicalHistory.smoking)
        }
        sharedPreferences.edit().putString("medical_data", json.toString()).apply()
    }

    fun getMedicalHistory(): MedicalHistory {
        val jsonString = sharedPreferences.getString("medical_data", null)
        return if (jsonString != null) {
            val json = JSONObject(jsonString)
            MedicalHistory(
                diabetes = json.optBoolean("diabetes", false),
                hypertension = json.optBoolean("hypertension", false),
                cardiovascularDisease = json.optBoolean("cardiovascularDisease", false),
                headInjury = json.optBoolean("headInjury", false),
                systolicBP = json.optInt("systolicBP", 0),
                diastolicBP = json.optInt("diastolicBP", 0),
                alcoholConsumption = json.optInt("alcoholConsumption", 0),
                dietQuality = json.optString("dietQuality", ""),
                sleepQuality = json.optString("sleepQuality", ""),
                smoking = json.optString("smoking", "")
            )
        } else {
            MedicalHistory()
        }
    }
}