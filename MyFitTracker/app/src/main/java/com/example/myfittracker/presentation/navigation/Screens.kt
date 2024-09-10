package com.example.myfittracker.presentation.navigation

sealed class Screens (val route: String) {
    object Welcome : Screens("welcome")
    object ScanDevice : Screens("scan_device")
    object ListOfPeople : Screens("list_of_people")
    object PersonDetailsScreen : Screens("person_details_screen/{macAddress}")
    object FitnessDashboardScreen : Screens("fitness_dashboard_screen/{macAddress}")
}