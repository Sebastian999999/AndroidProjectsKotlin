package com.example.foodsafetyapp.models

import kotlinx.serialization.Serializable

@Serializable
data class Sheet(
    val name: String,
    val data: List<List<Product>>
)
