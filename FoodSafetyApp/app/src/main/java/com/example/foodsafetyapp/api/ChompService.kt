package com.example.foodsafetyapp.api

import com.example.foodsafetyapp.models.NutritionResponse
import okhttp3.MultipartBody
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ChompService {
    @Multipart
    @POST("v1/food-recognition")
    suspend fun analyzeFood(
        @Header("X-RapidAPI-Key") apiKey: String,
        @Header("X-RapidAPI-Host") host: String = "https://chomp-food-nutrition-database-v2.p.rapidapi.com",
        @Part image: MultipartBody.Part
    ): ChompResponse
}

data class ChompResponse(
    val predictions: List<FoodPrediction>,
    val nutritionalInfo: ChompNutritionInfo,
    val ingredients: List<String>
)

data class FoodPrediction(
    val name: String,
    val confidence: Double,
    val servingSize: String?
)

data class ChompNutritionInfo(
    val calories: Double,
    val protein: Double,
    val carbs: Double,
    val fat: Double,
    val servingSize: String,
    val nutrients: List<NutritionResponse.Nutrient>
)