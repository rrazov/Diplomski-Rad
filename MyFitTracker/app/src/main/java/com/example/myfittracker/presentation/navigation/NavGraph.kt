package com.example.myfittracker.presentation.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myfittracker.domain.services.BleService
import com.example.myfittracker.presentation.screens.FitnessDashboard
import com.example.myfittracker.presentation.screens.ListOfPeopleScreen
import com.example.myfittracker.presentation.screens.ScanDevicesScreen
import com.example.myfittracker.presentation.screens.WelcomeScreen
import com.example.myfittracker.presentation.screens.PersonDetailsScreen
import com.example.myfittracker.presentation.viewmodel.PersonViewModel
import com.example.myfittracker.presentation.viewmodel.PersonViewModelFactory
import com.example.myfittracker.presentation.viewmodel.SharedDevicesScreenViewModel

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    bleService: BleService,
    sharedDevicesScreenViewModel: SharedDevicesScreenViewModel,
    innerPadding: PaddingValues
)
{
    // Get ViewModel instance
    //val sharedViewModel: SharedDevicesScreenViewModel = viewModel()

    NavHost(navController = navController, startDestination = Screens.Welcome.route) {
        composable(Screens.Welcome.route) {
            WelcomeScreen(ctx = LocalContext.current,
                navController
            )
        }
        composable(Screens.ScanDevice.route) {
            ScanDevicesScreen(
                viewModel = sharedDevicesScreenViewModel,
                ctx = LocalContext.current,
                navController,
                bleService = bleService)
        }
        composable(Screens.ListOfPeople.route) {
            ListOfPeopleScreen(
                sharedDevicesScreenViewModel,
                navController
            )
        }
        composable(Screens.PersonDetailsScreen.route) { backStackEntry ->
            val macAddress = backStackEntry.arguments?.getString("macAddress") ?: ""
            //val bleService = BleService()
            //val viewModel: PersonViewModel = viewModel(factory = PersonViewModelFactory(macAddress, "", bleService))
            PersonDetailsScreen(
                macAddress,
                sharedDevicesScreenViewModel,
                innerPadding,
            )
        }
        composable(Screens.FitnessDashboardScreen.route) { backStackEntry ->
            val macAddress = backStackEntry.arguments?.getString("macAddress") ?: ""
            //val bleService = BleService()
            //val viewModel: PersonViewModel = viewModel(factory = PersonViewModelFactory(macAddress, "", bleService))
            FitnessDashboard(
                macAddress = macAddress,
                sharedDevicesScreenViewModel = sharedDevicesScreenViewModel
            )
        }

    }
}


