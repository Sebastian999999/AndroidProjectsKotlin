package com.example.foodsafetyapp.models

// models/FoodRecommendation.kt
data class FoodRecommendation(
    val foodName: String,
    val name: String,  // Same as foodName, needed for name.contains() checks
    val pantry_tip: String = "",
    val refrigerate_tip: String = "",
    val freeze_tip: String = "",
    val pantry_max: Int = 0,
    val refrigerate_max: Int = 0,
    val freeze_max: Int = 0,
    val tips: String = "",
    val isHighRisk: Boolean = refrigerate_max in 1..7 ||
            tips.contains("risk", ignoreCase = true) ||
            name.lowercase().let { n ->
                n.contains("meat") ||
                        n.contains("fish") ||
                        n.contains("dairy") ||
                        n.contains("egg") ||
                        n.contains("seafood")
            }
)
