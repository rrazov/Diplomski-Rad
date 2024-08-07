package com.example.myfittracker.presentation.viewmodel

import com.example.myfittracker.domain.services.BleService

class TestPersonViewModel(
    macAddress: String,
    name: String,
    bleService: BleService
) : PersonViewModel(macAddress) {
    override fun setTemperature(temperature: String?) {
        super.setTemperature(temperature)
    }
}