package com.example.foodsafetyapp.models

data class RecommendationsUiState(
    val isLoading: Boolean = false,
    val error: Boolean = false,
    val storageTips: List<StorageTip> = emptyList(),
    val cookingMethods: List<CookingMethod> = emptyList(),
    val warnings: List<Warning> = emptyList()
)
