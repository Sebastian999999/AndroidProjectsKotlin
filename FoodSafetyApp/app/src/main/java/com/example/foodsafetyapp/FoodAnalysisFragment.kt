package com.example.foodsafetyapp

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.foodsafetyapp.models.FoodAnalysisResult
import com.example.foodsafetyapp.models.SafetyRecommendations
import com.example.foodsafetyapp.repository.FoodAnalysisRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FoodAnalysisFragment : Fragment(R.layout.fragment_food_analysis) {
    private lateinit var progressBar: ProgressBar
    private lateinit var repository: FoodAnalysisRepository



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        repository = FoodAnalysisRepository(requireContext())
        progressBar = view.findViewById(R.id.progressBarAnalysis)
        //capturedImage =
        if (capturedImage != null) {
            Log.d("Captured image", "Image received. Proceeding with analysis.")
            capturedImage.let{analyzeImage(it!!)}
        } else {
            Log.d("Captured image", "No image received yet.")
        }
        //testLogMealAPI() // Your test method

        view.findViewById<View>(R.id.btnScanFood).setOnClickListener {
            findNavController().navigate(R.id.action_foodAnalysisFragment_to_scanImageFragment)
        }
    }

    private fun analyzeImage(bitmap: Bitmap) {
        progressBar.visibility = View.VISIBLE

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val result = repository.analyzeFoodImageWithLogMeal(bitmap)

                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    navigateToResults(result)  // Navigate in the main thread
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    showError(e)  // Ensure Toast is shown in the main thread
                }
            }
        }
    }


    private fun navigateToResults(result: FoodAnalysisResult) {
        lifecycleScope.launch {
            try {
                // Get safety recommendations based on food name
                val safetyRecommendations = repository.getFoodSafetyRecommendations(result.foodName)

                // Generate alert if food is unsafe
                if (!result.safetyInfo.isSafe) {
                    try {
                        repository.generateSafetyAlert(result, capturedImage)
                    } catch (e: Exception) {
                        Log.e("AlertGeneration", "Failed to generate alert: ${e.message}")
                        // Continue with navigation even if alert creation fails
                    }
                }

                // Navigate to results
                val action = FoodAnalysisFragmentDirections.actionFoodAnalysisFragmentToResultsFragment(
                    foodName = result.foodName,
                    confidence = result.confidence,
                    calories = result.nutrition.calories,
                    protein = result.nutrition.protein,
                    carbs = result.nutrition.carbs,
                    fat = result.nutrition.fat,
                    allergens = result.allergens.toTypedArray(),
                    isSafe = result.safetyInfo.isSafe,
                    freshnessScore = result.safetyInfo.freshnessScore,
                    spoilageDetails = result.safetyInfo.spoilageDetails.toTypedArray(),
                    recommendedAction = result.safetyInfo.recommendedAction,
                    storageTemp = safetyRecommendations.storageTemp,
                    storageMethod = safetyRecommendations.storageMethod,
                    handlingTips = safetyRecommendations.handlingTips.toTypedArray(),
                    shelfLife = safetyRecommendations.shelfLife,
                    safetyWarnings = safetyRecommendations.safetyWarnings.toTypedArray()
                )
                findNavController().navigate(action)
            } catch (e: Exception) {
                Log.e("Navigation", "Error navigating to results: ${e.message}")
                Toast.makeText(context, "Error showing results: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun testLogMealAPI(){
        capturedImage = BitmapFactory.decodeResource(resources, R.drawable.spoiled1) // Add a test image to your drawable
        analyzeImage(capturedImage!!)
    }
    private fun showError(e: Exception) {
        Toast.makeText(context, "Analysis failed: ${e.message}", Toast.LENGTH_LONG).show()
    }

    companion object {
        var capturedImage: Bitmap? = null
    }
}