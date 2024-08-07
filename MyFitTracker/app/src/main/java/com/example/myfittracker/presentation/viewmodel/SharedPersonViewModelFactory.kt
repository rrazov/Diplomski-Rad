package com.example.myfittracker.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myfittracker.domain.services.BleService

class SharedPersonViewModelFactory(private val bleService: BleService) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SharedPersonViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SharedPersonViewModel(bleService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}