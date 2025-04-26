package com.peter.rgr.repository

import android.content.Context
import android.content.SharedPreferences
import com.peter.rgr.data.PatientDetails
import org.json.JSONObject

class PatientDetailsRepository(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("patient_details", Context.MODE_PRIVATE)

    fun savePatientDetails(patientDetails: PatientDetails) {
        val json = JSONObject().apply {
            put("age", patientDetails.age)
            put("gender", patientDetails.gender)
            put("height", patientDetails.height)
            put("weight", patientDetails.weight)
            put("educationLevel", patientDetails.educationLevel)
            put("ethnicity", patientDetails.ethnicity)
        }
        sharedPreferences.edit().putString("patient_data", json.toString()).apply()
    }

    fun getPatientDetails(): PatientDetails {
        val jsonString = sharedPreferences.getString("patient_data", null)
        return if (jsonString != null) {
            val json = JSONObject(jsonString)
            PatientDetails(
                age = json.optInt("age", 0),
                gender = json.optString("gender", ""),
                height = json.optDouble("height", 0.0).toFloat(),
                weight = json.optDouble("weight", 0.0).toFloat(),
                educationLevel = json.optString("educationLevel", ""),
                ethnicity = json.optString("ethnicity", "")
            )
        } else {
            PatientDetails()
        }
    }
} 