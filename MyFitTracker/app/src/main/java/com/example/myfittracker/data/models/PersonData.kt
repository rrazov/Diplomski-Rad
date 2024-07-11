package com.example.myfittracker.data.models

data class PersonData (
    val macAddress: String,
    val name: String,
    val temperature: String? = null,
    val heartRate: String? = null
)
