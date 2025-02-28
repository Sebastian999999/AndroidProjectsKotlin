package com.example.foodsafetyapp.models

data class FoodQuiz(
    val question: String,
    val options: List<String>,
    val correctAnswer: String,
    val explanation: String
)
