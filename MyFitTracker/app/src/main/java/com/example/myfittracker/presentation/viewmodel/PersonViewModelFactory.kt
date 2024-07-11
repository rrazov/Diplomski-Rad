package com.example.myfittracker.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myfittracker.domain.services.BleService

class PersonViewModelFactory(
    private val macAddress: String,
    private val name: String,
    private val bleService: BleService
) : ViewModelProvider.Factory
{
    override fun <T : ViewModel> create(modelClass: Class<T>): T{
        if (modelClass.isAssignableFrom(PersonViewModel::class.java)){
            @Suppress("UNCHECKED_CAST")
            return PersonViewModel(macAddress, name, bleService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}