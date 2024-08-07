package com.example.myfittracker.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.example.myfittracker.domain.services.BleService

class SharedPersonViewModel(private val bleService: BleService) : ViewModel() {
    private val personViewModels = mutableMapOf<String, PersonViewModel>()

    fun getPersonViewModel(macAddress: String, name: String): PersonViewModel {
        return personViewModels.getOrPut(macAddress) {
            PersonViewModel(macAddress)
        }
    }

    fun updateTemperature(macAddress: String, temperature: String?) {
        personViewModels[macAddress]?.setTemperature(temperature)
    }
}