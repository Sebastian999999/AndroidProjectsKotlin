package com.example.foodsafetyapp

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.foodsafetyapp.databinding.FragmentResultsPageBinding
import com.example.foodsafetyapp.models.WarningLevel
import com.example.foodsafetyapp.repository.FirebaseRepository
import com.example.foodsafetyapp.repository.FoodAnalysisRepository
import kotlinx.coroutines.launch

class ResultsFragment : Fragment() {
    private val args: ResultsFragmentArgs by navArgs()
    private lateinit var repository: FoodAnalysisRepository
    private lateinit var binding: FragmentResultsPageBinding
    private val firebaseRepository = FirebaseRepository()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentResultsPageBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("ResultsFragment", "Fragment created with food: ${args.foodName}")
        displayResults()

        // Save button
        view.findViewById<Button>(R.id.btnSave).setOnClickListener {
            lifecycleScope.launch {
                // Save to history
                saveToHistory()
                Toast.makeText(context, "Saved to history", Toast.LENGTH_SHORT).show()
                binding.progressBar.visibility = View.GONE
            }
        }

        // Share button
        view.findViewById<Button>(R.id.btnShare).setOnClickListener {
           // shareResults()
        }
    }

    private fun displayResults() {
        try{
            binding.apply {
                FoodAnalysisFragment.capturedImage.let { bitmap ->
                    ivFoodImage.setImageBitmap(bitmap)
                    ivFoodImage.visibility = View.VISIBLE
                }
                // Food Name and Confidence
                tvFoodName.text = "${args.foodName} (${(args.confidence * 100).toInt()}%)"

                // Safety Indicator
                tvSafetyIndicator.apply {
                    text = if (args.isSafe) "Safe to Eat" else "Exercise Caution"
                    setBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            if (args.isSafe) R.color.safe_green else R.color.unsafe_red
                        )
                    )
                }

                // Nutritional Information
                tvNutritionalInfo.text = """
                Nutritional Information:
                Calories: ${args.calories}
                Protein: ${args.protein}g
                Carbs: ${args.carbs}g
                Fat: ${args.fat}g
            """.trimIndent()

                // Freshness Score
                tvFreshnessScore.text = "Freshness Score: ${args.freshnessScore}%"

                // Allergen Alerts
                if (args.allergens.isNotEmpty()) {
                    tvAllergenAlerts.text = "Allergen Alerts: ${args.allergens.joinToString(", ")}"
                } else {
                    tvAllergenAlerts.text = "No allergens detected"
                }

                // Spoilage Details
                val spoilageText = if (args.spoilageDetails.isNotEmpty()) {
                    "Issues Found:\n${args.spoilageDetails.joinToString("\n")}"
                } else {
                    "No spoilage issues detected"
                }

                tvIssues.text = spoilageText

                tvSafetyRecommendations.text = buildString {
                    append("Storage Guidelines:\n")
                    append("Temperature: ${args.storageTemp}\n")
                    append("Method: ${args.storageMethod}\n")
                    append("\nShelf Life:\n${args.shelfLife}\n")

                    append("\nHandling Tips:\n")
                    args.handlingTips.forEach { tip ->
                        append("• $tip\n")
                    }

                    append("\nSafety Warnings:\n")
                    args.safetyWarnings.forEach { warning ->
                        append("⚠ $warning\n")
                    }
                }
            }
        }
        catch (e: Exception) {
            Log.e("ResultsFragment", "Error displaying results: ${e.message}", e)
            Toast.makeText(context, "Error displaying results: ${e.message}", Toast.LENGTH_LONG).show()
        }

    }

    private fun saveToHistory() {
        lifecycleScope.launch {
            try {
                binding.progressBar.visibility = View.VISIBLE

                val result = firebaseRepository.saveToHistory(
                    args,  // Pass all args directly
                    FoodAnalysisFragment.capturedImage!!
                )

                binding.progressBar.visibility = View.GONE
                Toast.makeText(context, result, Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}