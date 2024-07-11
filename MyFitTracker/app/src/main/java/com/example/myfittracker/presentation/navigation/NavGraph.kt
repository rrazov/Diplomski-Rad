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
import com.example.myfittracker.presentation.screens.PeopleDetailsScreen
import com.example.myfittracker.presentation.viewmodel.SharedDevicesScreenViewModel

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
)
{
    // Get ViewModel instance
    val sharedViewModel: SharedDevicesScreenViewModel = viewModel()

    NavHost(navController = navController, startDestination = Screens.Welcome.route) {
        composable(Screens.Welcome.route) {
            WelcomeScreen(ctx = LocalContext.current,
                navController
            )
        }
        composable(Screens.ScanDevice.route) {
            ScanDevicesScreen(
                viewModel = sharedViewModel,
                ctx = LocalContext.current,
                navController)
        }
        composable(Screens.ListOfPeople.route){
            ListOfPeopleScreen(
                sharedViewModel,
                navController
            )
        }
        composable(Screens.PeopleDetailsScreen.route) { backStackEntry ->
            val macAddress = backStackEntry.arguments?.getString("macAddress") ?: ""
            PeopleDetailsScreen(macAddress, sharedViewModel)
        }

    }
}


