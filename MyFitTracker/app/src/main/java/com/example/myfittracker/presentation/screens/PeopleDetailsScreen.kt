package com.example.myfittracker.presentation.screens

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myfittracker.presentation.navigation.Screens
import com.example.myfittracker.presentation.viewmodel.SharedDevicesScreenViewModel

@Composable
fun PeopleDetailsScreen(
    macAddress: String,
    sharedViewModel: SharedDevicesScreenViewModel
) {
    Log.d("PeopleDetailsScreen", "Received macAddress: $macAddress")
    val deviceName = sharedViewModel.discoveredDevicesMap.value?.get(macAddress) ?: "Unknown Device"

    // ... Logic to fetch and display data for the device with the given macAddress ...

    Column (
        modifier = Modifier
            .padding(20.dp)
    ){
        Text(
            deviceName,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        // ... Display fetched data here ...
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun PeopleDetailsScreenPreview () {
    val previewViewModel: SharedDevicesScreenViewModel = viewModel()

    previewViewModel.updateDeviceName("1", "Roko")
    previewViewModel.updateDeviceName("2", "Ivan")

    PeopleDetailsScreen(macAddress = "1", sharedViewModel = previewViewModel)
}


