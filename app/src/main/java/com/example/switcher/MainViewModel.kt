package com.example.switcher

import android.net.Uri
import android.util.Log
import androidx.lifecycle.*
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.example.switcher.api.RetrofitInstance
import com.example.switcher.api.VolleySingleton
import com.example.switcher.models.Switcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch


private const val TAG = "MainViewModel"
private const val SWITCHER_STATUS_PATH : String = "st0.xml"
class MainViewModel(var baseUrl: String, var authHeader: String) : ViewModel()  {

    private val _Switcher = MutableLiveData<Switcher>()
    val switcher: LiveData<Switcher>
        get() = _Switcher

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    private val _isError = MutableLiveData(false)
    val isError: LiveData<Boolean>
        get() = _isError

    private val _isStateUnknown = MutableLiveData(false)
    val isStateUnknown: LiveData<Boolean>
        get() = _isStateUnknown

    private val _switchIdx = MutableLiveData(-1)
    val switchIdx: LiveData<Int>
        get() = _switchIdx

    init {
        getStatus()
    }

    fun getStatus() {
        // Coroutine style
        viewModelScope.launch {
            _isStateUnknown.value = false
            _isLoading.value = true
            try {
                val fetchedStatus = RetrofitInstance.api.getStatus(baseUrl +"lol" + SWITCHER_STATUS_PATH, authHeader)
                Log.i(TAG, "Got status: $fetchedStatus")
                _Switcher.value = fetchedStatus
                _isLoading.value = false
            } catch (e: Exception) {
                Log.e(TAG, "Exception $e")
                _isLoading.value = false
                _isStateUnknown.value = true
            }
        }
    }

    fun flipPower(switch: Int, isPowered: Boolean) {
        _switchIdx.value = switch
        _isLoading.value = true
        _isError.value = false
        val newState: String = when(isPowered) {
            true -> "0"
            false -> "1"
        }

        val builder = Uri.parse(baseUrl).buildUpon()
            .appendPath("outs.cgi")
            .appendQueryParameter("out$switch", newState)
        val myUrl: String = builder.toString()
        Log.d(TAG, "Request url: $myUrl")

        val stringRequest = object: StringRequest(
            Request.Method.GET, myUrl,
            Response.Listener<String> { response ->
                Log.d(TAG + "Volley Listener", "Response is: $response")
                getStatus()
                _switchIdx.value = -1
            },
            Response.ErrorListener { error ->
                Log.e(TAG + "Volley Listener", "Response is: $error")
                _isLoading.value = false
                _isError.value = true
            })
        {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = authHeader
                return headers
            }
        }
        VolleySingleton.getInstance(MyApplication.appContext).addToRequestQueue(stringRequest)
    }


    //Experimental
    private  fun pollStatus() {
        viewModelScope.launch {
            val timer = (0..Int.MAX_VALUE)
                .asSequence()
                .asFlow()
                .onEach { delay(5_000) }
            timer.collect {
                getStatus()
            }
        }
    }
}