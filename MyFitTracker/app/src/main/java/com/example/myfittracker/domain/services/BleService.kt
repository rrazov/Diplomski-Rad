package com.example.myfittracker.domain.services

import android.app.Service
import android.bluetooth.BluetoothAdapter
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
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.myfittracker.MyApplication
import com.example.myfittracker.presentation.viewmodel.SharedDevicesScreenViewModel
import com.example.myfittracker.presentation.viewmodel.ViewModelManager
import com.yucheng.ycbtsdk.Bean.ScanDeviceBean
import com.yucheng.ycbtsdk.Constants
import com.yucheng.ycbtsdk.Response.BleDataResponse
import com.yucheng.ycbtsdk.Response.BleRealDataResponse
import com.yucheng.ycbtsdk.Response.BleScanResponse
import com.yucheng.ycbtsdk.YCBTClient
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.log

//class BleService : Service() {
//
//    private lateinit var sharedDevicesScreenViewModel: SharedDevicesScreenViewModel
//    private var isEnabled = false
//    private val scannedDevices = MutableLiveData<List<ScanDeviceBean>>()
//    private val currentDevices = mutableListOf<ScanDeviceBean>()
//    private var isProcessing = false
//    private var isScanning = false
//    private var startProcessing = false
//
//    private val binder = LocalBinder()
//
//    private val temperatureData = MutableLiveData<String?>()
//
//    inner class LocalBinder : Binder() {
//        fun getService(): BleService = this@BleService
//    }
//
//    override fun onBind(intent: Intent?): IBinder? {
//        Log.i("BleService", "Service bound")
//        return binder
//    }
//
//    override fun onCreate() {
//        super.onCreate()
//        val bluetoothAdapter = (applicationContext as MyApplication).bluetoothAdapter
//        isEnabled = bluetoothAdapter.isEnabled
//
//        val app = application as MyApplication
//        sharedDevicesScreenViewModel = app.provideSharedDevicesScreenViewModel()
//        Log.i("BleService", "Service created, Bluetooth is enabled: $isEnabled")
//    }
//
//    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//        super.onStartCommand(intent, flags, startId)
//
//        //startScan()
//        Log.i("BleService", "Service started")
//        return START_STICKY
//    }
//
//
//    fun startScan() {
//        try {
//            if (isEnabled && !isScanning) {
//
//                currentDevices.clear()
//
//                val discoveredMacAddresses = mutableSetOf<String>()
//
//
//                YCBTClient.startScanBle(object : BleScanResponse {
//                    override fun onScanResponse(code: Int, scanDeviceBean: ScanDeviceBean?) {
//                        try {
//                            scanDeviceBean?.let { it -> // Use safe call operator and let block
//
//                                if (!discoveredMacAddresses.contains(it.deviceMac)) {
//
//                                    discoveredMacAddresses.add(it.deviceMac)
//                                    currentDevices.add(it)
//                                    Log.i("BleService","currentDevices: ${currentDevices.size}")
//                                    scannedDevices.postValue(currentDevices.toList())
//                                    Log.i(
//                                        "BleService",
//                                        "Device found: ${it.deviceName}, Mac address: ${it.deviceMac}"
//                                    )
//                                }
//                            }
//                        }catch (e: Exception) {
//                            Log.e("BleService", "Error processing scan response", e)
//                        }
//                    }
//                }, 5)
//
//                Log.i("BleService", "Scan started")
//                CoroutineScope(Dispatchers.IO).launch {
//                    delay(5000)
//                    withContext(Dispatchers.Main) {
//                        scannedDevices.value?.let { it1 ->
//                            sharedDevicesScreenViewModel.updateDevices(
//                                it1
//                            )
//                        }
//                    }
//                }
//            }
//        } catch (e: Exception) {
//            Log.e("BleService", "Error starting scan", e)
//            isScanning = false
//        }
//    }
//
//    fun initializePersonViewModels() {
//        ViewModelManager.initializeViewModels(scannedDevices)
//    }
//
//
//    fun startProcessingDevices() {
//        Log.i("BleService", "currentDevices: ${currentDevices.size}")
//        startProcessing = true
//        if (currentDevices.isNotEmpty()) {
//            if (!isProcessing) {
//                isProcessing = true
//                processDevices()
//            }
//        }
//    }
//
//    private fun processDevices() {
//        CoroutineScope(Dispatchers.IO).launch {
//            while (isProcessing && currentDevices.isNotEmpty()) {
//                Log.i("BleService", "Processing devices")
//                val device = currentDevices.first()
//                connectToDevice(device)
//                delay(10000) // Adjust delay as necessary to ensure data is fetched
//                disconnectFromDevice(device)
//                rotateDeviceList()
//            }
//            startProcessing = false
//            isProcessing = false
//            Log.i("BleService", "Processing stopped")
//        }
//    }
//
//    private fun connectToDevice(device: ScanDeviceBean) : Boolean {
//
//        return withContext(Dispatchers.IO) {
//            Log.i("BleService", "Connecting to device: ${device.deviceName}")
//            var result = CompletableDeferred<Boolean>()
//            YCBTClient.connectBle(device.deviceMac) { code ->
//                if (code == 0) {
//                    Log.i("BleService", "Connected to device: ${device.deviceName}")
//                    result.complete(true)
//                    //fetchDataFromDevice(device)
//                } else {
//                    Log.e("BleService", "Failed to connect to device: ${device.deviceName}")
//                    result.complete(false)
//                }
//            }
//            return@withContext result.await()
//        }
//
//    }
//
//    fun fetchDataFromDevice(
//        device: ScanDeviceBean
//    ) {
//        YCBTClient.getRealTemp(object : BleDataResponse {
//            override fun onDataResponse(i: Int, v: Float, hashMap: HashMap<*, *>) {
//                if (i == 0) {
//                    val temp = hashMap["tempValue"] as? String
//                    Log.i(
//                        "BleService",
//                        "Fetched data from device: ${device.deviceName}, Temp: $temp"
//                    )
//                    Handler(Looper.getMainLooper()).post {
//                        val viewModel = ViewModelManager.getViewModel(device.deviceMac)
//                        viewModel?.updateTemperature(temp)
//                    }
//                }
//            }
//        })
//
//        YCBTClient.settingHeartMonitor(0x01, 1, object : BleDataResponse {
//            override fun onDataResponse(p0: Int, p1: Float, hashMap: HashMap<*, *>?) {
//                if (hashMap != null){
//                    for ((key, value) in hashMap) {
//                        Log.i("BleService", "Key: $key, Value: $value")
//                    }
//                }
//                Log.d("BleService", "Monitor setted to 1 min for ${device.deviceMac}")
//            }
//        })
//
//        YCBTClient.settingBloodOxygenModeMonitor(false, 1, object : BleDataResponse{
//            override fun onDataResponse(p0: Int, p1: Float, hashMap: HashMap<*, *>?) {
//                if (hashMap != null) {
//                    for ((key, value) in hashMap) {
//                        Log.d("Ble", "BloodOxygen turned on")
//                        Log.d("bleService", "Key: $key, Value: $value")
//                    }
//                }
//            }
//        })
//        // DBIVAN 0 ZA VRIJEDNOSTI
////        YCBTClient.getNowStep(object : BleDataResponse{
////            override fun onDataResponse(p0: Int, p1: Float, hashMap: HashMap<*, *>?) {
////                if (hashMap != null){
////                    for ((key, value) in hashMap) {
////                        Log.d("BleService", "Key: $key, Value: $value")
////                    }
////                }
////            }
////        })
//
////        YCBTClient.appRegisterRealDataCallBack(object : BleRealDataResponse {
////            override fun onRealDataResponse(p0: Int, hashMap: HashMap<*, *>?) {
////                Log.d("BleService", "register = $p0")
////                if (hashMap != null) {
////                    for ((key, value) in hashMap) {
////                        Log.d("BleService", "Key: $key, Value: $value")
////                    }
////                }
////            }
////
////        })
//
////        // ODE NE DOBIVAN NISTA
////        YCBTClient.getRealBloodOxygen(object : BleDataResponse{
////            override fun onDataResponse(p0: Int, p1: Float, hashMap: HashMap<*, *>?) {
////                if (hashMap != null){
////                    for ((key, value) in hashMap){
////                        Log.d("BleService", "Key(bloodOxygen): $key, Value: $value")
////                    }
////                }
////            }
////        })
//
//        YCBTClient.healthHistoryData(Constants.DATATYPE.Health_HistoryBlood, object : BleDataResponse{
//                override fun onDataResponse(i: Int, v: Float, hashMap: HashMap<*, *>?) {
//                    if (hashMap != null) {
//
//                        val lists = hashMap["data"] as ArrayList<HashMap<*, *>>
//                        var lastBloodDBP : Int? = null
//                        var lastBloodSBP : Int? = null
//
//                        //Log.d("BleService", "Key: $key, Value: $value")
//                        for (map in lists) {
//                            lastBloodDBP = map["bloodDBP"] as? Int
//                            lastBloodSBP = map["bloodSBP"] as? Int
////                            val StartTime = map["bloodStartTime"] as? Long
////                            val EndTime = map["bloodEndTime"] as? Long
////                            val dateFormat =
////                                SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
////                            val formattedStartTime = StartTime?.let { dateFormat.format(Date(it)) }
////                            val formattedEndTime = EndTime?.let { dateFormat.format(Date(it)) }
//
////                            Log.d("BleService", "sportStartTime: $formattedStartTime")
////                            Log.d("BleService", "heartValue: $lastBloodDBP")
////                            Log.d("BleService", "heartValue: $lastBloodSBP")
//                        }
//                        Handler(Looper.getMainLooper()).post {
//                            val viewModel = ViewModelManager.getViewModel(device.deviceMac)
//                            viewModel?.updateBloodPressure(lastBloodDBP, lastBloodSBP)
//                        }
//
//                    }
//                }
//            })
//
//        YCBTClient.healthHistoryData(Constants.DATATYPE.Health_HistoryHeart, object : BleDataResponse {
//                override fun onDataResponse(i: Int, v: Float, hashMap: HashMap<*, *>) {
//                    if (hashMap != null) {
//
////                        Log.d("BleService", "heartValue = $lastHeartValue")
//
//
////                        for ((key, value) in hashMap){
////                            Log.d("BleService", "Key: $key, Value: $value")
////                        }
//                        val lists = hashMap["data"] as ArrayList<HashMap<*, *>>
//                        var heartValue : Int? = null
//                        var counter = 0
//                        var formattedStartTime: String? = null
//
//                        for (map in lists) {
//
//                            heartValue = map["heartValue"] as? Int
//                            val StartTime = map["heartStartTime"] as? Long
//                            val dateFormat =
//                                SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
//                            formattedStartTime = StartTime?.let { dateFormat.format(Date(it)) }
////                            val formattedEndTime = EndTime?.let { dateFormat.format(Date(it)) }
////                            //
////                            //                        val sportStep = map["sportStep"] as? Int
////                            //                        val sportDistance = map["sportDistance"] as? Int
////                            //                        val sportCalorie = map["sportCalorie"] as? Int
////                            //
////                            Log.d("BleService", "sportStartTime: $formattedStartTime")
////                            Log.d("BleService", "heartValue: $heartValue")
//                            //                        Log.d("BleService", "sportStep: $sportStep")
//                            //                        Log.d("BleService", "sportDistance: $sportDistance")
//                            //                        Log.d("BleService", "sportCalorie: $sportCalorie")
//                            counter++
//                        }
//
//                        Handler(Looper.getMainLooper()).post {
//                            val viewModel = ViewModelManager.getViewModel(device.deviceMac)
//                            viewModel?.updateHearthRate(heartValue, counter, formattedStartTime)
//                        }
//                    }
//                }
//
//            })
//
//        YCBTClient.getNowStep( object : BleDataResponse{
//            override fun onDataResponse(p0: Int, p1: Float, hashMap: HashMap<*, *>?) {
//                Log.d("BleService", "p0: $p0, p1: $p1")
//                if (hashMap != null) {
//                    for((key, value) in hashMap){
//                        Log.d("BleService", "key(getNowStep): $key, value: $value")
//                    }
//                }
//            }
//        })
//
//        YCBTClient.healthHistoryData(Constants.DATATYPE.SettingStepCountingStateTime, object : BleDataResponse {
//                override fun onDataResponse(i: Int, v: Float, hashMap: HashMap<*, *>){
//                    if (hashMap != null) {
//////                    Log.d("BleService", "v: $v")
//                        for ((key, value) in hashMap.entries){
//                            Log.d("BleService", "Key: $key, Value: $value")
//                        }
////                    val lists = hashMap["data"] as ArrayList<HashMap<*, *>>
////                    for (map in lists) {
////                        val bloodOxygen =
////                            map["OOValue"] as Int? ?: 0 // Handle potential null and default to 0
////                        val tempIntValue = map["tempIntValue"] as Int? ?: 0
////                        val tempFloatValue = map["tempFloatValue"] as Int? ?: 0
////                        val hrv = map["hrvValue"] as Int? ?: 0
////                        val cvrr = map["cvrrValue"] as Int? ?: 0
////                        val respiratoryRateValue = map["respiratoryRateValue"] as Int? ?: 0
////                        val startTime = map["startTime"] as Long? ?: 0L
////
////
////                        // Log the extracted data
////                        Log.d("BleService", "Blood Oxygen: $bloodOxygen")
////                        Log.d("BleService", "Temperature (Int): $tempIntValue")
////                        Log.d("BleService", "Temperature (Float): $tempFloatValue")
////                        Log.d("BleService", "HRV: $hrv")
////                        Log.d("BleService", "CVRR: $cvrr")
////                        Log.d("BleService", "Respiratory Rate: $respiratoryRateValue")
////                        Log.d("BleService", "Start Time: $startTime")
////
////
////                        if (tempFloatValue != 15) {
////                            val temp = "$tempIntValue.$tempFloatValue".toDoubleOrNull()
////                                ?: 0.0 // Handle potential parsing errors
////                            // Use the calculated temperature (temp) as needed
////                            Log.d("BleService", "Calculated Temperature: $temp")
////                        }
////
////                        val time = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date(startTime))
////                        // Use the formatted time (time) as needed
////                        Log.d("BleService", "Formatted Time: $time")
////                    }
////
//                    }
//                }
//
//            })
//
//        YCBTClient.appRunModeStart(0x01, object : BleDataResponse {
//            override fun onDataResponse(code: Int, p1: Float, hashMap: HashMap<*, *>?) {
//                if (code == Constants.CODE.Code_OK) {
//                    if(hashMap != null){
//                        for ((key, value) in hashMap) {
//                            Log.d("BleService", "key(data): $key, value: $value")
//                        }
//                    } else {
//                        Log.w("BleService", "hashMap is null(BleDataResponse) - Thread: ${Thread.currentThread().name}, Time: ${System.currentTimeMillis()}")
//                    }
//                }
//            }
//        }, object : BleRealDataResponse {
//            override fun onRealDataResponse(p0: Int, hashMap: HashMap<*, *>?) {
//                if (hashMap != null) {
//                    for ((key, value) in hashMap) {
//                        Log.d("BleService", "key(real): $key, value: $value")
//                    }
//                }else {
//                    Log.d("BleService", "hashMap is null(BleRealDataResponse)")
//                }
//            }
//        })
//
//    }
//
////    private fun disconnectFromDevice(device: ScanDeviceBean) {
////
////        YCBTClient.disconnectBle()
////        Log.i("BleService", "Disconnected from device: ${device.deviceName}")
////
////    }
//
//    private suspend fun disconnectFromDevice(device: ScanDeviceBean) {
//        Log.d("BleService", "Attempting to disconnect from device ${device.deviceName}")
//        withContext(Dispatchers.IO) {
//            YCBTClient.disconnectBle()
//            var bleState: Int
//            do {
//                bleState = YCBTClient.connectState()
//                delay(100) // Small delay to avoid busy waiting
//            } while (bleState != Constants.BLEState.Disconnect)
//            Log.d("BleService", "Successfully disconnected from device ${device.deviceName}")
//        }
//    }
//
//
//    private fun rotateDeviceList() {
//        if (currentDevices.isNotEmpty()) {
//            val device = currentDevices.removeAt(0)
//            currentDevices.add(device)
//            scannedDevices.postValue(currentDevices.toList())
//        }
//    }
//
//    fun getScannedDevices(): LiveData<List<ScanDeviceBean>> = scannedDevices
//    fun getTemperatureData(): LiveData<String?> = temperatureData // Expose LiveData
//
//    override fun onDestroy() {
//        super.onDestroy()
//        Log.i("BleService", "Service destroyed")
//    }
//
//}
//
//@Composable
//fun RequestEnableBluetooth(ctx: Context,isBluetoothEnabled: Boolean) {
//    val enableBluetoothLauncher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.StartActivityForResult()
//    ) { result ->
//        if (result.resultCode == AppCompatActivity.RESULT_OK) {
//            // Bluetooth was successfully enabled
//            Toast.makeText(ctx, "Bluetooth enabled", Toast.LENGTH_SHORT).show()
//            // Proceed with Bluetooth operations
//        } else {
//            // User declined to enable Bluetooth
//            // Handle accordingly (e.g., show a message)
//            Toast.makeText(ctx, "Bluetooth not enabled", Toast.LENGTH_SHORT).show()
//        }
//    }
//
//    if (!isBluetoothEnabled) {
//        val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
//        enableBluetoothLauncher.launch(enableBtIntent)
//    }
//}

class BleService : Service() {

    private lateinit var sharedDevicesScreenViewModel: SharedDevicesScreenViewModel
    private var isEnabled = false
    private val scannedDevices = MutableLiveData<List<ScanDeviceBean>>()
    private val currentDevices = mutableListOf<ScanDeviceBean>()
    private var isProcessing = false
    private lateinit var bluetoothAdapter: BluetoothAdapter

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
        bluetoothAdapter = (applicationContext as MyApplication).bluetoothAdapter
        isEnabled = bluetoothAdapter.isEnabled

        val app = application as MyApplication
        sharedDevicesScreenViewModel = app.provideSharedDevicesScreenViewModel()
        Log.i("BleService", "Service created, Bluetooth is enabled: $isEnabled")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        Log.i("BleService", "Service started")
        return START_STICKY
    }

    fun startScan() {
        try {
            isEnabled = bluetoothAdapter.isEnabled
            if (isEnabled) {
                currentDevices.clear()

                val discoveredMacAddresses = mutableSetOf<String>()

                YCBTClient.startScanBle(object : BleScanResponse {
                    override fun onScanResponse(code: Int, scanDeviceBean: ScanDeviceBean?) {
                        try {
                            scanDeviceBean?.let {
                                if (!discoveredMacAddresses.contains(it.deviceMac)) {
                                    discoveredMacAddresses.add(it.deviceMac)
                                    currentDevices.add(it)
                                    Log.i("BleService", "currentDevices: ${currentDevices.size}")
                                    scannedDevices.postValue(currentDevices.toList())
                                    Log.i(
                                        "BleService",
                                        "Device found: ${it.deviceName}, Mac address: ${it.deviceMac}"
                                    )
                                }
                            }
                        } catch (e: Exception) {
                            Log.e("BleService", "Error processing scan response", e)
                        }
                    }
                }, 5)

                Log.i("BleService", "Scan started")
                CoroutineScope(Dispatchers.IO).launch {
                    delay(5000)
                    withContext(Dispatchers.Main) {
                        scannedDevices.value?.let { devices ->
                            sharedDevicesScreenViewModel.updateDevices(devices)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("BleService", "Error starting scan", e)
        }
    }

    fun initializePersonViewModels() {
        ViewModelManager.initializeViewModels(scannedDevices)
    }

    fun startProcessingDevices() {
        CoroutineScope(Dispatchers.IO).launch {
            processDevices()
        }
    }

    private suspend fun processDevices() {
        if (!isProcessing && currentDevices.isNotEmpty()) {
            isProcessing = true
            Log.i("BleService", "Starting to process devices")

            while (isProcessing && currentDevices.isNotEmpty()) {
                Log.i("BleService", "Processing devices")
                val device = currentDevices.first()
                Log.i("BleService", "Processing device: ${device.deviceName}")
                connectAndFetchData(device)
                rotateDeviceList()
            }

            isProcessing = false
            Log.i("BleService", "Processing stopped")
        } else {
            Log.i("BleService", "No devices to process or already processing")
        }
    }

    private suspend fun connectAndFetchData(device: ScanDeviceBean) {
        val connectionResult = connectToDevice(device)
        if (connectionResult) {
            delay(2000) // Increase delay before fetching data
            fetchDataFromDevice(device)
            delay(10000) // Allow time for data fetch
            disconnectFromDevice(device)
        } else {
            Log.e("BleService", "Skipping data fetch due to connection failure: ${device.deviceName}")
        }
    }

    private suspend fun connectToDevice(device: ScanDeviceBean): Boolean {
        return withContext(Dispatchers.IO) {
            var attempts = 0
            var success = false
            while (attempts < 3 && !success) {  // Retry up to 3 times
                attempts++
                val result = CompletableDeferred<Boolean>()
                YCBTClient.connectBle(device.deviceMac) { code ->
                    if (code == 0) {
                        Log.i("BleService", "Connected to device: ${device.deviceName} on attempt $attempts")
                        result.complete(true)
                        success = true
                    } else {
                        Log.e("BleService", "Failed to connect to device: ${device.deviceName} on attempt $attempts")
                        result.complete(false)
                    }
                }
                success = result.await()
                if (!success) {
                    delay(3000) // Wait for 3 seconds before retrying
                }
            }
            success
        }
    }

    private suspend fun disconnectFromDevice(device: ScanDeviceBean) {
        withContext(Dispatchers.IO) {
            YCBTClient.disconnectBle()
            var bleState: Int
            do {
                Log.d("BleService", "Attempting to disconnect from device ${device.deviceName}")
                bleState = YCBTClient.connectState()
                delay(100) // Small delay to avoid busy waiting
            } while (bleState != Constants.BLEState.Disconnect)
            Log.d("BleService", "Successfully disconnected from device ${device.deviceName}")
        }
    }

    fun fetchDataFromDevice(device: ScanDeviceBean) {
        YCBTClient.getRealTemp(object : BleDataResponse {
            override fun onDataResponse(i: Int, v: Float, hashMap: HashMap<*, *>?) {
                if (i == 0 && hashMap != null) {
                    val temp = hashMap["tempValue"] as? String
                    Log.i("BleService", "Fetched data from device: ${device.deviceName}, Temp: $temp")
                    Handler(Looper.getMainLooper()).post {
                        val viewModel = ViewModelManager.getViewModel(device.deviceMac)
                        viewModel?.updateTemperature(temp)
                    }
                } else {
                    Log.w("BleService", "hashMap is null or response code is not 0")
                }
            }
        })

        YCBTClient.settingHeartMonitor(0x01, 1, object : BleDataResponse {
            override fun onDataResponse(p0: Int, p1: Float, hashMap: HashMap<*, *>?) {
                hashMap?.let {
                    for ((key, value) in it) {
                        Log.i("BleService", "Key: $key, Value: $value")
                    }
                }
                Log.d("BleService", "Monitor set to 1 min for ${device.deviceMac}")
            }
        })

        YCBTClient.settingBloodOxygenModeMonitor(false, 1, object : BleDataResponse {
            override fun onDataResponse(p0: Int, p1: Float, hashMap: HashMap<*, *>?) {
                hashMap?.let {
                    for ((key, value) in it) {
                        Log.d("Ble", "BloodOxygen turned on")
                        Log.d("BleService", "Key: $key, Value: $value")
                    }
                }
            }
        })

        YCBTClient.healthHistoryData(Constants.DATATYPE.Health_HistoryBlood, object : BleDataResponse {
                override fun onDataResponse(i: Int, v: Float, hashMap: HashMap<*, *>?) {
                    hashMap?.let {
                        val lists = it["data"] as ArrayList<HashMap<*, *>>
                        var lastBloodDBP: Int? = null
                        var lastBloodSBP: Int? = null

                        for (map in lists) {
                            lastBloodDBP = map["bloodDBP"] as? Int
                            lastBloodSBP = map["bloodSBP"] as? Int
                        }
                        Handler(Looper.getMainLooper()).post {
                            val viewModel = ViewModelManager.getViewModel(device.deviceMac)
                            viewModel?.updateBloodPressure(lastBloodDBP, lastBloodSBP)
                        }
                    }
                }
            })

        YCBTClient.healthHistoryData(Constants.DATATYPE.Health_HistoryHeart, object : BleDataResponse {
                override fun onDataResponse(i: Int, v: Float, hashMap: HashMap<*, *>?) {
                    hashMap?.let {
                        val lists = it["data"] as ArrayList<HashMap<*, *>>
                        var heartValue: Int? = null
                        var counter = 0
                        var formattedStartTime: String? = null

                        for (map in lists) {
                            heartValue = map["heartValue"] as? Int
                            val startTime = map["heartStartTime"] as? Long
                            val dateFormat =
                                SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                            formattedStartTime = startTime?.let { dateFormat.format(Date(it)) }
                            counter++
                        }

                        Handler(Looper.getMainLooper()).post {
                            val viewModel = ViewModelManager.getViewModel(device.deviceMac)
                            viewModel?.updateHearthRate(heartValue, counter, formattedStartTime)
                        }
                    }
                }
            })

//        YCBTClient.getNowStep(object : BleDataResponse {
//            override fun onDataResponse(p0: Int, p1: Float, hashMap: HashMap<*, *>?) {
//                Log.d("BleService", "p0: $p0, p1: $p1")
//                hashMap?.let {
//                    for ((key, value) in it) {
//                        Log.d("BleService", "key(getNowStep): $key, value: $value")
//                    }
//                }
//            }
//        })

        YCBTClient.healthHistoryData(Constants.DATATYPE.Health_HistoryAll, object : BleDataResponse {
                override fun onDataResponse(i: Int, v: Float, hashMap: HashMap<*, *>?) {
                    hashMap?.let {
//                        for ((key, value) in it.entries) {
//                            Log.d("BleService", "Key(history sport): $key, Value: $value")
//                        }

                        val lists = hashMap["data"] as ArrayList<HashMap<*, *>>
//                        var heartValue : Int? = null
//                        var counter = 0
                        var formattedStartTime: String? = null
                        var SPO2: Int?
                        var respiratoryRate: Int?

                        for (map in lists) {

                            val StartTime = map["startTime"] as? Long
                            //val EndTime = map["sportEndTime"] as? Long
                            val dateFormat =
                                SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                            formattedStartTime = StartTime?.let { dateFormat.format(Date(it)) }
                            //val formattedEndTime = EndTime?.let { dateFormat.format(Date(it)) }

//                            val sportStep = map["sportStep"] as? Int
//                            val sportDistance = map["sportDistance"] as? Int
//                            val sportCalorie = map["sportCalorie"] as? Int

//                            Log.d("BleService", "sportStartTime: $formattedStartTime")
//                            Log.d("BleService", "sportEndTime: $formattedEndTime")
//                            Log.d("BleService", "sportStep: $sportStep")
//                            Log.d("BleService", "sportDistance: $sportDistance")
//                            Log.d("BleService", "sportCalorie: $sportCalorie")
                            //respitoryRateValue
                            SPO2 = map["OOValue"] as? Int?
                            respiratoryRate = map["respiratoryRateValue"] as? Int?
//                            Log.d("BleService", "SPO2: $SPO2")
//                            Log.d("BleService", "Respira: $respiratoryRate")

                            Handler(Looper.getMainLooper()).post {
                                val viewModel = ViewModelManager.getViewModel(device.deviceMac)
                                viewModel?.updateSPO2(SPO2)
                                viewModel?.updateRespiratoryRate(respiratoryRate)
                            }

                        }
                    }
                }
            })

        YCBTClient.appRunModeStart(0x02, object : BleDataResponse {
            override fun onDataResponse(code: Int, p1: Float, hashMap: HashMap<*, *>?) {
                if (code == Constants.CODE.Code_OK) {
                    if(hashMap != null) {
                        for ((key, value) in hashMap) {
                            Log.d("BleService", "key(data): $key, value: $value")
                        }
                    } else {
                        Log.w("BleService", "hashMap is null(BleDataResponse) - Thread: ${Thread.currentThread().name}, Time: ${System.currentTimeMillis()}")
                    }
                }
            }
        }, object : BleRealDataResponse {
            override fun onRealDataResponse(p0: Int, hashMap: HashMap<*, *>?) {
                if(hashMap != null) {
                    for ((key, value) in hashMap) {
                        Log.d("BleService", "key(real): $key, value: $value")
                    }
                } else {
                    Log.d("BleService", "hashMap is null(BleRealDataResponse)")
                }
            }
        })
    }

    private fun rotateDeviceList() {
        if (currentDevices.isNotEmpty()) {
            val device = currentDevices.removeAt(0)
            currentDevices.add(device)
            scannedDevices.postValue(currentDevices.toList())
        }
    }

    fun getScannedDevices(): LiveData<List<ScanDeviceBean>> = scannedDevices

    override fun onDestroy() {
        super.onDestroy()
        Log.i("BleService", "Service destroyed")
    }
}