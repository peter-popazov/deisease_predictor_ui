package com.peter.rgr.data

data class PatientDetails(
    val age: Int = 0,
    val gender: String = "",
    val height: Float = 0f,
    val weight: Float = 0f,
    val bmi: Double = 0.0,
    val educationLevel: String = "",
    val ethnicity: String = ""
) 