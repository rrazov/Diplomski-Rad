package com.example.myfittracker.domain.services

import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataScope
import androidx.lifecycle.MutableLiveData
import com.example.myfittracker.MyApplication
import com.yucheng.ycbtsdk.Bean.ScanDeviceBean
import com.yucheng.ycbtsdk.Response.BleScanResponse
import com.yucheng.ycbtsdk.YCBTClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class BleService : Service(){

    private var isEnabled = false
    private val scannedDevices = MutableLiveData<List<ScanDeviceBean>>()
    private val currentDevices = mutableListOf<ScanDeviceBean>()

    private val binder = LocalBinder()

    inner class LocalBinder : Binder() {
        fun getService(): BleService = this@BleService
    }

    override fun onBind(intent: Intent?): IBinder? {
        Log.i("BleService", "Service bound")
        return binder
    }

    override fun onCreate() {
        super.onCreate()
        val bluetoothAdapter = (applicationContext as MyApplication).bluetoothAdapter
        isEnabled = bluetoothAdapter.isEnabled
        Log.i("BleService", "Service created, Bluetooth is enabled: $isEnabled")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        startScan()
        return START_STICKY
    }


    private fun startScan(){
        try {
            if (isEnabled) {

                currentDevices.clear()

                val discoveredMacAddresses = mutableSetOf<String>()


                YCBTClient.startScanBle(object : BleScanResponse {
                    override fun onScanResponse(code: Int, scanDeviceBean: ScanDeviceBean?) {
                        try {
                            scanDeviceBean?.let { it -> // Use safe call operator and let block

                                if (!discoveredMacAddresses.contains(it.deviceMac)) {

                                    discoveredMacAddresses.add(it.deviceMac)
                                    currentDevices.add(it)
                                    scannedDevices.postValue(currentDevices.toList())
                                    Log.i(
                                        "BleService",
                                        "Device found: ${it.deviceName}, Mac address: ${it.deviceMac}"
                                    )
                                }

                            }
                        }catch (e: Exception) {
                            Log.e("BleService", "Error processing scan response", e)
                        }
                    }
                }, 5)
                Log.i("BleService", "Scan started")
            }
        }catch (e: Exception) {
            Log.e("BleService", "Error starting scan", e)
        }
    }

    fun getScannedDevices(): LiveData<List<ScanDeviceBean>> = scannedDevices

    override fun onDestroy() {
        super.onDestroy()
        Log.i("BleService", "Service destroyed")
    }

}

@Composable
fun RequestEnableBluetooth(ctx: Context,isBluetoothEnabled: Boolean) {
    val enableBluetoothLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            // Bluetooth was successfully enabled
            Toast.makeText(ctx, "Bluetooth enabled", Toast.LENGTH_SHORT).show()
            // Proceed with Bluetooth operations
        } else {
            // User declined to enable Bluetooth
            // Handle accordingly (e.g., show a message)
            Toast.makeText(ctx, "Bluetooth not enabled", Toast.LENGTH_SHORT).show()
        }
    }

    if (!isBluetoothEnabled) {
        val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        enableBluetoothLauncher.launch(enableBtIntent)
    }
}