package com.example.myfittracker.domain.services

import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.os.Looper
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
import com.example.myfittracker.presentation.viewmodel.SharedDevicesScreenViewModel
import com.example.myfittracker.presentation.viewmodel.ViewModelManager
import com.yucheng.ycbtsdk.Bean.ScanDeviceBean
import com.yucheng.ycbtsdk.Response.BleDataResponse
import com.yucheng.ycbtsdk.Response.BleScanResponse
import com.yucheng.ycbtsdk.YCBTClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BleService : Service() {

    private lateinit var sharedDevicesScreenViewModel: SharedDevicesScreenViewModel
    private var isEnabled = false
    private val scannedDevices = MutableLiveData<List<ScanDeviceBean>>()
    private val currentDevices = mutableListOf<ScanDeviceBean>()
    private var isProcessing = false
    private var isScanning = false
    private var startProcessing = false

    private val binder = LocalBinder()

    private val temperatureData = MutableLiveData<String?>()

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

        val app = application as MyApplication
        sharedDevicesScreenViewModel = app.provideSharedDevicesScreenViewModel()
        Log.i("BleService", "Service created, Bluetooth is enabled: $isEnabled")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        //startScan()
        Log.i("BleService", "Service started")
        return START_STICKY
    }


    fun startScan() {
        try {
            if (isEnabled && !isScanning) {

                currentDevices.clear()

                val discoveredMacAddresses = mutableSetOf<String>()


                YCBTClient.startScanBle(object : BleScanResponse {
                    override fun onScanResponse(code: Int, scanDeviceBean: ScanDeviceBean?) {
                        try {
                            scanDeviceBean?.let { it -> // Use safe call operator and let block

                                if (!discoveredMacAddresses.contains(it.deviceMac)) {

                                    discoveredMacAddresses.add(it.deviceMac)
                                    currentDevices.add(it)
                                    Log.i("BleService","currentDevices: ${currentDevices.size}")
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
                CoroutineScope(Dispatchers.IO).launch {
                    delay(5000)
                    withContext(Dispatchers.Main) {
                        scannedDevices.value?.let { it1 ->
                            sharedDevicesScreenViewModel.updateDevices(
                                it1
                            )
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("BleService", "Error starting scan", e)
            isScanning = false
        }
    }

    fun initializePersonViewModels() {
        ViewModelManager.initializeViewModels(scannedDevices)
    }


    fun startProcessingDevices() {
        Log.i("BleService", "currentDevices: ${currentDevices.size}")
        startProcessing = true
        if (currentDevices.isNotEmpty()) {
            if (!isProcessing) {
                isProcessing = true
                processDevices()
            }
        }
    }

    private fun processDevices() {
        CoroutineScope(Dispatchers.IO).launch {
            while (isProcessing && currentDevices.isNotEmpty()) {
                Log.i("BleService", "Processing devices")
                val device = currentDevices.first()
                connectToDevice(device)
                delay(10000) // Adjust delay as necessary to ensure data is fetched
                disconnectFromDevice(device)
                rotateDeviceList()
            }
            startProcessing = false
            isProcessing = false
            Log.i("BleService", "Processing stopped")
        }
    }

    private fun connectToDevice(device: ScanDeviceBean) {
        Log.i("BleService", "Connecting to device: ${device.deviceName}")
        YCBTClient.connectBle(device.deviceMac) { code ->
            if (code == 0) {
                Log.i("BleService", "Connected to device: ${device.deviceName}")
                fetchDataFromDevice(device)
            } else {
                Log.e("BleService", "Failed to connect to device: ${device.deviceName}")
            }
        }
    }

    fun fetchDataFromDevice(
        device: ScanDeviceBean
    ) {
        YCBTClient.getRealTemp(object : BleDataResponse {
            override fun onDataResponse(i: Int, v: Float, hashMap: HashMap<*, *>) {
                if (i == 0) {
                    val temp = hashMap["tempValue"] as? String
                    Log.i(
                        "BleService",
                        "Fetched data from device: ${device.deviceName}, Temp: $temp"
                    )
                    Handler(Looper.getMainLooper()).post {
                        val viewModel = ViewModelManager.getViewModel(device.deviceMac)
                        viewModel?.updateTemperature(temp)
                    }
                }
            }
        })
    }

    private fun disconnectFromDevice(device: ScanDeviceBean) {

        YCBTClient.disconnectBle()
        Log.i("BleService", "Disconnected from device: ${device.deviceName}")

    }

    private fun rotateDeviceList() {
        if (currentDevices.isNotEmpty()) {
            val device = currentDevices.removeAt(0)
            currentDevices.add(device)
            scannedDevices.postValue(currentDevices.toList())
        }
    }

    fun getScannedDevices(): LiveData<List<ScanDeviceBean>> = scannedDevices
    fun getTemperatureData(): LiveData<String?> = temperatureData // Expose LiveData

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