package com.example.myfittracker.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myfittracker.presentation.screens.ListOfPeopleScreen
import com.example.myfittracker.presentation.screens.ScanDevicesScreen
import com.example.myfittracker.presentation.screens.WelcomeScreen
import com.example.myfittracker.presentation.viewmodel.ScanDevicesScreenViewModel

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
)
{
    NavHost(navController = navController, startDestination = Screens.Welcome.route) {
        composable(Screens.Welcome.route) {
            WelcomeScreen(ctx = LocalContext.current,
                navController
            )
        }
        composable(Screens.ScanDevice.route) {
            // Get ViewModel instance
            val viewModel: ScanDevicesScreenViewModel = viewModel()
            ScanDevicesScreen(
                viewModel = viewModel,
                ctx = LocalContext.current,
                navController)
        }
        composable(Screens.ListOfPeople.route){
            ListOfPeopleScreen()
        }

    }
}


