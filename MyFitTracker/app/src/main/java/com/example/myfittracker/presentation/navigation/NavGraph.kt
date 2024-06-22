package com.example.myfittracker.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myfittracker.presentation.screens.ScanDevicesScreen
import com.example.myfittracker.presentation.screens.WelcomeScreen

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
)
{
    NavHost(navController = navController, startDestination = Screens.Welcome.route) {
        composable(Screens.Welcome.route) {
            WelcomeScreen(ctx = LocalContext.current, navController)
        }
        composable(Screens.ScanDevice.route) {
            ScanDevicesScreen(ctx = LocalContext.current, navController)
        }

    }
}