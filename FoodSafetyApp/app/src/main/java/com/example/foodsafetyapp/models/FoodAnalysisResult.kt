package com.example.foodsafetyapp.models

import android.graphics.Bitmap

data class FoodAnalysisResult(
    val foodName: String,
    val confidence: Float,
    val nutrition: NutritionInfo,
    val safetyInfo: SafetyInfo,
    val image: Bitmap? = null,
    val allergens: List<String>
)
