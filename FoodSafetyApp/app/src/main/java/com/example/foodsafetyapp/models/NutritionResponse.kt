package com.example.foodsafetyapp.models

data class NutritionResponse(
    val foodName: String,
    val hasNutritionalInfo: Boolean,
    val nutritional_info: NutritionalInfo
) {
    data class NutritionalInfo(
        val calories: Double,
        val totalNutrients: TotalNutrients
    )

    data class TotalNutrients(
        val PROCNT: Nutrient,  // Protein
        val FAT: Nutrient,     // Fat
        val CHOCDF: Nutrient   // Carbs
    )

    data class Nutrient(
        val label: String,
        val quantity: Double,
        val unit: String
    )
}
