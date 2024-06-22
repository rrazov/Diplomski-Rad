package com.example.myfittracker.domain.permissions

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AlertDialog
import androidx.compose.runtime.rememberCoroutineScope
import androidx.core.content.ContextCompat
import com.example.myfittracker.MyApplication
import kotlinx.coroutines.launch

class PermissionHandl(private val context: Context){
    val bluetoothAdapter = (context.applicationContext as MyApplication).bluetoothAdapter
    var isBluetootEnabled = bluetoothAdapter.isEnabled

    companion object {
        private val permissionsBelowQ = arrayOf(
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        private val permissionsAboveQ = arrayOf(
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    }

    fun checkAndRequestPermissions(permissionLauncher: ActivityResultLauncher<Array<String>>) {
        val permissions =
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                permissionsAboveQ
            } else {
                permissionsBelowQ
            }

        val permissionsToRequest = permissions.filter {
            ContextCompat.checkSelfPermission(context, it) != PackageManager.PERMISSION_GRANTED
        }

        if (permissionsToRequest.isNotEmpty()) {
            permissionLauncher.launch(permissionsToRequest.toTypedArray())
        } else {
            enableBluetoothAndLocation()
        }
    }

    fun handlePermissionsResult(grantResults: Map<String, Boolean>) {
        if (grantResults.values.all { it }) {
            enableBluetoothAndLocation()
        } else {
            Toast.makeText(context, "Permissions required for BLE functionality", Toast.LENGTH_SHORT).show()
            // Optionally re-request permissions or handle the case when permissions are not granted
        }
    }

    private fun enableBluetoothAndLocation() {
        if (!isBluetootEnabled) {
            requestEnableBluetooth()
        }
        if (!isLocationEnabled()) {
            requestEnableLocation()
        }
    }

    private fun requestEnableBluetooth() {
        val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        try {
            context.startActivity(enableBtIntent)
        } catch (ex: SecurityException) {
            Toast.makeText(context, "$ex", Toast.LENGTH_SHORT).show()
        }
    }

    fun isLocationEnabled(): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    private fun requestEnableLocation() {

//        AlertDialog.Builder(context)
//            .setIcon(android.R.drawable.ic_dialog_alert)
//            .setTitle("Enable Location")
//            .setMessage("Location services are required for BLE functionality. Please enable location services.")
//            .setNegativeButton("Cancel", null)
//            .setPositiveButton("Enable") { _, _ ->
//                coroutineScope.launch {
//                    Toast.makeText(context, "Location services enabled", Toast.LENGTH_SHORT).show()
//
//                    context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
//                }
//            }
//            .show()

            context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
        //}
    }
}