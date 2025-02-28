package com.example.foodsafetyapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.foodsafetyapp.databinding.ItemWarningBinding
import com.example.foodsafetyapp.models.Warning

class WarningsAdapter(private val onWarningClick: (Warning) -> Unit) :
    ListAdapter<Warning, WarningsAdapter.ViewHolder>(WarningDiffCallback()) {

    class ViewHolder(
        private val binding: ItemWarningBinding,
        private val onWarningClick: (Warning) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(warning: Warning) {
            binding.apply {
                foodNameText.text = warning.foodName
                warningText.text = warning.warningMessage
                safetyTipsText.text = warning.safetyTips

                // Set click listener on the item
                root.setOnClickListener {
                    onWarningClick(warning)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemWarningBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            onWarningClick
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    private class WarningDiffCallback : DiffUtil.ItemCallback<Warning>() {
        override fun areItemsTheSame(oldItem: Warning, newItem: Warning): Boolean {
            return oldItem.foodName == newItem.foodName
        }

        override fun areContentsTheSame(oldItem: Warning, newItem: Warning): Boolean {
            return oldItem == newItem
        }
    }
}