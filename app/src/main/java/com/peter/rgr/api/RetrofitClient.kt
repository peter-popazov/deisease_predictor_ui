package com.peter.rgr.api

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private const val BASE_URL = "http://your-api-base-url/"
    private const val TAG = "API_CALLS"
    
    private val loggingInterceptor = HttpLoggingInterceptor { message ->
        Log.d(TAG, message)
    }.apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    
    private val requestInterceptor = HttpLoggingInterceptor { message ->
        Log.d(TAG, "Request: $message")
    }.apply {
        level = HttpLoggingInterceptor.Level.HEADERS
    }
    
    private val responseInterceptor = HttpLoggingInterceptor { message ->
        Log.d(TAG, "Response: $message")
    }.apply {
        level = HttpLoggingInterceptor.Level.HEADERS
    }
    
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request()
            Log.d(TAG, "Making API call to: ${request.url}")
            Log.d(TAG, "Method: ${request.method}")
            Log.d(TAG, "Headers: ${request.headers}")
            
            val response = chain.proceed(request)
            
            Log.d(TAG, "Response code: ${response.code}")
            Log.d(TAG, "Response headers: ${response.headers}")
            
            response
        }
        .addInterceptor(loggingInterceptor)
        .addInterceptor(requestInterceptor)
        .addInterceptor(responseInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()
    
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    
    val medicalHistoryAPI: MedicalHistoryAPI = retrofit.create(MedicalHistoryAPI::class.java)
    val alzheimerAPI: AlzheimerAPI = retrofit.create(AlzheimerAPI::class.java)

    fun <T> create(service: Class<T>): T {
        return retrofit.create(service)
    }
} 