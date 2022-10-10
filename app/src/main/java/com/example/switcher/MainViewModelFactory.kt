package com.example.switcher

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

@Suppress("UNCHECKED_CAST")
class MainViewModelFactory(private var fullUrl: String, private var authHeader: String): ViewModelProvider.Factory {
    override fun<T: ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(MainViewModel::class.java)){
            return MainViewModel(fullUrl, authHeader) as T
        }
        throw IllegalArgumentException("ViewModel class not found")
    }
}