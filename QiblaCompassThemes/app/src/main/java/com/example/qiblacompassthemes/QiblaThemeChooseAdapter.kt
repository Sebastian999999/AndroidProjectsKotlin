package com.example.qiblacompassthemes

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.color.MaterialColors.getColor
class QiblaThemeChooseAdapter(private val images: List<Int>) : RecyclerView.Adapter<QiblaThemeChooseAdapter.MyViewHolder>() {
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.ivqiblatheme)
        val cardView: CardView = itemView.findViewById(R.id.cvtheme)
        val chosenIcon: ImageView = itemView.findViewById(R.id.ivselectedicon)
    }
    var onItemClick: ((Int) -> Unit)? = null
    private lateinit var context: Context
    var selectedPosition = -1
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.qibla_theme_item,parent ,false)
        context = parent.context
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return images.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.imageView.setImageResource(images[position])
        holder.imageView.scaleType=ImageView.ScaleType.CENTER_INSIDE
        val strokeWidth = 15
        val strokeColor = ContextCompat.getColor(context,R.color.color_purple_primary)
        val drawable = GradientDrawable().apply{
            shape = GradientDrawable.RECTANGLE
            setStroke(strokeWidth,strokeColor)
            cornerRadius = 50F
            setColor(ContextCompat.getColor(context,R.color.white))
        }

        val defaultBackground = GradientDrawable().apply{
            shape=GradientDrawable.RECTANGLE
            cornerRadius=50F
            setColor(ContextCompat.getColor(context,R.color.white))
        }
        if (position == selectedPosition) {
            holder.cardView.cardElevation = 10F
            holder.cardView.background = drawable
            holder.chosenIcon.visibility = View.VISIBLE
        } else {
            holder.cardView.cardElevation = 0F
            holder.chosenIcon.visibility = View.GONE
            holder.cardView.background = defaultBackground
        }

        // Handle click events.
        holder.cardView.setOnClickListener {
            val sharedPref = holder.itemView.context.getSharedPreferences("theme_prefs", Context.MODE_PRIVATE)
            with(sharedPref.edit()){
                putInt("selectedImageId", images[position])
                apply()
            }
            // Store previous selection.
            val previousPosition = selectedPosition
            // Update selected position.
            selectedPosition = position

            onItemClick?.invoke(images[position])
            // Notify adapter of item changes to update UI.
            if (previousPosition != -1) notifyItemChanged(previousPosition)
            notifyItemChanged(position)
        }

    }
}