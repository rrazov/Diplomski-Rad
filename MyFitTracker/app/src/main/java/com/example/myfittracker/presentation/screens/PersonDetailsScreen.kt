package com.example.myfittracker.presentation.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myfittracker.domain.services.BleService
import com.example.myfittracker.presentation.viewmodel.PersonViewModel
import com.example.myfittracker.presentation.viewmodel.SharedDevicesScreenViewModel
import com.example.myfittracker.presentation.viewmodel.TestPersonViewModel
import com.example.myfittracker.presentation.viewmodel.ViewModelManager

@Composable
fun PersonDetailsScreen(
    macAddress: String,
    sharedDevicesScreenViewModel: SharedDevicesScreenViewModel,
    innerPaddingValues: PaddingValues
) {
    val viewModel = ViewModelManager.getViewModel(macAddress)

    val deviceName = sharedDevicesScreenViewModel
        .discoveredDevicesMap
        .observeAsState().value?.get(macAddress) ?: ""

    Column(modifier = Modifier
            .padding(innerPaddingValues)
    ) {
        Text(
            text = deviceName,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            modifier = Modifier
                .padding(bottom = 10.dp)
        )
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

    val sharedDevicesScreenViewModel = SharedDevicesScreenViewModel()
    sharedDevicesScreenViewModel.updateDeviceName("adse23e", "Roko")
    val bleService = BleService()
    val previewViewModel = TestPersonViewModel("1", "Roko", bleService)
    previewViewModel.setTemperature("37.2")
    PersonDetailsScreen(macAddress = "adse23e",
        sharedDevicesScreenViewModel = sharedDevicesScreenViewModel,
        innerPaddingValues = PaddingValues()
    )

}


