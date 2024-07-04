package com.example.myfittracker.presentation.screens

import android.bluetooth.BluetoothClass.Device
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import com.example.myfittracker.MyApplication
import com.example.myfittracker.domain.services.BleService
import com.yucheng.ycbtsdk.Bean.ScanDeviceBean
import com.yucheng.ycbtsdk.Constants
import com.yucheng.ycbtsdk.Response.BleConnectResponse
import com.yucheng.ycbtsdk.Response.BleDataResponse
import com.yucheng.ycbtsdk.YCBTClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.HashMap




@Composable
fun ScanDevicesScreen(
    ctx: Context,
    navController: NavController,
    modifier: Modifier = Modifier
) {


    val devices = remember {
        mutableStateListOf<ScanDeviceBean>()
    }
    val serviceIntent =  remember {
        Intent(ctx, BleService::class.java)
    }

    val lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current

    var isServiceBound by remember()
    {
        mutableStateOf(false)
    }

    var bleService: BleService? by remember {
        mutableStateOf<BleService?>(null)
    }

    var temperature by remember {
        mutableStateOf<String?>(null)
    }
    val lifecycleScope = rememberCoroutineScope()


    val serviceConnection = remember {
        object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                Log.i("ScanDevicesScreen", "ServiceConnection onServiceConnected called")
                val binder = service as BleService.LocalBinder
                bleService = binder.getService()
                isServiceBound = true;

                // Observe LiveData from BleService
                bleService?.getScannedDevices()?.observe(lifecycleOwner, Observer { newDevices ->
                    devices.clear()
                    devices.addAll(newDevices)
                    Log.i("ScanDevicesScreen", "New devices: ${newDevices.size}")
                })
                Log.i("ScanDevicesScreen", "Service connected")

                /// Dodano za test temperature
                bleService?.getTemperatureData()?.observe(lifecycleOwner, Observer { newTemperature ->
                        newTemperature?.let {
                            temperature = newTemperature
                            Log.i("ScanDevicesScreen", "New temperature: $newTemperature")
                        }
                    })
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                // Handle service disconnection
                isServiceBound = false;
                bleService = null;
                Log.i("ScanDevicesScreen", "Service disconnected")
            }
        }
    }
    LaunchedEffect(key1 = Unit)
    {
        lifecycleScope.launch {
            while (true) {
                getTemperature { newTemperature ->
                    temperature = newTemperature
                }
                delay(5000)
            }
        }
    }


    DisposableEffect(Unit)
    {
        Log.i("ScanDevicesScreen", "Binding service")
        ctx.bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE)
        onDispose()
        {
            if (isServiceBound) {
                ctx.unbindService(serviceConnection)
            }
        }
    }


    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (temperature != null) {
            Text(text = "Temperature: $temperature°C")
        }
        else{
            Text(text = "Loading temperature")
        }
        Button(onClick = {
            ctx.startService(serviceIntent)
            Log.i("ScanDevicesScreen", "Start service button clicked")
        }) {
            Text(text = "Scan Devices")
        }
        Button(onClick = {
            ctx.stopService(serviceIntent)
            Log.i("ScanDevicesScreen", "Stop service button clicked")
        }) {
            Text(text = "Stop Scan")

        }
        Button(onClick = { TODO() }) {
            Text("Get temperature")
        }
        LazyColumn {
            items(devices) { device ->
                DeviceItem(device = device, onClick = { connectToDevice(device, ctx) })
            }
        }
    }
}



@Composable
fun DeviceItem(device: ScanDeviceBean, onClick: () -> Unit){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp)

    ) {
        Column {
            Text(text = "Name: ${device.deviceName}")
            Text(text = "MAC: ${device.deviceMac}")
        }
    }
}

fun connectToDevice(device: ScanDeviceBean, ctx: Context) {
    Log.i("ScanDevicesScreen", "Connecting to device: ${device.deviceName}")
    YCBTClient.connectBle(device.deviceMac, object : BleConnectResponse{
        override fun onConnectResponse(code: Int) {
            if(code == Constants.CODE.Code_OK){
                CoroutineScope(Dispatchers.Main).launch {
                    delay(1000)
                    Toast.makeText(
                        ctx,
                        "Connected to device: ${device.deviceName}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else if(code == Constants.CODE.Code_Failed){
                CoroutineScope(Dispatchers.Main).launch {
                    delay(1000)
                    Toast.makeText(
                        ctx,
                        "Failed to connect to device: ${device.deviceName}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else{
                CoroutineScope(Dispatchers.Main).launch {
                    delay(1000)
                    Toast.makeText(
                        ctx,
                        "Unknown error occurred while connecting to device: ${device.deviceName}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    })
    Toast.makeText(ctx, "Connecting to device: ${device.deviceName}", Toast.LENGTH_SHORT).show()

}

fun getTemperature(onTemperatureReceived: (String?) -> Unit){
    YCBTClient.getRealTemp(object : BleDataResponse{
        override fun onDataResponse(p0: Int, p1: Float, p2: HashMap<*, *>?) {
            if(p0 == 0){
                val temp = p2?.get("tempValue") as? String
                Log.i("getTemperature", "Temperature: $temp")
                CoroutineScope(Dispatchers.Main).launch {
                    /// Switch to the main thread
                    onTemperatureReceived(temp)
                }
            }
        }
    })
}
