package com.example.foodsafetyapp.models

data class IngredientsResponse(
    val ingredients: List<Ingredient>
) {
    data class Ingredient(
        val name: String,
        val probability: Float
    )
}
