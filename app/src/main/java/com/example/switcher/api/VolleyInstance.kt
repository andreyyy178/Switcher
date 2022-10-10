package com.example.switcher.api

//Volley is used for sending a simple http request from the MainActivity that turn different switches on or off
//no need to get a response because Retrofit with SimpleXml converter factory takes care of that
//and the UI is updated from the MainViewModel

import android.content.Context
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley


class VolleyInstance constructor(context: Context) {
    companion object {
        @Volatile
        private var INSTANCE: VolleyInstance? = null
        fun getInstance(context: Context) =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: VolleyInstance(context).also {
                    INSTANCE = it
                }
            }
    }
    val requestQueue: RequestQueue by lazy {
        // applicationContext is key, it keeps you from leaking the
        // Activity or BroadcastReceiver if someone passes one in.
        Volley.newRequestQueue(context.applicationContext)
    }
    fun <T> addToRequestQueue(req: Request<T>) {
        requestQueue.add(req)
    }
}