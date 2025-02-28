package com.example.foodsafetyapp.models

data class CookingMethod(
    val foodName: String,
    val method: String,
    val minimumTemperature: Int? = null,
    val cookingDuration: String? = null,
    val safetyTips: String,
    val doneness: Map<String, String>? = null // e.g., "Medium Rare" to "63°C"
)
