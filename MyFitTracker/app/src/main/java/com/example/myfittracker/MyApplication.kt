package com.example.myfittracker

import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import com.yucheng.ycbtsdk.YCBTClient

class MyApplication : Application() {
    lateinit var bluetoothAdapter: BluetoothAdapter

    override fun onCreate() {
        super.onCreate()
        // Initialize Bluetooth adapter
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter

        //Initialize YCBTSDK
        YCBTClient.initClient(applicationContext, true)
    }

}