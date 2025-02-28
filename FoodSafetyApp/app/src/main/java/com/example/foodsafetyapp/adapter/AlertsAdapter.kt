package com.example.foodsafetyapp.adapter

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.foodsafetyapp.R
import com.example.foodsafetyapp.models.Alert
import com.example.foodsafetyapp.models.AlertSeverity
import com.example.foodsafetyapp.repository.AlertsRepository
import java.text.SimpleDateFormat
import java.util.Locale

class AlertsAdapter(
    private val onAlertClicked: (Alert) -> Unit,
    private val alertsRepository: AlertsRepository
) : RecyclerView.Adapter<AlertsAdapter.AlertViewHolder>() {

    private var alerts: List<Alert> = emptyList()

    fun submitList(newAlerts: List<Alert>) {
        val oldList = alerts
        alerts = newAlerts

        // Add debug logging
        Log.d("AlertsAdapter", "Submitting ${newAlerts.size} alerts to adapter")

        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlertViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_alert, parent, false)
        return AlertViewHolder(view)
    }

    override fun onBindViewHolder(holder: AlertViewHolder, position: Int) {
        val alert = alerts[position]
        Log.d("AlertsAdapter", "Binding alert at position $position: ${alert.title}")
        holder.bind(alert)
    }

    override fun getItemCount() = alerts.size

    inner class AlertViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val cardView: CardView = itemView.findViewById(R.id.cardViewAlert)
        private val textViewTitle: TextView = itemView.findViewById(R.id.textViewAlertTitle)
        private val textViewMessage: TextView = itemView.findViewById(R.id.textViewAlertMessage)
        private val textViewDate: TextView = itemView.findViewById(R.id.textViewAlertDate)
        private val textViewFoodName: TextView = itemView.findViewById(R.id.textViewAlertFoodName)
        private val imageViewSeverity: ImageView = itemView.findViewById(R.id.imageViewAlertSeverity)
        private val imageViewFoodImage: ImageView = itemView.findViewById(R.id.imageViewAlertImage)
        private val unreadIndicator: View = itemView.findViewById(R.id.viewUnreadIndicator)

        fun bind(alert: Alert) {
            textViewTitle.text = alert.title
            textViewMessage.text = alert.message
            textViewFoodName.text = alert.foodName

            // Format date
            val dateFormatter = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
            textViewDate.text = dateFormatter.format(alert.timestamp)

            // Set severity icon and color
            val (backgroundColor, iconRes) = when (alert.severity) {
                AlertSeverity.LOW -> Pair(
                    Color.parseColor("#E8F5E9"),  // Light green
                    R.drawable.info
                )
                AlertSeverity.MEDIUM -> Pair(
                    Color.parseColor("#FFF8E1"),  // Light yellow
                    R.drawable.alert
                )
                AlertSeverity.HIGH -> Pair(
                    Color.parseColor("#FFEBEE"),  // Light red
                    R.drawable.warning
                )
            }

            cardView.setCardBackgroundColor(backgroundColor)
            imageViewSeverity.setImageResource(iconRes)

            // Show image if available
            val bitmap = alert.imageByteArray?.let { alertsRepository.byteArrayToBitmap(it) }
            if (bitmap != null) {
                imageViewFoodImage.setImageBitmap(bitmap)
                imageViewFoodImage.visibility = View.VISIBLE
            } else {
                imageViewFoodImage.visibility = View.GONE
            }

            // Show unread indicator for unread alerts
            unreadIndicator.visibility = if (alert.hasBeenRead) View.GONE else View.VISIBLE

            // Set click listener
            itemView.setOnClickListener {
                onAlertClicked(alert)
            }
        }
    }
}