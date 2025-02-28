package com.example.foodsafetyapp.models

data class ColorAnalysis(
    val isNormal: Boolean,
    val freshnessScore: Int,
    val issues: List<String>
)
