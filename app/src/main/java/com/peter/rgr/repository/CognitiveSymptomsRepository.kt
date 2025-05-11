package com.peter.rgr.repository

import android.content.Context
import android.content.SharedPreferences
import com.peter.rgr.data.CognitiveSymptoms
import org.json.JSONObject
import java.io.File
import java.io.FileWriter

class CognitiveSymptomsRepository(private val context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("AlzheimerAssessment", Context.MODE_PRIVATE)

    fun saveCognitiveSymptoms(cognitiveSymptoms: CognitiveSymptoms) {
        try {
            // Save to JSON file
            val symptomsData = JSONObject().apply {
                put("confusion", cognitiveSymptoms.confusion)
                put("disorientation", cognitiveSymptoms.disorientation)
                put("forgetfulness", cognitiveSymptoms.forgetfulness)
                put("depression", cognitiveSymptoms.depression)
                put("memory_complaints", cognitiveSymptoms.memoryComplaints)
                put("personality_changes", cognitiveSymptoms.personalityChanges)
                put("difficulty_completing_tasks", cognitiveSymptoms.difficultyCompletingTasks)
            }

            val file = File(context.filesDir, "cognitive_symptoms.json")
            FileWriter(file).use { writer ->
                writer.write(symptomsData.toString())
            }
        } catch (e: Exception) {
            throw Exception("Error saving cognitive symptoms: ${e.message}")
        }
    }

    fun getCognitiveSymptoms(): CognitiveSymptoms {
        return CognitiveSymptoms(
            confusion = sharedPreferences.getBoolean("confusion", false),
            disorientation = sharedPreferences.getBoolean("disorientation", false),
            forgetfulness = sharedPreferences.getBoolean("forgetfulness", false),
            depression = sharedPreferences.getBoolean("depression", false),
            memoryComplaints = sharedPreferences.getBoolean("memoryComplaints", false),
            personalityChanges = sharedPreferences.getBoolean("personalityChanges", false),
            difficultyCompletingTasks = sharedPreferences.getBoolean("difficultyCompletingTasks", false)
        )
    }
} 