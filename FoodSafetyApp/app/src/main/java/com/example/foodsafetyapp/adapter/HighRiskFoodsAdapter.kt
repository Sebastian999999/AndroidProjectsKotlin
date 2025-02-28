package com.example.foodsafetyapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.foodsafetyapp.databinding.ItemHighRiskFoodBinding
import com.example.foodsafetyapp.models.FoodRecommendation

//class HighRiskFoodsAdapter :
//    ListAdapter<FoodRecommendation, HighRiskFoodsAdapter.ViewHolder>(RecommendationDiffCallback())
//{
//
//    class ViewHolder(private val binding: ItemHighRiskFoodBinding) :
//        RecyclerView.ViewHolder(binding.root) {
//
//        fun bind(recommendation: FoodRecommendation) {
//            binding.apply {
//                tvHighRiskFoodName.text = recommendation.foodName
//
//                // Set critical storage info combining shelf life and storage tip
//                tvCriticalStorage.text = buildString {
//                    append("Critical Storage Info:\n")
//                    append(recommendation.storageTip)
//                    append("\n\nShelf Life:\n")
//                    append(recommendation.shelfLife)
//                }
//
//                // Set safety tips
//                tvSafetyTips.text = buildString {
//                    append("Safety Tips:\n")
//                    append(recommendation.generalTips)
//                }
//            }
//        }
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        return ViewHolder(
//            ItemHighRiskFoodBinding.inflate(
//                LayoutInflater.from(parent.context),
//                parent,
//                false
//            )
//        )
//    }
//
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        holder.bind(getItem(position))
//    }
//}