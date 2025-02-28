package com.example.foodsafetyapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.foodsafetyapp.databinding.ItemCookingMethodBinding
import com.example.foodsafetyapp.models.CookingMethod

class CookingMethodsAdapter :
    ListAdapter<CookingMethod, CookingMethodsAdapter.ViewHolder>(CookingMethodDiffCallback()) {

    class ViewHolder(private val binding: ItemCookingMethodBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(method: CookingMethod) {
            binding.foodNameText.text = method.foodName
            binding.methodText.text = method.method
            binding.safetyTipsText.text = method.safetyTips
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemCookingMethodBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    private class CookingMethodDiffCallback : DiffUtil.ItemCallback<CookingMethod>() {
        override fun areItemsTheSame(oldItem: CookingMethod, newItem: CookingMethod): Boolean {
            return oldItem.foodName == newItem.foodName
        }

        override fun areContentsTheSame(oldItem: CookingMethod, newItem: CookingMethod): Boolean {
            return oldItem == newItem
        }
    }
}