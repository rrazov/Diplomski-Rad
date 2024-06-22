package com.example.myfittracker.presentation.screens

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.example.myfittracker.MyApplication
import com.example.myfittracker.domain.services.BleService

@Composable
fun ScanDevicesScreen(
    ctx: Context,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val applicationContext = LocalContext.current

    Button(onClick = {
        try {
            val intent = Intent(applicationContext, BleService::class.java)
            applicationContext.startService(intent)
            Log.i("ScanCallback", "Service is started")
        }catch (e: Exception){
            Log.i("ScanCallback", "Exception: $e")
        }
    }) {
        Text(text = "Scan Devices")
    }
}