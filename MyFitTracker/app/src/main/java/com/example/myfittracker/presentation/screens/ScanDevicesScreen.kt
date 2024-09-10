package com.example.myfittracker.presentation.screens

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.myfittracker.domain.services.BleService
import com.example.myfittracker.presentation.viewmodel.SharedDevicesScreenViewModel
import com.yucheng.ycbtsdk.Bean.ScanDeviceBean
import com.yucheng.ycbtsdk.Constants
import com.yucheng.ycbtsdk.Response.BleConnectResponse
import com.yucheng.ycbtsdk.Response.BleDataResponse
import com.yucheng.ycbtsdk.YCBTClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.HashMap




//@Composable
//fun ScanDevicesScreen(
//    viewModel: SharedDevicesScreenViewModel = viewModel(),
//    ctx: Context,
//    navController: NavController,
//    bleService: BleService,
//    modifier: Modifier = Modifier,
//) {
//    val isScanned by viewModel.isScanned.observeAsState(false)
//    var isConfirmed by remember {
//        mutableStateOf(false)
//    }
//    var recompositionTrigger by remember { mutableStateOf(0) }
//    val discoveredDevicesMap by viewModel
//        .discoveredDevicesMap
//        .observeAsState(mutableMapOf())
//
//    val devices by viewModel
//        .devices
//        .observeAsState(emptyList())
//
//    LaunchedEffect(key1 = Unit){
//        snapshotFlow { discoveredDevicesMap }
//            .collect { updatedMap ->
//
//                withContext(Dispatchers.Main){
//                    Log.d("ScanDevicesScreen", "Discovered devices map updated: $updatedMap")
//                    recompositionTrigger++
//                }
//            }
//    }
//
//    Column(
//        modifier = Modifier.fillMaxSize(),
//        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.Center
//    ) {
//
//        Button(onClick = {
//            bleService.startScan()
//            viewModel.startScanning()
//            Toast.makeText(ctx, "Scanning nearby devices", Toast.LENGTH_SHORT).show()
//            Log.i("ScanDevicesScreen", "Start service button clicked")
//        }) {
//            Text(text = "Scan Devices")
//        }
//
//        LazyColumn {
//            items(devices, key = { it.deviceMac + recompositionTrigger }) { device ->
//                val displayName = discoveredDevicesMap[device.deviceMac] ?: device.deviceMac
//                DeviceItem(
//                    device = device,
//                    onNameEntered = { name ->
//                        viewModel.updateDeviceName(device.deviceMac, name)
//                        Log.d(
//                            "ScanDevicesScreen",
//                            "Device name updated for ${device.deviceMac}: $name"
//                        )
//                        recompositionTrigger++
//                    }
//                ) {
//                    Column {
//                        //Text(text = "Name: $device.deviceName")
//                        Text(text = "ID: $displayName")
//                    }
//                }
//            }
//        }
//        if(isScanned && !isConfirmed) {
//            Button(onClick = {
//                isConfirmed = true
//                bleService.initializePersonViewModels()
//            }) {
//                Text(text = "Confirm")
//            }
//        }
//        if(isConfirmed) {
//            Button(onClick = {
//                bleService.startProcessingDevices()
//                Toast.makeText(ctx, "Connecting to the devices", Toast.LENGTH_SHORT).show()
//
//                CoroutineScope(Dispatchers.Main).launch {
//                    delay(2000)
//                    navController.navigate("list_of_people")
//                }
//                //isConfirmed = false
//            }) {
//                Text(text = "Connect")
//            }
//        }
//    }
//}
//
//
//@Composable
//fun DeviceItem(
//    device: ScanDeviceBean,
//    onNameEntered: (String) -> Unit,
//    content: @Composable () -> Unit) {
//    var showDialog by remember {
//        mutableStateOf(false)
//    }
//    var enteredName by remember()
//    {
//        mutableStateOf("")
//    }
//    Row(
//        modifier = Modifier
//            .fillMaxWidth()
//            .clickable { showDialog = true }
//            .padding(16.dp)
//
//    ) {
//        content()
//    }
//
//    if (showDialog) {
//        Log.d("ScanDevicesScreen", "Showing dialog")
//        AlertDialog(
//            onDismissRequest = { showDialog = false },
//            confirmButton = {
//                Button(onClick = {
//                    Log.d("DeviceItem", "Calling onNameEntered with name: $enteredName")
//                    onNameEntered(enteredName)
//                    showDialog = false
//
//                }) {
//                    Text("Save")
//                }
//            },
//            title = {Text("Enter Device Name")},
//            text = {
//                OutlinedTextField(
//                    value = enteredName,
//                    onValueChange = {
//                        enteredName = it
//                        Log.d("ScanDevicesScreen", "Entered name after: $enteredName")},
//                    label = { Text("Device Name") }
//                )
//            },
//            dismissButton = {
//                Button(onClick = { showDialog = false }) {
//                    Text("Cancel")
//                }
//            }
//        )
//    }
//}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanDevicesScreen(
    viewModel: SharedDevicesScreenViewModel = viewModel(),
    ctx: Context,
    navController: NavController,
    bleService: BleService,
    modifier: Modifier = Modifier,
) {
    val isScanned by viewModel.isScanned.observeAsState(false)
    var isConfirmed by remember { mutableStateOf(false) }
    var recompositionTrigger by remember { mutableStateOf(0) }
    val discoveredDevicesMap by viewModel
        .discoveredDevicesMap
        .observeAsState(mutableMapOf())

    val devices by viewModel
        .devices
        .observeAsState(emptyList())

    LaunchedEffect(key1 = Unit) {
        snapshotFlow { discoveredDevicesMap }
            .collect { updatedMap ->
                withContext(Dispatchers.Main) {
                    Log.d("ScanDevicesScreen", "Discovered devices map updated: $updatedMap")
                    recompositionTrigger++
                }
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Scan Devices",
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                modifier = Modifier.shadow(elevation = 4.dp),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF007BFF), // Background color
                    titleContentColor = Color.White, // Title text color
                    actionIconContentColor = Color.White // Icons color if any
                )
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF007BFF),
                                Color(0xFF673AB7)
                            )
                        )
                    )
                    .padding(paddingValues)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Button(
                    onClick = {
                        bleService.startScan()
                        viewModel.startScanning()
                        Toast.makeText(ctx, "Scanning nearby devices", Toast.LENGTH_SHORT).show()
                        Log.i("ScanDevicesScreen", "Start service button clicked")
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFF66BB6A), // Start with a lighter green
                                    Color(0xFF43A047)
                                )
                            ),
                            shape = RoundedCornerShape(50)
                        )


                ) {
                    Text(text = "Scan Devices", style = MaterialTheme.typography.bodySmall)
                }

                LazyColumn(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(devices, key = { it.deviceMac + recompositionTrigger }) { device ->
                        val displayName = discoveredDevicesMap[device.deviceMac] ?: device.deviceMac
                        DeviceItem(
                            device = device,
                            onNameEntered = { name ->
                                viewModel.updateDeviceName(device.deviceMac, name)
                                Log.d(
                                    "ScanDevicesScreen",
                                    "Device name updated for ${device.deviceMac}: $name"
                                )
                                recompositionTrigger++
                            }
                        ) {
                            Column {
                                Text(
                                    text = displayName,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = Color(0xFF3A3A3A)
                                )
                            }
                        }
                    }
                }

                if (isScanned && !isConfirmed) {
                    Button(
                        onClick = {
                            isConfirmed = true
                            bleService.initializePersonViewModels()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(50),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp)
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        Color(0xFF66BB6A), // Start with a lighter green
                                        Color(0xFF43A047)
                                    )
                                ),
                                shape = RoundedCornerShape(50)
                            )
                    ) {
                        Text(text = "Confirm", style = MaterialTheme.typography.bodySmall)
                    }
                }

                if (isConfirmed) {
                    Button(
                        onClick = {
                            bleService.startProcessingDevices()
                            Toast.makeText(ctx, "Connecting to the devices", Toast.LENGTH_SHORT).show()

                            CoroutineScope(Dispatchers.Main).launch {
                                delay(2000)
                                navController.navigate("list_of_people")
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(50),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp)
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        Color(0xFF66BB6A), // Start with a lighter green
                                        Color(0xFF43A047)
                                    )
                                ),
                                shape = RoundedCornerShape(50)
                            )
                    ) {
                        Text(text = "Connect", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    )
}


@Composable
fun DeviceItem(
    device: ScanDeviceBean,
    onNameEntered: (String) -> Unit,
    content: @Composable () -> Unit
) {
    var showDialog by remember {
        mutableStateOf(false)
    }
    var enteredName by remember { mutableStateOf("") }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { showDialog = true },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            content()
        }
    }

    if (showDialog) {
        Log.d("ScanDevicesScreen", "Showing dialog")
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                Button(onClick = {
                    Log.d("DeviceItem", "Calling onNameEntered with name: $enteredName")
                    onNameEntered(enteredName)
                    showDialog = false

                }) {
                    Text("Save")
                }
            },
            title = { Text("Enter Device Name") },
            text = {
                OutlinedTextField(
                    value = enteredName,
                    onValueChange = {
                        enteredName = it
                        Log.d("ScanDevicesScreen", "Entered name after: $enteredName")
                    },
                    label = { Text("Device Name") }
                )
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}


@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun ScanDevicesScreenPreview() {
    val previewViewModel = SharedDevicesScreenViewModel()
    ScanDevicesScreen(
        viewModel = previewViewModel,
        ctx = LocalContext.current,
        navController = rememberNavController(),
        bleService = BleService()
    )
}

