package com.example.myfittracker.presentation.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.tooling.preview.Preview
import com.example.myfittracker.domain.services.BleService
import com.example.myfittracker.presentation.viewmodel.PersonViewModel
import com.example.myfittracker.presentation.viewmodel.TestPersonViewModel
import com.example.myfittracker.presentation.viewmodel.ViewModelManager

@Composable
fun PersonDetailsScreen(
    macAddress: String,
) {
    val viewModel = ViewModelManager.getViewModel(macAddress)

    Column {
        //Text("Name: ${viewModel.name}")
        Text("Temperature: ${viewModel?.temperature?.observeAsState()?.value ?: "Loading..."}")
        Text("Heart Rate: ${viewModel?.heartRate?.observeAsState()?.value ?: "Loading..."}")
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun PersonDetailsScreenPreview() {
//    val previewViewModel: SharedDevicesScreenViewModel = viewModel()
//
//    previewViewModel.updateDeviceName("1", "Roko")
//    previewViewModel.updateDeviceName("2", "Ivan")
    val bleService = BleService()
    val previewViewModel = TestPersonViewModel("1", "Roko", bleService)
    previewViewModel.setTemperature("37.2")
    PersonDetailsScreen(macAddress = "dr2334d3")

}


