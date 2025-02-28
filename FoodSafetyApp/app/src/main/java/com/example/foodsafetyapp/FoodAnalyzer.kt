package com.example.foodsafetyapp

import android.content.Context
import android.graphics.Bitmap
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.task.vision.classifier.ImageClassifier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FoodAnalyzer(context: Context) {
    private val foodClassifier: ImageClassifier
    private val spoilageDetector: ImageClassifier
    private val nutritionData: Map<String, Nutrition>

    init {
        // Load models
        foodClassifier = ImageClassifier.createFromFile(
            context,
            "ml/food_classifier.tflite"
        )

        spoilageDetector = ImageClassifier.createFromFile(
            context,
            "ml/spoilage_detector.tflite",
//            ImageClassifier.ImageClassifierOptions.builder()
//                .setMaxResults(2)
//                .build()
        ).apply {
            ImageClassifier.ImageClassifierOptions.builder()
                .setMaxResults(2)
                .build()
        }



        // Load nutrition data
        nutritionData = loadNutritionData(context)
    }

    suspend fun analyze(bitmap: Bitmap): AnalysisResult = withContext(Dispatchers.IO) {
        val foodResult = classifyFood(bitmap)
        val spoilageResult = detectSpoilage(bitmap)
        val nutrition = nutritionData[foodResult.name] ?: Nutrition()

        AnalysisResult(
            foodName = foodResult.name,
            confidence = foodResult.confidence,
            isFresh = spoilageResult.isFresh,
            freshnessScore = spoilageResult.score,
            nutrition = nutrition,
            allergens = nutrition.allergens
        )
    }

    private fun classifyFood(bitmap: Bitmap): FoodResult {
        val results = foodClassifier.classify(TensorImage.fromBitmap(bitmap))
        val best = results[0].categories.maxByOrNull { it.score }
        return FoodResult(
            name = best?.label?.replace("_", " ")?.capitalize() ?: "Unknown",
            confidence = best?.score ?: 0f
        )
    }

    private fun detectSpoilage(bitmap: Bitmap): SpoilageResult {
        val results = spoilageDetector.classify(TensorImage.fromBitmap(bitmap))
        val freshScore = results[0].categories.find { it.label == "fresh" }?.score ?: 0f
        return SpoilageResult(
            isFresh = freshScore > 0.7f,
            score = freshScore * 100
        )
    }

    private fun loadNutritionData(context: Context): Map<String, Nutrition> {
        val json = context.assets.open("ml/nutrition_db.json").bufferedReader().use { it.readText() }
        return Gson().fromJson(json, object : TypeToken<Map<String, Nutrition>>() {}.type)
    }

    data class FoodResult(val name: String, val confidence: Float)
    data class SpoilageResult(val isFresh: Boolean, val score: Float)
    data class Nutrition(
        val calories: Int = 0,
        val protein: Float = 0f,
        val carbs: Float = 0f,
        val fats: Float = 0f,
        val allergens: List<String> = emptyList()
    )

    data class AnalysisResult(
        val foodName: String,
        val confidence: Float,
        val isFresh: Boolean,
        val freshnessScore: Float,
        val nutrition: Nutrition,
        val allergens: List<String>
    )
}