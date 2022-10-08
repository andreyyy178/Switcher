package com.example.switcher.api

import okhttp3.Interceptor
import okhttp3.Response

class HeaderInterceptor(private val credentials: String) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
            .newBuilder()
            .addHeader("Authorization", credentials)
            .build()
        return chain.proceed(request)
    }

}