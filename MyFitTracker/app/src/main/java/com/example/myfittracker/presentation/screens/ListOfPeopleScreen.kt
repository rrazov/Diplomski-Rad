package com.example.myfittracker.presentation.screens

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.myfittracker.presentation.viewmodel.SharedDevicesScreenViewModel

@Composable
fun ListOfPeopleScreen(
    sharedViewModel: SharedDevicesScreenViewModel,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val devicesMap by sharedViewModel
        .discoveredDevicesMap
        .observeAsState(mutableMapOf())

    Log.d("ListOfPeopleScreen", "devicesMap: $devicesMap")

    LazyColumn(modifier = modifier.fillMaxSize()) {
        items(devicesMap.entries.toList()) { entry ->
            PeopleListItem(
                name = entry.value,
                macAddress = entry.key,
                modifier = Modifier.fillMaxWidth()
            ) { macAddress ->
                navController.navigate("people_details_screen/$macAddress")
            }

        }
    }
}

@Composable
fun PeopleListItem(
    name: String,
    macAddress: String,
    modifier: Modifier = Modifier,
    onClick: (String) -> Unit
)
{
    Row(
        modifier = modifier
            .clickable { onClick(macAddress) }
            .padding(16.dp)
    ){
        Text(
            text = name,
            fontSize = 20.sp
        )
    }
}


@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun ListOfPeopleScreenPreview() {

    val previewViewModel = SharedDevicesScreenViewModel()
    previewViewModel.updateDeviceName("1", "Roko")
    previewViewModel.updateDeviceName("2", "Ivan")

    val navController = rememberNavController()
    ListOfPeopleScreen(previewViewModel, navController)


}