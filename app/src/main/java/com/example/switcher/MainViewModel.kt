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

    private val _isStateUnknown = MutableLiveData(true)
    val isStateUnknown: LiveData<Boolean>
        get() = _isStateUnknown


    init {
        getStatus()
    }

    fun getStatus() {
        // Coroutine style
        viewModelScope.launch {
            _isStateUnknown.value = false
            _isLoading.value = true
            try {
                val fetchedStatus = RetrofitInstance.api.getStatus(baseUrl + SWITCHER_STATUS_PATH, authHeader)
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
        var newState: String = when(isPowered) {
            true -> "0"
            false -> "1"
        }
        switchRequestBuilder(switch, newState)
    }

    fun powerOn(switch: Int, isPowered: Boolean) {
        //0 turns ON
        var newState = "0"
        switchRequestBuilder(switch, newState)
    }

    fun powerOff(switch: Int, isPowered: Boolean) {
        //1 turns OFF
        var newState = "1"
        switchRequestBuilder(switch, newState)
    }

    private fun switchRequestBuilder(switch: Int, newState: String) {
        val builder = Uri.parse(baseUrl).buildUpon()
            .appendPath("outs.cgi")
            .appendQueryParameter("out$switch", newState)
        val myUrl: String = builder.toString()

        volleyRequest(myUrl)
    }
    private fun volleyRequest(myUrl: String) {
        _isError.value = false
        val stringRequest = object : StringRequest(
            Request.Method.GET, myUrl,
            Response.Listener<String> { response ->
                Log.d(TAG + "Volley Listener", "Response is: $response")
            },
            Response.ErrorListener { error ->
                Log.e(TAG + "Volley Listener", "Response is: $error")
                _isError.value = true
            }) {
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