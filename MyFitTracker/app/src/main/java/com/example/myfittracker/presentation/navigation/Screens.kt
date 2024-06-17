package com.example.myfittracker.presentation.navigation

sealed class Screens (val route: String) {
    object Welcome : Screens("welcome")

}