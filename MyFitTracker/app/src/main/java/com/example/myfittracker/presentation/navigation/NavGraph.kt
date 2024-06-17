package com.example.myfittracker.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun NavGraph(navController: NavHostController = rememberNavController()

)
{
    NavHost(navController = navController, startDestination = Screens.Welcome.route ) {
        composable(Screens.Welcome.route) {
            WelcomeScreen(ctx = LocalContext.current)
        }

    }
}