package com.peter.rgr.data

data class MedicalHistory(
    val diabetes: Boolean = false,
    val hypertension: Boolean = false,
    val cardiovascularDisease: Boolean = false,
    val headInjury: Boolean = false,
    val systolicBP: String = "",
    val diastolicBP: String = "",
    val alcoholConsumption: Int = 0,
    val dietQuality: String = "",
    val sleepQuality: String = "",
    val smoking: String = ""
) 