package com.example.myfittracker.presentation.navigation

import android.app.Activity
import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.myfittracker.MainActivity
import com.example.myfittracker.permissions.PermissionsHandler

@Composable
fun WelcomeScreen (ctx: Context,
                   modifier: Modifier = Modifier
) {
    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
    {
        //RequestBlePermissions(ctx = ctx)
        CenteredText()

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
fun RequestBlePermissions(ctx: Context){
    val permissionHandler = PermissionsHandler.rememberPermissionsHandler()
    val permissionGranted = remember {
        mutableStateOf(permissionHandler.hasPermissions())
    }

    val permissionLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestMultiplePermissions()) {permissions ->
        permissionGranted.value = permissions.values.all { it }
        if (permissionGranted.value)
        {
            //Toast.makeText(ctx,"All permission granted!", Toast.LENGTH_SHORT).show()
            (ctx as? Activity)?.runOnUiThread{
                Toast.makeText(ctx,"All permission granted!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    if (!permissionGranted.value) {
        // Launch the permission request immediately if permissions are not granted
        permissionLauncher.launch(permissionHandler.requiredPermissions)
    } else {
        (ctx as? Activity)?.runOnUiThread{
            Toast.makeText(ctx,"Some permissions are missing!", Toast.LENGTH_SHORT).show()
        }
            permissionHandler.requestPermissions(permissionLauncher) //
    }
}



//@Preview
//@Composable
//fun WelcomeScreenPreview(){
//    WelcomeScreen(ctx = LocalContext.current)
//}