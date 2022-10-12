package com.example.switcher

import android.app.Application
import android.content.Context

//Helps to get Context from anywhere in the app
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        @get:Synchronized
        var instance: MyApplication? = null
            private set
        val appContext: Context
            get() = instance!!.applicationContext
    }
}