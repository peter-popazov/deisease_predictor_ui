package com.peter.rgr.api.models

import com.google.gson.annotations.SerializedName

data class AlzheimerAssessmentResponse(
    @SerializedName("probability")
    val probability: Double, // 0.0 to 1.0
    
    @SerializedName("prediction")
    val prediction: Int, // 0 for No Alzheimer's, 1 for Alzheimer's
    
    @SerializedName("risk_level")
    val riskLevel: String, // "LOW", "MODERATE", "HIGH"
    
    @SerializedName("recommendations")
    val recommendations: List<String>,
    
    @SerializedName("risk_factors")
    val riskFactors: List<RiskFactor>,
    
    @SerializedName("confidence_score")
    val confidenceScore: Double // 0.0 to 1.0
)

data class RiskFactor(
    @SerializedName("factor")
    val factor: String,
    
    @SerializedName("severity")
    val severity: String, // "LOW", "MODERATE", "HIGH"
    
    @SerializedName("description")
    val description: String,
    
    @SerializedName("recommendation")
    val recommendation: String
) 