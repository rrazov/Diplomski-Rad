package com.example.myfittracker.presentation.navigation

sealed class Screens (val route: String) {
    object Welcome : Screens("welcome")
    object ScanDevice : Screens("scan_device")

}