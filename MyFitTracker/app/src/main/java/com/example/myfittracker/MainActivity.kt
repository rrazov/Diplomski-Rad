package com.example.myfittracker

import android.os.Bundle
import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.location.LocationManagerCompat
import com.example.myfittracker.presentation.navigation.AppNavigation
import com.example.myfittracker.ui.theme.MyFitTrackerTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private var isBluetoothEnabled by mutableStateOf(false)

    private val permissionToRequest = arrayOf(
        Manifest.permission.BLUETOOTH,
        Manifest.permission.BLUETOOTH_ADMIN,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
        // Add more permissions as needed for different android versions
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val bluetoothAdapter = (applicationContext as MyApplication).bluetoothAdapter
        isBluetoothEnabled = bluetoothAdapter.isEnabled

        enableEdgeToEdge()
        setContent {
            MyFitTrackerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AppNavigation()

//                    val permissionHandler = PermissionHandler()
//                    val dialogQueue = permissionHandler.visiblePermissionDialogQueue
//
//                    RequestEnableLocation {
//                        Toast.makeText(this, "Location is enabled", Toast.LENGTH_SHORT).show()
//                    }
//
//                    // 1. Initialize the Bluetooth enablement launcher FIRST
//                    val enableBluetoothLauncher = rememberLauncherForActivityResult(
//                        contract = ActivityResultContracts.StartActivityForResult()) {result: ActivityResult ->
//                        if (result.resultCode == Activity.RESULT_OK) {
//                            isBluetoothEnabled = true
//                        }else{
//                            Toast.makeText(this, "Bluetooth is not enabled. You need to enable it!!", Toast.LENGTH_SHORT).show()
//                        }
//                    }
//
//                    // 2. THEN call RequestEnableBluetooth
//                    RequestEnableBluetooth(isBluetoothEnabled, enableBluetoothLauncher)
//
//                    //3. FINALLY, launch the multiple permissions request
//                    val multiplePermissionsResultLauncher = rememberLauncherForActivityResult(
//                        contract = ActivityResultContracts.RequestMultiplePermissions(),
//                        onResult = { perms ->
//                            permissionToRequest.forEach { permission ->
//                                permissionHandler.onPermissionResult(
//                                    permission = permission,
//                                    isGranted = perms[permission] == true
//                                )
//                            }
//                        }
//                    )
//                    LaunchedEffect(key1 = Unit) {
//                        delay(100)
//                        multiplePermissionsResultLauncher.launch(permissionToRequest)
//                    }
//
//
//
//
//                    Button(
//                        onClick = {
//                            try {
//                                val intent = Intent(this, BleService::class.java)
//                                this.startService(intent)
//                                Log.i("ScanCallback", "Service is started")
//                            }catch (e: Exception){
//                                Log.i("ScanCallback", "Exception: $e")
//                            }
//
//
//                        },
//                        modifier = Modifier.padding(innerPadding)) {
//                        Text(text = "Click me")
//
//                    }
//
//                    dialogQueue
//                        .reversed()
//                        .forEach { permission ->
//                            PermissionDialog(
//                                permissionTextProvider = when (permission) {
//                                    Manifest.permission.BLUETOOTH -> BluetoothPermissionTextProvider()
//                                    Manifest.permission.BLUETOOTH_ADMIN -> BluetoothAdminPermissionTextProvider()
//                                    Manifest.permission.ACCESS_FINE_LOCATION -> LocationPermissionTextProvider()
//                                    else -> return@forEach
//                                },
//                                isPermanentlyDeclined = !shouldShowRequestPermissionRationale(permission),
//                                onDismissClick = { permissionHandler.dismissDialog() }, // Call the function within the lambda
//                                onOkClick = {
//                                    permissionHandler.dismissDialog()
//                                    multiplePermissionsResultLauncher.launch(arrayOf(permission))
//                                },
//                                onGoToSettingsClick = ::openAppSettings
//
//                            )
//                        }
                }
            }
        }
    }
}

@Composable
fun RequestEnableBluetooth(
    isBluetoothEnabled: Boolean,
    enableBluetoothLauncher: ActivityResultLauncher<Intent>) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(key1 = isBluetoothEnabled) {
        if (!isBluetoothEnabled) {
            coroutineScope.launch {
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                enableBluetoothLauncher.launch(enableBtIntent)
            }
        }
    }
}


// ... (Your existing code)

@Composable
fun RequestEnableLocation(onLocationEnabled: () -> Unit) {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }

    // Check if location is enabled
    if (!context.isLocationEnabled()) {
        showDialog = true
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Enable Location") },
            text = { Text("Location services are required for this app to function properly.") },
            confirmButton = {
                Button(onClick = {
                    showDialog = false
                    // Open location settings
                    context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                }) {
                    Text("Open Settings")
                }
            },
            dismissButton = {
                Button(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Callback when location is enabled (you'll need to handle this in your main activity)
    if (context.isLocationEnabled()) {
        onLocationEnabled()
    }
}

// Extension function to check location status
fun Context.isLocationEnabled(): Boolean {
    val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
    return LocationManagerCompat.isLocationEnabled(locationManager)
}






fun Activity.openAppSettings(){
    Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", packageName, null)
    ).also(::startActivity)

}

