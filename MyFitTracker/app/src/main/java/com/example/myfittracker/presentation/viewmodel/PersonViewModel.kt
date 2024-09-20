package com.example.myfittracker.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myfittracker.domain.services.BleService
import com.yucheng.ycbtsdk.Bean.ScanDeviceBean
import kotlinx.coroutines.launch

open class PersonViewModel(
    val macAddress: String
) : ViewModel() {

    var firstFetch = true;
    private var sizeOfHeartHasMap: Int? = null
    var heartStartTime: String? = null

    private val _temperature = MutableLiveData<String?>(null)
    val temperature: MutableLiveData<String?> = _temperature

    private val _heartRate = MutableLiveData<Int?>(null)
    val heartRate: MutableLiveData<Int?> = _heartRate

    private val _bloodDBP = MutableLiveData<Int?>(null)
    val bloodDBP: MutableLiveData<Int?> = _bloodDBP

    private val _bloodSBP = MutableLiveData<Int?>(null)
    val bloodSBP: MutableLiveData<Int?> = _bloodSBP

    private val _SPO2 = MutableLiveData<Int?>(null)
    val SPO2: MutableLiveData<Int?> = _SPO2

    private val _respiratoryRate = MutableLiveData<Int?>(null)
    val respiratoryRate: MutableLiveData<Int?> = _respiratoryRate

    private val _heartRateGraphData = MutableLiveData<List<Int>>(mutableListOf())
    val heartRateGraphData: LiveData<List<Int>> = _heartRateGraphData


    fun updateTemperature(newTemperature: String?) {
        _temperature.value = newTemperature
    }

    fun updateHearthRate(newHearthRate: Int?, hashMapSize: Int?, startTimestamp: String?) {
        if (firstFetch) {
            sizeOfHeartHasMap = hashMapSize
            firstFetch = false

            _heartRate.value = newHearthRate
            heartStartTime = startTimestamp
            newHearthRate?.let { rate ->
                val updatedList = _heartRateGraphData.value.orEmpty().toMutableList().apply {
                    add(rate)
                    if (size > 20) removeAt(0)
                }
                _heartRateGraphData.value = updatedList
            }
        } else {
            val hearthSize = hashMapSize
            if (hearthSize != sizeOfHeartHasMap) {
                _heartRate.value = newHearthRate
                newHearthRate?.let { rate ->
                    val updatedList = _heartRateGraphData.value.orEmpty().toMutableList().apply {
                        add(rate)
                        if (size > 20) removeAt(0)
                    }
                    _heartRateGraphData.value = updatedList
                }
                sizeOfHeartHasMap = hashMapSize
            }
        }
    }

    fun updateBloodPressure(newBloodDBP: Int?, newBloodSPB: Int?){
        _bloodDBP.value = newBloodDBP
        _bloodSBP.value = newBloodSPB
    }

    fun updateSPO2(newSPO2: Int?){
        _SPO2.value = newSPO2
    }

    fun updateRespiratoryRate(newRespiratoryRate: Int?){
        _respiratoryRate.value = newRespiratoryRate
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