package com.example.foodsafetyapp.adapter

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.foodsafetyapp.R
import com.example.foodsafetyapp.databinding.ItemHistoryBinding
import com.example.foodsafetyapp.models.HistoryEntry
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HistoryAdapter(private val onItemClick: (HistoryEntry) -> Unit) :
    RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    private var items = listOf<HistoryEntry>()

    fun submitList(newItems: List<HistoryEntry>) {
        items = newItems
        notifyDataSetChanged()
    }

    inner class HistoryViewHolder(private val binding: ItemHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: HistoryEntry) {
            binding.apply {
                // Convert byte array back to bitmap
                val bitmap = byteArrayToBitmap(item.imageByteArray)
                ivFoodImage.setImageBitmap(bitmap)

                tvFoodName.text = item.foodName
                tvDateTime.text = formatDate(item.timestamp)
                val isItemSafe = item.isSafe ?: true
                Log.d("HistoryAdapter", "Food: ${item.foodName}, isSafe: ${item.isSafe}")
                Log.d("SafetyStatus:", item.isSafe.toString())
                tvSafetyStatus.apply {
                    text = if(isItemSafe) "Safe to Eat" else "Exercise Caution"
                    setBackgroundColor(
                        ContextCompat.getColor(
                        context,
                        if(isItemSafe) R.color.safe_green else R.color.unsafe_red
                    ))
                }

                root.setOnClickListener { onItemClick(item) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        return HistoryViewHolder(
            ItemHistoryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    private fun formatDate(timestamp: Long): String {
        return SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
            .format(Date(timestamp))
    }

    private fun byteArrayToBitmap(intArray: ArrayList<Int>): Bitmap {
        // Convert ArrayList<Int> back to ByteArray
        val byteArray = intArray.map { it.toByte() }.toByteArray()

        // Convert ByteArray to Bitmap
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }
}