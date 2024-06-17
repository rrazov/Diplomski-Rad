package com.example.myfittracker.domain.services

import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
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

    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    override fun onCreate() {
        super.onCreate()

        val bluetoothAdapter = (applicationContext as MyApplication).bluetoothAdapter
        isEnabled = bluetoothAdapter.isEnabled



    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        startScan()
        return START_STICKY
    }


    private fun startScan(){
        if(isEnabled){
            YCBTClient.startScanBle(object : BleScanResponse {
                override fun onScanResponse(code: Int, scanDeviceBean: ScanDeviceBean?) {
                    scanDeviceBean?.let { // Use safe call operator and let block
                        val deviceName = it.deviceName
                        val deviceMac = it.deviceMac
                        Log.i("ScanCallback", "Device Name: $deviceName")
                        Log.i("ScanCallback", "Device Address: $deviceMac")
//
                    } ?: run {
                        Log.e("ScanCallback", "ScanDeviceBean is null for code: $code")
                    }
                }
            }, 5)

        }

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