package com.example.myfittracker.presentation.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.myfittracker.presentation.viewmodel.SharedDevicesScreenViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListOfPeopleScreen(
    sharedViewModel: SharedDevicesScreenViewModel,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val devicesMap by sharedViewModel.discoveredDevicesMap
        .observeAsState(mutableMapOf())

    Log.d("ListOfPeopleScreen", "devicesMap: $devicesMap")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(
                        "Clients",
                        color = Color.White
                    ) },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = Color(0xFF007BFF) // Match the blue gradient
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = modifier.fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF007BFF),
                            Color(0xFF673AB7)
                        )
                    )
                )
                .padding(innerPadding)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 16.dp, end = 16.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(devicesMap.entries.toList()) { entry ->
                    PeopleListItem(
                        name = entry.value,
                        macAddress = entry.key,
                        modifier = Modifier.fillMaxWidth()
                    ) { macAddress ->
                        navController.navigate("fitness_dashboard_screen/$macAddress")
                    }
                }
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
) {
    Card(
        modifier = modifier
            .clickable { onClick(macAddress) }
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp), // Increased elevation
        shape = RoundedCornerShape(12.dp), // More rounded corners
        colors = CardDefaults.cardColors(containerColor = Color.White) // White card
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.titleMedium,
                color = Color.Black // Black text on white card
            )
            Spacer(Modifier.weight(1f))
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Navigate to details",
                tint = Color.Gray // Adjust icon color as needed
            )
        }
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