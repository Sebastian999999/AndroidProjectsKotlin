package com.example.foodsafetyapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.foodsafetyapp.adapter.CookingMethodsAdapter
//import com.example.foodsafetyapp.adapter.HighRiskFoodsAdapter
//import com.example.foodsafetyapp.adapter.RecentFoodsAdapter
import com.example.foodsafetyapp.adapter.StorageRecommendationsAdapter
import com.example.foodsafetyapp.adapter.WarningsAdapter
import com.example.foodsafetyapp.data.FoodKeeperDataManager
import com.example.foodsafetyapp.databinding.FragmentRecommendationsBinding
import com.example.foodsafetyapp.models.CookingMethod
import com.example.foodsafetyapp.models.FoodRecommendation
import com.example.foodsafetyapp.models.RiskLevel
import com.example.foodsafetyapp.models.StorageTip
import com.example.foodsafetyapp.models.Warning
import com.example.foodsafetyapp.notification.NotificationWorker
import com.example.foodsafetyapp.repository.FirebaseRepository
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit


class RecommendationsFragment : Fragment() {
    private lateinit var binding: FragmentRecommendationsBinding
    private lateinit var storageRecommendationsAdapter: StorageRecommendationsAdapter
    private lateinit var cookingMethodsAdapter: CookingMethodsAdapter
    private lateinit var warningsAdapter: WarningsAdapter
    private lateinit var foodKeeperManager: FoodKeeperDataManager
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentRecommendationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerViews()
        setupFoodKeeperData()
        getLastFiveScansAndGenerateRecommendations()
        binding.btnTakeQuiz.setOnClickListener {
            findNavController().navigate(R.id.action_recommendationsFragment_to_quizFragment)
        }
    }

    private fun setupRecyclerViews() {
        // Storage Tips RecyclerView
        storageRecommendationsAdapter = StorageRecommendationsAdapter()
        binding.storageRecyclerView.apply {
            adapter = storageRecommendationsAdapter
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }

        // Cooking Methods RecyclerView
        cookingMethodsAdapter = CookingMethodsAdapter()
        binding.cookingRecyclerView.apply {
            adapter = cookingMethodsAdapter
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }

        // Warnings RecyclerView
        warningsAdapter = WarningsAdapter { warning ->
            showWarningDetails(warning)
        }
        binding.warningsRecyclerView.apply {
            adapter = warningsAdapter
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }
    }

    private fun setupFoodKeeperData() {
        try {
            foodKeeperManager = FoodKeeperDataManager(requireContext())
        } catch (e: Exception) {
            Log.e("RecommendationsFragment", "Error setting up FoodKeeper data", e)
            Toast.makeText(context, "Error loading food safety data", Toast.LENGTH_LONG).show()
        }
    }

    private fun getLastFiveScansAndGenerateRecommendations() {
        binding.progressBar.isVisible = true
        binding.contentGroup.isVisible = false
        binding.errorLayout.root.isVisible = false

        val userId = auth.currentUser?.uid ?: run {
            handleError("User not logged in")
            return
        }

        firestore.collection("history")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { documents ->
                Log.d("RecommendationsFragment", "Total documents found: ${documents.size()}")

                val lastFiveScans = documents.documents
                    .sortedByDescending { it.getLong("timestamp") }
                    .take(5)

                Log.d("RecommendationsFragment", "Processing ${lastFiveScans.size} recent scans")

                // Add detailed logging
                val foodNames = lastFiveScans.mapNotNull { it.getString("foodName") }
                Log.d("RecommendationsFragment", "Food names from database: $foodNames")

                val foodRecommendations = mutableListOf<FoodRecommendation>()

                for (doc in lastFiveScans) {
                    val foodName = doc.getString("foodName")
                    if (foodName != null) {
                        Log.d("RecommendationsFragment", "Processing food: $foodName")

                        try {
                            // Create a hardcoded recommendation for testing
                            val recommendation = FoodRecommendation(
                                foodName = foodName,
                                name = foodName,
                                pantry_tip = "Store in a cool, dry place",
                                refrigerate_tip = "Store at 4°C or below",
                                freeze_tip = "Can be frozen at -18°C",
                                pantry_max = 7,
                                refrigerate_max = 5,
                                freeze_max = 30,
                                tips = "Handle with care. When in doubt, throw it out."
                            )

                            Log.d("RecommendationsFragment", "Created hardcoded recommendation for: $foodName")
                            foodRecommendations.add(recommendation)
                        } catch (e: Exception) {
                            Log.e("RecommendationsFragment", "Error creating recommendation for $foodName", e)
                        }
                    }
                }

                Log.d("RecommendationsFragment", "Total hardcoded recommendations: ${foodRecommendations.size}")

                if (foodRecommendations.isEmpty()) {
                    handleError("No recommendations could be generated")
                    return@addOnSuccessListener
                }

                // Generate tips
                val storageTips = generateStorageTips(foodRecommendations)
                val cookingMethods = generateCookingMethods(foodRecommendations)
                val warnings = checkForHighRiskWarnings(foodRecommendations)

                // Submit to adapters
                storageRecommendationsAdapter.submitList(storageTips)
                cookingMethodsAdapter.submitList(cookingMethods)
                warningsAdapter.submitList(warnings)

                binding.progressBar.isVisible = false
                binding.contentGroup.isVisible = true
            }
            .addOnFailureListener { e ->
                Log.e("RecommendationsFragment", "Error fetching history: ", e)
                handleError("Error loading recommendations: ${e.message}")
            }
    }

    private fun generateStorageTips(foods: List<FoodRecommendation>): List<StorageTip> {
        val tips = foods.map { food ->
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
        Log.d("RecommendationsFragment", "Generated ${tips.size} storage tips")
        return tips
    }

    private fun generateCookingMethods(foods: List<FoodRecommendation>): List<CookingMethod> {
        val methods = foods.map { food ->
            CookingMethod(
                foodName = food.foodName,
                method = when {
                    food.isHighRisk -> "Cook thoroughly at high temperature (75°C)"
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
        Log.d("RecommendationsFragment", "Generated ${methods.size} cooking methods")
        return methods
    }

    private fun checkForHighRiskWarnings(foods: List<FoodRecommendation>): List<Warning> {
        val highRiskFoods = foods.filter { it.isHighRisk }
        Log.d("RecommendationsFragment", "Found ${highRiskFoods.size} high risk foods")

        val warnings = if (highRiskFoods.isNotEmpty()) {
            highRiskFoods.map { food ->
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
        } else {
            emptyList()
        }

        binding.warningsSection.isVisible = warnings.isNotEmpty()
        return warnings
    }

    private fun showWarningDetails(warning: Warning) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("${warning.foodName} - Safety Information")
            .setMessage(buildString {
                appendLine("Risk Level: ${warning.riskLevel}")
                appendLine("\nCritical Points:")
                warning.criticalPoints.forEach { appendLine("• $it") }
                appendLine("\nPreventive Measures:")
                warning.preventiveMeasures.forEach { appendLine("• $it") }
                appendLine("\nSafety Tips:")
                appendLine(warning.safetyTips)
            })
            .setPositiveButton("Got it") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun handleError(message: String) {
        binding.progressBar.isVisible = false
        binding.contentGroup.isVisible = false
        binding.errorLayout.apply {
            root.isVisible = true
            retryButton.setOnClickListener {
                getLastFiveScansAndGenerateRecommendations()
            }
        }
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    companion object {
        const val CHANNEL_ID = "1"
    }

}