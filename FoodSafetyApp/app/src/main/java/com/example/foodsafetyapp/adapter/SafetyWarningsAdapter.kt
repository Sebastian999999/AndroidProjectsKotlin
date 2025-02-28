package com.example.foodsafetyapp.adapter

import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

//class SafetyWarningsAdapter :
//    ListAdapter<SafetyWarning, SafetyWarningsAdapter.ViewHolder>(SafetyWarningDiffCallback()) {
//
//    class ViewHolder(private val binding: ItemSafetyWarningBinding) :
//        RecyclerView.ViewHolder(binding.root) {
//
//        fun bind(warning: SafetyWarning) {
//            binding.apply {
//                foodTypeText.text = warning.foodType
//                warningText.text = warning.warningText
//
//                // Set background color based on risk level
//                warningCard.setCardBackgroundColor(
//                    when (warning.riskLevel) {
//                        RiskLevel.HIGH -> Color.parseColor("#FFEBEE")  // Light Red
//                        RiskLevel.MEDIUM -> Color.parseColor("#FFF3E0") // Light Orange
//                        RiskLevel.LOW -> Color.parseColor("#F1F8E9")   // Light Green
//                    }
//                )
//
//                precautionsText.text = warning.precautions
//            }
//        }
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        return ViewHolder(
//            ItemSafetyWarningBinding.inflate(
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
//
//    private class SafetyWarningDiffCallback : DiffUtil.ItemCallback<SafetyWarning>() {
//        override fun areItemsTheSame(oldItem: SafetyWarning, newItem: SafetyWarning) =
//            oldItem.foodType == newItem.foodType
//
//        override fun areContentsTheSame(oldItem: SafetyWarning, newItem: SafetyWarning) =
//            oldItem == newItem
//    }
//}