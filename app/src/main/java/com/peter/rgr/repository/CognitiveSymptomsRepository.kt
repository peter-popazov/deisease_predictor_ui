package com.peter.rgr.repository

import android.content.Context
import android.content.SharedPreferences
import com.peter.rgr.data.CognitiveSymptoms
import org.json.JSONObject
import java.io.File
import java.io.FileWriter

class CognitiveSymptomsRepository(private val context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("AlzheimerAssessment", Context.MODE_PRIVATE)

    fun saveCognitiveSymptoms(symptoms: CognitiveSymptoms) {
        try {
            // Save to JSON file
            val symptomsData = JSONObject().apply {
                put("memoryProblems", symptoms.memoryProblems)
                put("languageProblems", symptoms.languageProblems)
                put("attentionProblems", symptoms.attentionProblems)
                put("executiveFunctionProblems", symptoms.executiveFunctionProblems)
                put("visuospatialProblems", symptoms.visuospatialProblems)
                put("socialCognitionProblems", symptoms.socialCognitionProblems)
                put("otherSymptoms", symptoms.otherSymptoms)
            }

            val file = File(context.filesDir, "cognitive_symptoms.json")
            FileWriter(file).use { writer ->
                writer.write(symptomsData.toString())
            }

            // Save to SharedPreferences
            with(sharedPreferences.edit()) {
                putBoolean("memoryProblems", symptoms.memoryProblems)
                putBoolean("languageProblems", symptoms.languageProblems)
                putBoolean("attentionProblems", symptoms.attentionProblems)
                putBoolean("executiveFunctionProblems", symptoms.executiveFunctionProblems)
                putBoolean("visuospatialProblems", symptoms.visuospatialProblems)
                putBoolean("socialCognitionProblems", symptoms.socialCognitionProblems)
                putString("otherSymptoms", symptoms.otherSymptoms)
                apply()
            }
        } catch (e: Exception) {
            throw Exception("Error saving cognitive symptoms: ${e.message}")
        }
    }

    fun getCognitiveSymptoms(): CognitiveSymptoms {
        return CognitiveSymptoms(
            memoryProblems = sharedPreferences.getBoolean("memoryProblems", false),
            languageProblems = sharedPreferences.getBoolean("languageProblems", false),
            attentionProblems = sharedPreferences.getBoolean("attentionProblems", false),
            executiveFunctionProblems = sharedPreferences.getBoolean("executiveFunctionProblems", false),
            visuospatialProblems = sharedPreferences.getBoolean("visuospatialProblems", false),
            socialCognitionProblems = sharedPreferences.getBoolean("socialCognitionProblems", false),
            otherSymptoms = sharedPreferences.getString("otherSymptoms", "") ?: ""
        )
    }
} 