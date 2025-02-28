package com.example.foodsafetyapp.adapter

import androidx.recyclerview.widget.DiffUtil
import com.example.foodsafetyapp.models.FoodRecommendation

// adapters/RecommendationDiffCallback.kt
class RecommendationDiffCallback : DiffUtil.ItemCallback<FoodRecommendation>() {
    override fun areItemsTheSame(oldItem: FoodRecommendation, newItem: FoodRecommendation): Boolean {
        return oldItem.foodName == newItem.foodName
    }

    override fun areContentsTheSame(oldItem: FoodRecommendation, newItem: FoodRecommendation): Boolean {
        return oldItem == newItem
    }
}