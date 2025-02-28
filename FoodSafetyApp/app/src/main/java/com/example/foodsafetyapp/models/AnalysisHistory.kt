package com.example.foodsafetyapp.models

// This will be used later for history
data class AnalysisHistory(
    val id: Long,
    val timestamp: Long,
    val result: FoodAnalysisResult
)
