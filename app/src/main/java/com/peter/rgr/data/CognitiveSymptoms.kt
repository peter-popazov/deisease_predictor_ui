package com.peter.rgr.data

data class CognitiveSymptoms(
    val confusion: Boolean = false,
    val disorientation: Boolean = false,
    val forgetfulness: Boolean = false,
    val depression: Boolean = false,
    val memoryComplaints: Boolean = false,
    val personalityChanges: Boolean = false,
    val difficultyCompletingTasks: Boolean = false,
) 