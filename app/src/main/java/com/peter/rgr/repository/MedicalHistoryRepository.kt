package com.peter.rgr.repository

import android.content.Context
import android.content.SharedPreferences
import com.peter.rgr.data.MedicalHistory
import org.json.JSONObject
import java.io.File
import java.io.FileWriter

class MedicalHistoryRepository(private val context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("AlzheimerAssessment", Context.MODE_PRIVATE)

    fun saveMedicalHistory(medicalHistory: MedicalHistory) {
        try {
            // Save to JSON file
            val medicalData = JSONObject().apply {
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

            val file = File(context.filesDir, "medical_history.json")
            FileWriter(file).use { writer ->
                writer.write(medicalData.toString())
            }

            // Save to SharedPreferences
            with(sharedPreferences.edit()) {
                putBoolean("diabetes", medicalHistory.diabetes)
                putBoolean("hypertension", medicalHistory.hypertension)
                putBoolean("cardiovascularDisease", medicalHistory.cardiovascularDisease)
                putBoolean("headInjury", medicalHistory.headInjury)
                putString("systolicBP", medicalHistory.systolicBP)
                putString("diastolicBP", medicalHistory.diastolicBP)
                putInt("alcoholConsumption", medicalHistory.alcoholConsumption)
                putString("dietQuality", medicalHistory.dietQuality)
                putString("sleepQuality", medicalHistory.sleepQuality)
                putString("smoking", medicalHistory.smoking)
                apply()
            }
        } catch (e: Exception) {
            throw Exception("Error saving medical data: ${e.message}")
        }
    }

    fun getMedicalHistory(): MedicalHistory {
        return MedicalHistory(
            diabetes = sharedPreferences.getBoolean("diabetes", false),
            hypertension = sharedPreferences.getBoolean("hypertension", false),
            cardiovascularDisease = sharedPreferences.getBoolean("cardiovascularDisease", false),
            headInjury = sharedPreferences.getBoolean("headInjury", false),
            systolicBP = sharedPreferences.getString("systolicBP", "") ?: "",
            diastolicBP = sharedPreferences.getString("diastolicBP", "") ?: "",
            alcoholConsumption = sharedPreferences.getInt("alcoholConsumption", 0),
            dietQuality = sharedPreferences.getString("dietQuality", "") ?: "",
            sleepQuality = sharedPreferences.getString("sleepQuality", "") ?: "",
            smoking = sharedPreferences.getString("smoking", "") ?: ""
        )
    }
}