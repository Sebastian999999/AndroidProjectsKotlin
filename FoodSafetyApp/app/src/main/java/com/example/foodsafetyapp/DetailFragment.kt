package com.example.foodsafetyapp

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.navArgs
import com.example.foodsafetyapp.databinding.FragmentDetailBinding

class DetailFragment : Fragment() {
    private val args: DetailFragmentArgs by navArgs()
    private lateinit var binding: FragmentDetailBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        displayDetails()

        binding.btnShare.setOnClickListener {
            // Share functionality will be implemented later
            Toast.makeText(context, "Share functionality coming soon", Toast.LENGTH_SHORT).show()
        }
    }

    private fun displayDetails() {
        try {
            binding.apply {
                val imageIntArray = args.imageByteArray.toCollection(ArrayList())
                val bitmap = byteArrayToBitmap(imageIntArray)
                ivFoodImage.setImageBitmap(bitmap)
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
                tvAllergenAlerts.text = if (args.allergens.isNotEmpty()) {
                    "Allergen Alerts: ${args.allergens.joinToString(", ")}"
                } else {
                    "No allergens detected"
                }

                // Spoilage Details
                tvIssues.text = if (args.spoilageDetails.isNotEmpty()) {
                    "Issues Found:\n${args.spoilageDetails.joinToString("\n")}"
                } else {
                    "No spoilage issues detected"
                }

                // Safety Recommendations
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
        } catch (e: Exception) {
            Log.e("DetailFragment", "Error displaying details: ${e.message}", e)
            Toast.makeText(context, "Error displaying details", Toast.LENGTH_LONG).show()
        }
    }

    private fun byteArrayToBitmap(intArray: ArrayList<Int>): Bitmap {
        // Convert ArrayList<Int> back to ByteArray
        val byteArray = intArray.map { it.toByte() }.toByteArray()

        // Convert ByteArray to Bitmap
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }
}