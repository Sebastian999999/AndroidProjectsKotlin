package com.example.foodsafetyapp.models

data class Warning(
    val foodName: String,
    val warningMessage: String,
    val riskLevel: RiskLevel,
    val safetyTips: String,
    val criticalPoints: List<String>,
    val preventiveMeasures: List<String>
)
