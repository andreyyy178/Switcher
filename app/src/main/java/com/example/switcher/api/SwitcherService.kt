package com.example.switcher.api


import com.example.switcher.models.Status
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Url



interface SwitcherService {
    @GET
    fun getStatus(
        @Url fullUrl: String?,
        @Header("Authorization") authHeader: String): Call<Status>?
}