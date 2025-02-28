package com.example.foodsafetyapp.models

import kotlinx.serialization.Serializable

@Serializable
data class FoodKeeperData(
    val fileName: String? = null,
    val sheets: List<Sheet>
)
