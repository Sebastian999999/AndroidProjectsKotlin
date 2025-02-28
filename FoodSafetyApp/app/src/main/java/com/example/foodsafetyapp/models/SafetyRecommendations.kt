package com.example.foodsafetyapp.models

data class SafetyRecommendations(
    val storageTemp: String,
    val storageMethod: String,
    val handlingTips: List<String>,
    val shelfLife: String,
    val safetyWarnings: List<String>
)
