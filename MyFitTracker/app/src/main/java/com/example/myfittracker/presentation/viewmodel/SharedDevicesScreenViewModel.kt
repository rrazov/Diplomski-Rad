package com.example.myfittracker.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yucheng.ycbtsdk.Bean.ScanDeviceBean
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SharedDevicesScreenViewModel : ViewModel() {

    private val _isScanned = MutableLiveData(false)
    val isScanned : LiveData<Boolean> = _isScanned

    private val _discoveredDevicesMap = MutableLiveData<MutableMap<String, String>>(mutableMapOf())
    val discoveredDevicesMap: LiveData<MutableMap<String, String>> = _discoveredDevicesMap

    private val _devices = MutableLiveData<List<ScanDeviceBean>>(emptyList())
    val devices: LiveData<List<ScanDeviceBean>> = _devices

    fun startScanning(){
        viewModelScope.launch {
            delay(6000)
            _isScanned.value = true
        }
    }

    fun updateDeviceName(macAddress: String, name: String) {
        val currentMap = _discoveredDevicesMap.value ?: mutableMapOf()
        currentMap[macAddress] = name
        _discoveredDevicesMap.value = currentMap
        Log.d("SharedDevicesScreenViewModel", "Updated name for $macAddress: $name")
    }

    fun updateDevices(newDevices: List<ScanDeviceBean>) {
        // Clear the previous list
        _devices.value = emptyList()
        _devices.value = newDevices
        Log.d("SharedDevicesScreenViewModel", "Updated devices list: ${newDevices.size} devices")
        Log.d("SharedDevicesScreenViewModel", "Discovered Devices Map: $_discoveredDevicesMap.value")
    }

}