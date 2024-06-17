package com.example.myfittracker.permissions

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

class PermissionsHandler(private val context: Context) {
    val requiredPermissions = arrayOf(
        Manifest.permission.BLUETOOTH,
        Manifest.permission.BLUETOOTH_ADMIN,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    fun hasPermissions(): Boolean {
        return requiredPermissions.all { permission ->
            ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
        }
    }

    fun requestPermissions(launcher: ActivityResultLauncher<Array<String>>) {
        launcher.launch(requiredPermissions)
    }

    companion object {
        @Composable
        fun rememberPermissionsHandler(): PermissionsHandler {
            val context = LocalContext.current
            return remember { PermissionsHandler(context) }
        }
    }
}