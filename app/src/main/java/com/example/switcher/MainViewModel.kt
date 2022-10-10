package com.example.switcher


import android.util.Log
import androidx.lifecycle.*
import com.example.switcher.api.RetrofitInstance
import com.example.switcher.models.SwitcherStatus
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.lang.Exception

private const val TAG = "MainViewModel"
class MainViewModel(var fullUrl: String, var authHeader: String) : ViewModel()  {

    private val _SwitcherStatus = MutableLiveData<SwitcherStatus>()
    val switcherStatus: LiveData<SwitcherStatus>
        get() = _SwitcherStatus

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    private val _isError = MutableLiveData(false)
    val isError: LiveData<Boolean>
        get() = _isError

    init {
        getStatus()
    }

    private fun getStatus() {
        // Coroutine style
        viewModelScope.launch {
            _isError.value = false
            _isLoading.value = true
            try {
                val fetchedStatus = RetrofitInstance.api.getStatus(fullUrl, authHeader)
                Log.i(TAG, "Got status: $fetchedStatus")
                _SwitcherStatus.value = fetchedStatus
            } catch (e: Exception) {
                _isError.value = true
                Log.e(TAG, "Exception $e")
            } finally {
                _isLoading.value = false
            }
        }
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