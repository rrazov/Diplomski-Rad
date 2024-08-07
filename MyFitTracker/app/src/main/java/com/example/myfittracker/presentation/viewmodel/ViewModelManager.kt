package com.example.myfittracker.presentation.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.yucheng.ycbtsdk.Bean.ScanDeviceBean

object ViewModelManager {
    private val viewModelMap = mutableMapOf<String, PersonViewModel>()

    private lateinit var  viewModelStoreOwner: ViewModelStoreOwner

    fun initialize(owner: ViewModelStoreOwner) {
        viewModelStoreOwner = owner
    }

    // Function to initialize ViewModel instances based on scanned devices
    fun initializeViewModels(macAddresses: MutableLiveData<List<ScanDeviceBean>>) {
        val addresses = macAddresses.value

//        addresses?.let {
//            for (address in it) {
//                if (!viewModelMap.containsKey(address.deviceMac)) {
//                    viewModelMap[address.deviceMac] =
//                        ViewModelProvider.NewInstanceFactory().create(PersonViewModel::class.java)
//
//                }
//            }
//        }

        addresses?.let {
            for (address in it) {
                if (!viewModelMap.containsKey(address.deviceMac)) {
                    val factory = PersonViewModelFactory(address.deviceMac)
                    val viewModel = ViewModelProvider(viewModelStoreOwner, factory).get(address.deviceMac, PersonViewModel::class.java)
                    viewModelMap[address.deviceMac] = viewModel
                }
            }
        }


//        macAddresses.value?.forEach { device ->
//            if (!viewModelMap.containsKey(device.deviceMac)) {
//                val viewModel = ViewModelProvider(this, PersonViewModelFactory(device.deviceMac))
//                    .get(PersonViewModel::class.java)
//                viewModelMap[device.deviceMac] = viewModel
//            }
//
//        }

    }

    // Function to get an existing ViewModel by MAC address
    fun getViewModel(macAddress: String): PersonViewModel? {
        return viewModelMap[macAddress]
    }
}