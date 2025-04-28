package com.peter.rgr.api.models

import com.google.gson.annotations.SerializedName

data class MedicalHistoryRequest(
    @SerializedName("diabetes")
    val diabetes: Boolean,

    @SerializedName("hypertension")
    val hypertension: Boolean,

    @SerializedName("cardiovascular_disease")
    val cardiovascularDisease: Boolean,

    @SerializedName("head_injury")
    val headInjury: Boolean,

    @SerializedName("alcohol_consumption")
    val alcoholConsumption: Boolean,

    @SerializedName("systolic_bp")
    val systolicBP: Int,

    @SerializedName("diastolic_bp")
    val diastolicBP: Int,

    @SerializedName("cognitive_symptoms")
    val cognitiveSymptoms: CognitiveSymptoms
) 