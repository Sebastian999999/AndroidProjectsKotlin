package com.example.foodsafetyapp.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodsafetyapp.data.FoodKeeperDataManager
import com.example.foodsafetyapp.models.CookingMethod
import com.example.foodsafetyapp.models.FoodRecommendation
import com.example.foodsafetyapp.models.RecommendationsUiState
import com.example.foodsafetyapp.models.RiskLevel
import com.example.foodsafetyapp.models.StorageTip
import com.example.foodsafetyapp.models.Warning
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class RecommendationsViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private lateinit var foodKeeperManager: FoodKeeperDataManager

    private val _uiState = MutableStateFlow(RecommendationsUiState())
    val uiState: StateFlow<RecommendationsUiState> = _uiState.asStateFlow()

    fun loadRecommendations() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, error = false) }

                // Get last 5 scans
                val userId = auth.currentUser?.uid ?: throw Exception("User not logged in")

                val scans = firestore.collection("history")
                    .whereEqualTo("userId", userId)
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .limit(5)
                    .get()
                    .await()

                val foodRecommendations = scans.mapNotNull { doc ->
                    val foodName = doc.getString("foodName") ?: return@mapNotNull null
                    foodKeeperManager.getRecommendations(foodName)
                }

                // Generate recommendations
                val storageTips = generateStorageTips(foodRecommendations)
                val cookingMethods = generateCookingMethods(foodRecommendations)
                val warnings = generateWarnings(foodRecommendations)

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        storageTips = storageTips,
                        cookingMethods = cookingMethods,
                        warnings = warnings
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, error = true)
                }
            }
        }
    }

    private fun generateStorageTips(foods: List<FoodRecommendation>): List<StorageTip> {
        return foods.map { food ->
            StorageTip(
                foodName = food.foodName,
                storageTip = buildString {
                    if (food.pantry_tip.isNotBlank()) {
                        append("• Pantry: ${food.pantry_tip}\n")
                    }
                    if (food.refrigerate_tip.isNotBlank()) {
                        append("• Refrigerator: ${food.refrigerate_tip}\n")
                    }
                    if (food.freeze_tip.isNotBlank()) {
                        append("• Freezer: ${food.freeze_tip}")
                    }
                }.trim(),
                optimalTemperature = when {
                    food.refrigerate_max > 0 -> "1-4°C"
                    food.freeze_max > 0 -> "-18°C"
                    else -> "Room temperature"
                }
            )
        }
    }

    private fun generateCookingMethods(foods: List<FoodRecommendation>): List<CookingMethod> {
        return foods.map { food ->
            CookingMethod(
                foodName = food.foodName,
                method = when {
                    food.isHighRisk -> "Cook thoroughly at high temperature"
                    food.name.contains("meat", ignoreCase = true) ->
                        "Cook until internal temperature reaches safe level"
                    else -> "Follow standard cooking instructions"
                },
                safetyTips = food.tips,
                minimumTemperature = when {
                    food.name.contains("beef", ignoreCase = true) -> 63
                    food.name.contains("pork", ignoreCase = true) -> 71
                    food.name.contains("poultry", ignoreCase = true) -> 74
                    food.isHighRisk -> 75
                    else -> null
                }
            )
        }
    }

    private fun generateWarnings(foods: List<FoodRecommendation>): List<Warning> {
        return foods.filter { it.isHighRisk }.map { food ->
            Warning(
                foodName = food.foodName,
                warningMessage = "This is a high-risk food item that requires careful handling",
                riskLevel = RiskLevel.HIGH,
                safetyTips = food.tips,
                criticalPoints = listOf(
                    "Must be stored at correct temperature",
                    "Handle with clean utensils",
                    "Cook thoroughly",
                    "Avoid cross-contamination"
                ),
                preventiveMeasures = listOf(
                    "Wash hands before handling",
                    "Keep refrigerated below 5°C",
                    "Use separate cutting boards",
                    "Cook to safe internal temperature"
                )
            )
        }
    }
}