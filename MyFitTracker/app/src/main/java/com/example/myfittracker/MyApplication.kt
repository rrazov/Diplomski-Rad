package com.example.myfittracker

import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.os.Build
import android.util.Log
import com.yucheng.ycbtsdk.YCBTClient

class MyApplication : Application() {
    lateinit var bluetoothAdapter: BluetoothAdapter

    override fun onCreate() {
        super.onCreate()
        val androidVersion = Build.VERSION.SDK_INT
        Log.d("MyApplication", "Android version: $androidVersion")

        // Initialize Bluetooth adapter
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter

        //Initialize YCBTSDK
        YCBTClient.initClient(applicationContext, true)

        val applicationContext = applicationContext
    }

}