package com.peter.rgr.api

import com.peter.rgr.api.models.MedicalHistoryRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface MedicalHistoryAPI {
    @POST("api/medical-history")
    suspend fun saveMedicalHistory(@Body request: MedicalHistoryRequest): Response<Unit>
} 