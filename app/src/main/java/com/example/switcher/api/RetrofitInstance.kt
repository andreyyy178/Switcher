package com.example.switcher.api

//Retrofit is used to get the status of the system from an xml file
//Base URL is required to build a retrofit instance. At runtime it will be replaced with the
//user's URL from assets sw.txt file

import retrofit2.Retrofit
import retrofit2.converter.simplexml.SimpleXmlConverterFactory

private const val BASE_URL_PLACEHOLDER = "https://jsonplaceholder.typicode.com/"
object RetrofitInstance {

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL_PLACEHOLDER)
            .addConverterFactory(SimpleXmlConverterFactory.create())
            .build()
    }

    val api: SwitcherApi by lazy {
        retrofit.create(SwitcherApi::class.java)
    }
}