package com.example.foodsafetyapp.models

data class SafetyInfo(
    val isSafe: Boolean,
    val freshnessScore: Int,
    val spoilageDetails: List<String>,
    val recommendedAction: String
)