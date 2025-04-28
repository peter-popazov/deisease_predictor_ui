package com.peter.rgr.data

data class MedicalHistory(
    val diabetes: Boolean = false,
    val hypertension: Boolean = false,
    val cardiovascularDisease: Boolean = false,
    val headInjury: Boolean = false,
    val systolicBP: Int = 0,
    val diastolicBP: Int = 0,
    val alcoholConsumption: Boolean = false,
    val confusion: Boolean = false,
    val disorientation: Boolean = false,
    val forgetfulness: Boolean = false,
    val depression: Boolean = false,
    val memoryComplaints: Boolean = false,
    val personalityChanges: Boolean = false,
    val difficultyCompletingTasks: Boolean = false
) 