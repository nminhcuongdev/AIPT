package com.example.aipt.core.network

import retrofit2.http.GET

interface ApiService {
    @GET("health")
    suspend fun healthCheck(): Unit
}
