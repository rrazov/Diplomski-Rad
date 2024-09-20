package com.example.myfittracker.presentation.screens

import android.content.Context
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.myfittracker.domain.permissions.PermissionHandle
import kotlinx.coroutines.delay
import com.example.myfittracker.R


@Composable
fun WelcomeScreen(
    ctx: Context,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    var showCenteredText by remember { mutableStateOf(true) }
    var showDialog by remember { mutableStateOf(true) }
    var acknowledgedButtonPressed by remember { mutableStateOf(false) }
    val permissionHandle = remember {
        PermissionHandle(ctx)
    }
    var shouldNavigateToScanDevice by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        permissions.forEach { (permission, isGranted) ->
            Log.d("WelcomeScreen", "Permission $permission granted: $isGranted")
        }

        permissionHandle.handlePermissionsResult(permissions)

    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF2196F3), // Lighter blue at the top
                        Color(0xFF1E88E5), // Mid blue
                        Color(0xFF1976D2)  // Darker blue at the bottom
                    )
                )
            )
    ) {
        if (showCenteredText) {
            CenteredText()

            LaunchedEffect(key1 = Unit) {
                delay(3000)
                showCenteredText = false
            }
        } else {
            if (showDialog) {
                StartupInfoDialog(
                    onConfirmButtonClick = {
                        showDialog = false
                        acknowledgedButtonPressed = true
                    }
                )
            }
        }
    }

//    if (acknowledgedButtonPressed) {
//        LaunchedEffect(Unit) {
//            Log.d("WelcomeScreen", "Acknowledged button pressed, checking permissions")
//            permissionHandle.checkAndRequestPermissions(permissionLauncher)
////            shouldNavigateToScanDevice =
////                permissionHandle.isLocationEnabled() && permissionHandle.bluetoothAdapter.isEnabled
//        }
//    }
    // Check and request permissions when the user acknowledges
    if (acknowledgedButtonPressed) {
        LaunchedEffect(Unit) {
            Log.d("WelcomeScreen", "Acknowledged button pressed, checking permissions")
            permissionHandle.checkAndRequestPermissions(permissionLauncher)
        }
    }

    LaunchedEffect(Unit) {
        while (!shouldNavigateToScanDevice) {
            // Continuously check Bluetooth and Location statuses
            val bluetoothEnabled = permissionHandle.bluetoothAdapter.isEnabled
            val locationEnabled = permissionHandle.isLocationEnabled()

            Log.d(
                "WelcomeScreen",
                "Bluetooth enabled: $bluetoothEnabled, Location enabled: $locationEnabled"
            )

            if (bluetoothEnabled && locationEnabled && acknowledgedButtonPressed) {
                // Once both are enabled, trigger navigation
                shouldNavigateToScanDevice = true
            }

            // Wait for 1 second before checking again
            delay(1000)
        }
    }


    LaunchedEffect(shouldNavigateToScanDevice) {
        if (shouldNavigateToScanDevice) {
            Log.d("WelcomeScreen", "Navigating to Scan Device Screen")
            navController.navigate("scan_device")
        }
    }
}

@Composable
fun StyledText() {
    Text(
        text = "Welcome to MyFitTracker!",
        style = TextStyle(
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center,
            fontFamily = FontFamily.SansSerif,
            shadow = Shadow(
                color = Color.Black.copy(alpha = 0.4f),
                offset = Offset(2f, 2f),
                blurRadius = 4f
            )
        ),
        modifier = Modifier.padding(horizontal = 16.dp)
    )
}

@Composable
fun CenteredText() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(id = R.drawable.fitness_tracker_24dp_ff6a00_fill1_wght400_grad0_opsz24),
                contentDescription = "Fitness Tracker",
                modifier = Modifier
                    .size(100.dp)
                    .padding(bottom = 15.dp)
            )
            StyledText()
        }
    }
}

@Composable
fun StartupInfoDialog(
    onConfirmButtonClick: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = { /*TODO*/ },
        confirmButton = {
            Button(
                onClick = onConfirmButtonClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1976D2),
                    contentColor = Color.White
                ),
                modifier = Modifier.padding(8.dp)
            ) {
                Text("Acknowledge")
            }
        },
        title = {
            Text(
                text = "Important Information",
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color(0xFF1E88E5)
                )
            )
        },
        text = {
            Text(
                "To use this app, you need to grant the required permissions including Bluetooth and Location. " +
                        "Please grant them after pressing 'Acknowledge'.",
                style = TextStyle(fontSize = 16.sp, color = Color.Gray)
            )
        },
        shape = RoundedCornerShape(16.dp),
        containerColor = Color.White
    )
}



@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun WelcomeScreenPreview() {
    // Mock NavController for preview purposes
    val mockNavController = rememberNavController()

    WelcomeScreen(
        ctx = LocalContext.current,
        navController = mockNavController
    )
}

//    showDialog: Boolean
//) {
//    showDialog = false
//}




//@Preview
//@Composable
//fun WelcomeScreenPreview(){
//    WelcomeScreen(ctx = LocalContext.current)
//}