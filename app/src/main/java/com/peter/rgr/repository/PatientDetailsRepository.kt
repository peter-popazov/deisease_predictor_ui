package com.peter.rgr.repository

import android.content.Context
import android.content.SharedPreferences
import com.peter.rgr.data.PatientDetails
import org.json.JSONObject
import java.io.File

class PatientDetailsRepository(private val context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("AlzheimerAssessment", Context.MODE_PRIVATE)

    fun savePatientDetails(details: PatientDetails) {
        try {
            // Save to JSON file
            val jsonObject = JSONObject().apply {
                put("age", details.age)
                put("gender", details.gender)
                put("height", details.height)
                put("weight", details.weight)
                put("bmi", details.bmi)
                put("educationLevel", details.educationLevel)
                put("ethnicity", details.ethnicity)
            }

            val file = File(context.filesDir, "personal_data.json")
            file.writeText(jsonObject.toString())

            // Save to SharedPreferences
            with(sharedPreferences.edit()) {
                putInt("age", details.age)
                putString("gender", details.gender)
                putFloat("height", details.height)
                putFloat("weight", details.weight)
                putString("educationLevel", details.educationLevel)
                putString("ethnicity", details.ethnicity)
                apply()
            }
        } catch (e: Exception) {
            throw Exception("Error saving patient details: ${e.message}")
        }
    }

    fun getPatientDetails(): PatientDetails {
        return PatientDetails(
            age = sharedPreferences.getInt("age", 0),
            gender = sharedPreferences.getString("gender", "") ?: "",
            height = sharedPreferences.getFloat("height", 0f),
            weight = sharedPreferences.getFloat("weight", 0f),
            educationLevel = sharedPreferences.getString("educationLevel", "") ?: "",
            ethnicity = sharedPreferences.getString("ethnicity", "") ?: ""
        )
    }
} 