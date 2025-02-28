package com.example.foodsafetyapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.foodsafetyapp.databinding.ItemStorageTipBinding
import com.example.foodsafetyapp.models.StorageTip

class StorageRecommendationsAdapter :
    ListAdapter<StorageTip, StorageRecommendationsAdapter.ViewHolder>(StorageTipDiffCallback()) {

    class ViewHolder(private val binding: ItemStorageTipBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(tip: StorageTip) {
            binding.foodNameText.text = tip.foodName
            binding.storageTipText.text = tip.storageTip
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemStorageTipBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    private class StorageTipDiffCallback : DiffUtil.ItemCallback<StorageTip>() {
        override fun areItemsTheSame(oldItem: StorageTip, newItem: StorageTip): Boolean {
            return oldItem.foodName == newItem.foodName
        }

        override fun areContentsTheSame(oldItem: StorageTip, newItem: StorageTip): Boolean {
            return oldItem == newItem
        }
    }
}
