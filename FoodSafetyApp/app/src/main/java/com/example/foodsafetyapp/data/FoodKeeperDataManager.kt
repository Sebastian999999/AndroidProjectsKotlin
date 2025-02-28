package com.example.foodsafetyapp.data

import android.content.Context
import android.util.Log
import com.example.foodsafetyapp.models.FoodKeeperData
import com.example.foodsafetyapp.models.FoodRecommendation
import com.example.foodsafetyapp.models.Product
import kotlinx.serialization.json.Json
import org.json.JSONObject

// data/FoodKeeperDataManager.kt
class FoodKeeperDataManager(private val context: Context) {
    private val foodKeeperData: FoodKeeperData

    init {
        try {
            val json = Json {
                ignoreUnknownKeys = true
                isLenient = true
                coerceInputValues = true  // Add this
            }

            val jsonString = context.assets
                .open("foodkeeper.json")
                .bufferedReader()
                .use { it.readText() }

            foodKeeperData = json.decodeFromString(jsonString)
        } catch (e: Exception) {
            Log.e("FoodKeeperDataManager", "Error reading JSON: ${e.message}")
            e.printStackTrace()
            throw e
        }
    }


    fun getRecommendations(foodName: String): FoodRecommendation? {
        // Find matching product in FoodKeeper data
        val product = foodKeeperData.sheets
            .firstOrNull { it.name == "Products" }
            ?.data
            ?.flatten()  // Flatten the nested list
            ?.find {
                it.name.contains(foodName, ignoreCase = true) ||
                        foodName.contains(it.name, ignoreCase = true)
            } ?: return null

        return FoodRecommendation(
            foodName = product.name,
            name = product.name,  // Same as foodName, needed for name.contains() checks
            pantry_tip = product.pantry_tip,
            refrigerate_tip = product.refrigerate_tip,
            freeze_tip = product.freeze_tip,
            pantry_max = product.pantry_max,
            refrigerate_max = product.refrigerate_max,
            freeze_max = product.freeze_max,
            tips = product.tips
            // isHighRisk is automatically computed in the data class
        )
    }

    private fun isHighRiskFood(product: Product): Boolean {
        return product.refrigerate_max < 7 ||
                product.tips.contains("risk", ignoreCase = true) ||
                product.tips.contains("careful", ignoreCase = true) ||
                product.tips.contains("hazard", ignoreCase = true) ||
                product.name.lowercase().let { name ->
                    name.contains("meat") ||
                            name.contains("fish") ||
                            name.contains("dairy") ||
                            name.contains("egg") ||
                            name.contains("seafood")
                }
    }
}