package com.example.foodsafetyapp.api

import com.google.gson.annotations.SerializedName
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.Multipart as Multipart1
import retrofit2.http.Part as Part1

// SpoonacularService.kt
interface SpoonacularService {
    @Multipart1
    @POST("food/images/analyze")
    suspend fun analyzeFoodImage(
        @Query("apiKey") apiKey: String,
        @Part1("imageType") imageType: RequestBody = RequestBody.create(
            "text/plain".toMediaTypeOrNull(), "file"
        ),
        @Part1 image: MultipartBody.Part
    ): SpoonacularAnalyzeResponse
}

// Response data classes matching Spoonacular's exact format
data class SpoonacularAnalyzeResponse(
    val status: String,
    val category: Category,  // Changed from String to Category object
    val recipes: List<RecipeGuess>,
    val nutrition: NutritionAnalysis,
    val annotations: List<String>? // ingredients detected
)

// New data class for category
data class Category(
    val name: String,
    val probability: Double
)

data class NutritionAnalysis(
    val recipesUsed: Int,
    val calories: NutrientInfo,
    val fat: NutrientInfo,
    val protein: NutrientInfo,
    val carbs: NutrientInfo
)

data class NutrientInfo(
    val value: Double,
    val unit: String,
    //@SerializedName("confidenceRange95Percent") // Match the exact JSON field name
    val confidenceRange95: ConfidenceRange,
    val standardDeviation: Double
)

data class ConfidenceRange(
    val min: Double,
    val max: Double
)

data class RecipeGuess(
    val id: Long,
    val title: String,
    val imageType: String,
    val url: String  // Added this field from the response
)