package com.example.foodsafetyapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.foodsafetyapp.databinding.ItemFoodRecommendationBinding
import com.example.foodsafetyapp.models.FoodRecommendation

//class RecentFoodsAdapter :
//    ListAdapter<FoodRecommendation, RecentFoodsAdapter.ViewHolder>(RecommendationDiffCallback())
//{
//
//    class ViewHolder(private val binding: ItemFoodRecommendationBinding) :
//        RecyclerView.ViewHolder(binding.root) {
//
//        fun bind(recommendation: FoodRecommendation) {
//            binding.apply {
//                tvFoodName.text = recommendation.foodName
//                tvStorageTip.text = recommendation.storageTip
//                tvShelfLife.text = recommendation.shelfLife
//                tvGeneralTips.text = recommendation.generalTips
//
//                if (recommendation.isHighRisk) {
//                    highRiskWarning.visibility = View.VISIBLE
//                } else {
//                    highRiskWarning.visibility = View.GONE
//                }
//            }
//        }
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        return ViewHolder(
//            ItemFoodRecommendationBinding.inflate(
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
//
