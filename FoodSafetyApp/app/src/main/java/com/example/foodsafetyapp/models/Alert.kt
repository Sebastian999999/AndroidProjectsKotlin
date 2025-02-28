package com.example.foodsafetyapp.models

import java.util.Date

data class Alert(
    val id: String = "",
    val timestamp: Date = Date(),
    val title: String = "",
    val message: String = "",
    val foodName: String = "",
    val severity: AlertSeverity = AlertSeverity.MEDIUM,
    val hasBeenRead: Boolean = false,
    val imageByteArray: ArrayList<Int>? = null,
    val userId: String
)
