package com.example.myfittracker.presentation.viewmodel

import androidx.lifecycle.LiveData
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

    private val _heartRateGraphData = MutableLiveData<List<Int>>(mutableListOf())
    val heartRateGraphData: LiveData<List<Int>> = _heartRateGraphData

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
        newHearthRate?.let { rate ->
           val updatedList = _heartRateGraphData.value.orEmpty().toMutableList().apply {
                add(rate)
                if (size > 20) removeAt(0)
            }
            _heartRateGraphData.value = updatedList
        }
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