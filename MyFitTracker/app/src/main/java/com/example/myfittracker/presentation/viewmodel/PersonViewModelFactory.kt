package com.example.myfittracker.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myfittracker.domain.services.BleService

class PersonViewModelFactory(
    private val macAddress: String,
) : ViewModelProvider.Factory
{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PersonViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PersonViewModel(macAddress) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}