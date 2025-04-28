package com.peter.rgr.api.models

import com.google.gson.annotations.SerializedName

data class CognitiveSymptoms(
    @SerializedName("confusion")
    val confusion: Boolean,
    
    @SerializedName("disorientation")
    val disorientation: Boolean,
    
    @SerializedName("forgetfulness")
    val forgetfulness: Boolean,
    
    @SerializedName("depression")
    val depression: Boolean,
    
    @SerializedName("memory_complaints")
    val memoryComplaints: Boolean,
    
    @SerializedName("personality_changes")
    val personalityChanges: Boolean,
    
    @SerializedName("difficulty_completing_tasks")
    val difficultyCompletingTasks: Boolean
) 