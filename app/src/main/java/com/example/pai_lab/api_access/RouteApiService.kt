package com.example.pai_lab.api_access

import retrofit2.http.GET
import retrofit2.http.Query

interface RouteApiService {
    @GET("interpreter")
    suspend fun getTouristRoutes(
        @Query("data") query: String
    ): OverpassResponse
}