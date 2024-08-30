package com.example.myfittracker.presentation.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myfittracker.domain.services.BleService
import com.yucheng.ycbtsdk.Bean.ScanDeviceBean
import kotlinx.coroutines.launch

open class PersonViewModel(
    val macAddress: String,
    //val name: String,
    //private val bleService: BleService
) : ViewModel() {
    private val _temperature = MutableLiveData<String?>(null)
    val temperature: MutableLiveData<String?> = _temperature

    private val _heartRate = MutableLiveData<Int?>(null)
    val heartRate: MutableLiveData<Int?> = _heartRate

//    init {
//        viewModelScope.launch {
//            fetchTemperature()
//        }
//    }

    fun updateTemperature(newTemperature: String?) {
        _temperature.value = newTemperature
    }

    fun updateHearthRate(newHearthRate: Int?) {
        _heartRate.value = newHearthRate
    }

    open fun setTemperature(temperature: String?) {
        _temperature.value = temperature

    }

//    fun fetchTemperature() {
//        val deviceBean = ScanDeviceBean()
//        deviceBean.deviceMac = macAddress
//        //deviceBean.deviceName = name
//
//        bleService.fetchDataFromDevice(deviceBean)
//        { fetchedTemperature ->
//            _temperature.postValue(fetchedTemperature)
//        }
//    }


}