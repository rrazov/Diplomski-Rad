package com.example.myfittracker.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yucheng.ycbtsdk.Bean.ScanDeviceBean

class ScanDevicesScreenViewModel : ViewModel() {
    private val _discoveredDevicesMap = MutableLiveData<MutableMap<String, String>>(mutableMapOf())
    val discoveredDevicesMap: LiveData<MutableMap<String, String>> = _discoveredDevicesMap

    private val _devices = MutableLiveData<List<ScanDeviceBean>>(emptyList())
    val devices: LiveData<List<ScanDeviceBean>> = _devices

    fun updateDeviceName(macAddress: String, name: String) {
        val currentMap = _discoveredDevicesMap.value ?: mutableMapOf()
        currentMap[macAddress] = name
        _discoveredDevicesMap.value = currentMap
        Log.d("ScanDevicesScreenViewModel", "Updated name for $macAddress: $name")
    }

    fun updateDevices(newDevices: List<ScanDeviceBean>) {
        // Clear the previous list
        _devices.value = emptyList()
        _devices.value = newDevices
        Log.d("ScanDevicesScreenViewModel", "Updated devices list: ${newDevices.size} devices")
    }
}