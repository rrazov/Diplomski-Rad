package com.example.myfittracker.presentation.screens

import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.myfittracker.domain.permissions.PermissionHandl
import kotlinx.coroutines.delay


@Composable
fun WelcomeScreen(
    ctx: Context,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    var showCenteredText by remember { mutableStateOf(true) }
    var showDialog by remember { mutableStateOf(true) }
    var acknoledgedButtonPresseded by remember { mutableStateOf(false) }
    val permissionHandl = remember {
        PermissionHandl(ctx)
    }
    var shouldNavigateToScanDevice by remember { mutableStateOf(false) }


    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        permissionHandl.handlePermissionsResult(permissions)
    }


    if (showCenteredText) {
        CenteredText()

        LaunchedEffect(key1 = Unit) {
            delay(3000)
            showCenteredText = false
        }
    } else {
        if(showDialog) {
            StartupInfoDialog(
                onConfirmButtonClick = { //onConfirmButtonClick(showDialog)
                    showDialog = false
                    acknoledgedButtonPresseded = true

                }
            )
        }
    }
    if (acknoledgedButtonPresseded) {
        LaunchedEffect(Unit) {
            permissionHandl.checkAndRequestPermissions(permissionLauncher)
            shouldNavigateToScanDevice =
                permissionHandl.isLocationEnabled() && permissionHandl.bluetoothAdapter.isEnabled
        }
    }

    if(shouldNavigateToScanDevice){
        navController.navigate("scan_device")
    }


}


@Composable
fun StyledText() {
    Text(
        text = "MyFitTrackerApp!",
        style = TextStyle(
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Blue,
            textAlign = TextAlign.Center, // Center text horizontally
            fontFamily = FontFamily.Serif,
            textDecoration = TextDecoration.Underline
        )
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
            StyledText() // Call your styled text composable
        }
    }
}

@Composable
fun StartupInfoDialog(
    onConfirmButtonClick: () -> Unit
){
    AlertDialog(
        onDismissRequest = { /*TODO*/ },
        confirmButton = {
            Button(
                onClick = onConfirmButtonClick,
            ) {
                Text("Acknowledge")
            }
        },
        title = { Text("Information") },
        text = {
            Text(
                "You need to grant few permissions to use this app including Bluetooth and Location. " +
                        "Please grant them after you press the button 'Acknowledge'."
            )
        }

    )
}

//fun onConfirmButtonClick(
//    showDialog: Boolean
//) {
//    showDialog = false
//}



//@Composable
//fun RequestBlePermissions(ctx: Context){
//    val permissionHandler = PermissionsHandler.rememberPermissionsHandler()
//    val permissionGranted = remember {
//        mutableStateOf(permissionHandler.hasPermissions())
//    }
//
//    val permissionLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestMultiplePermissions()) {permissions ->
//            permissionGranted.value = permissions.values.all { it }
//            if (permissionGranted.value) {
//                //Toast.makeText(ctx,"All permission granted!", Toast.LENGTH_SHORT).show()
//                (ctx as? Activity)?.runOnUiThread{
//                    Toast.makeText(ctx,"All permission granted!", Toast.LENGTH_SHORT).show()
//                }
//            }
//        }
//
//    if (!permissionGranted.value) {
//        // Launch the permission request immediately if permissions are not granted
//        permissionLauncher.launch(permissionHandler.requiredPermissions)
//    } else {
//        (ctx as? Activity)?.runOnUiThread{
//            Toast.makeText(ctx,"Some permissions are missing!", Toast.LENGTH_SHORT).show()
//        }
//        permissionHandler.requestPermissions(permissionLauncher) //
//    }
//}



//@Preview
//@Composable
//fun WelcomeScreenPreview(){
//    WelcomeScreen(ctx = LocalContext.current)
//}