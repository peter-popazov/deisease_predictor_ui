package com.peter.rgr.api.models

import com.google.gson.annotations.SerializedName
import com.peter.rgr.data.MedicalHistory

data class AlzheimerAssessmentRequest(
    // Demographic Information
    @SerializedName("age")
    val age: Int,
    
    @SerializedName("gender")
    val gender: String, // "Male" or "Female"
    
    @SerializedName("ethnicity")
    val ethnicity: String, // "Caucasian", "African American", "Asian", "Other"
    
    @SerializedName("education_level")
    val educationLevel: String, // "None", "High School", "Bachelor's", "Higher"
    
    @SerializedName("bmi")
    val bmi: Double,
    
    // Lifestyle Factors
    @SerializedName("physical_activity")
    val physicalActivity: Int, // hours per week
    
    @SerializedName("diet_quality")
    val dietQuality: String, // "Very Poor" to "Excellent"
    
    @SerializedName("sleep_quality")
    val sleepQuality: String, // "Very Poor" to "Perfect"
    
    @SerializedName("smoking")
    val smoking: String, // "Never", "Former", "Current"
    
    // Medical History
    @SerializedName("family_history_alzheimers")
    val familyHistoryAlzheimers: Boolean,
    
    @SerializedName("behavioral_problems")
    val behavioralProblems: Boolean,
    
    @SerializedName("medical_history")
    val medicalHistory: MedicalHistory,
    
    // Assessment Scores
    @SerializedName("mmse_score")
    val mmseScore: Int, // 0-30
    
    @SerializedName("functional_assessment")
    val functionalAssessment: Int, // 0-10
    
    @SerializedName("adl_score")
    val adlScore: Int, // 0-10
    
    @SerializedName("memory_score")
    val memoryScore: Int, // 0-100
    
    @SerializedName("cognitive_score")
    val cognitiveScore: Int, // 0-100
    
    @SerializedName("problem_solving_score")
    val problemSolvingScore: Float, // 0-10
    
    @SerializedName("language_skills")
    val languageSkills: Int, // 0-3
    
    @SerializedName("attention_span")
    val attentionSpan: Int, // 0-60
    
    @SerializedName("decision_making")
    val decisionMaking: Float // 0-10
) 