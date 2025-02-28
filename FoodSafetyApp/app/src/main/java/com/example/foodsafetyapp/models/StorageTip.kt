package com.example.foodsafetyapp.models
data class StorageTip(
    val foodName: String,
    val storageTip: String,
    val pantryDuration: String? = null,
    val refrigeratorDuration: String? = null,
    val freezerDuration: String? = null,
    val optimalTemperature: String,
    val additionalTips: String? = null
)
