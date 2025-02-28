package com.example.mediaplayerfragments

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayoutMediator

class ImageSwiperAdapter(
    private val images: List<Int>
) : RecyclerView.Adapter<ImageSwiperAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.msivfragmentpics)
        val cl: ConstraintLayout = view.findViewById(R.id.climageswiper)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_swiper, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.imageView.setImageResource(images[position])
        val constraintLayout = holder.cl
        val constraintSet = ConstraintSet()
        constraintSet.clone(constraintLayout)

        val ratio = holder.imageView.drawable.intrinsicHeight.toFloat() / holder.imageView.drawable.intrinsicWidth.toFloat()
        Toast.makeText(holder.imageView.context, "Ratio: $ratio", Toast.LENGTH_SHORT).show()
        constraintSet.setDimensionRatio(holder.imageView.id,"1:$ratio")
        constraintSet.applyTo(constraintLayout)
    }

    override fun getItemCount(): Int = images.size
}
