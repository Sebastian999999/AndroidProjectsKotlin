package com.example.foodsafetyapp.models

data class NutritionInfo(
    val calories: Float,
    val protein: Float,
    val carbs: Float,
    val fat: Float,
    val sugar: Float = 0f,
    val allergens: List<String> = emptyList()
)
