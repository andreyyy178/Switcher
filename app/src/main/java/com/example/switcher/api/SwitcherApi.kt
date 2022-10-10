package com.example.switcher.api


import com.example.switcher.models.SwitcherStatus
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Url



interface SwitcherApi {
    @GET
    fun getStatusViaCallback(
        @Url fullUrl: String?,
        @Header("Authorization") authHeader: String): Call<SwitcherStatus>?

    @GET
    suspend fun getStatus(
        @Url fullUrl: String?,
        @Header("Authorization") authHeader: String): SwitcherStatus
}