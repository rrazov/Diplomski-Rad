package com.example.myfittracker

import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import com.example.myfittracker.domain.services.BleService
import com.example.myfittracker.presentation.viewmodel.SharedDevicesScreenViewModel
import com.example.myfittracker.presentation.viewmodel.ViewModelManager
import com.yucheng.ycbtsdk.YCBTClient

class MyApplication : Application(), ViewModelStoreOwner {
    lateinit var bluetoothAdapter: BluetoothAdapter
    lateinit var bleService: BleService

    override lateinit var  viewModelStore: ViewModelStore
    lateinit var sharedDevicesScreenViewModel: SharedDevicesScreenViewModel

    init {
        instance = this
    }

    override fun onCreate() {
        super.onCreate()
        val androidVersion = Build.VERSION.SDK_INT
        Log.d("MyApplication", "Android version: $androidVersion")

        viewModelStore = ViewModelStore()

        sharedDevicesScreenViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())
                .get(SharedDevicesScreenViewModel::class.java)

        ViewModelManager.initialize(this)


        // Initialize Bluetooth adapter
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter

        //Initialize bleService
        bleService = BleService()


        //Initialize YCBTSDK
        YCBTClient.initClient(applicationContext, true)

        val applicationContext = applicationContext
    }


    // Method to get the singleton instance of PersonViewModel
    fun provideSharedDevicesScreenViewModel(): SharedDevicesScreenViewModel {
        return sharedDevicesScreenViewModel
    }


    companion object {
        private var instance: MyApplication? = null

        fun applicationContext(): Context {
            return instance!!.applicationContext
        }
    }

}