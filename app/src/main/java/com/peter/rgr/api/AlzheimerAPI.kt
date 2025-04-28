package com.peter.rgr.api

import com.peter.rgr.api.models.AlzheimerAssessmentRequest
import com.peter.rgr.api.models.AlzheimerAssessmentResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AlzheimerAPI {
    @POST("api/assess")
    suspend fun assessAlzheimerRisk(@Body request: AlzheimerAssessmentRequest): Response<AlzheimerAssessmentResponse>
} 