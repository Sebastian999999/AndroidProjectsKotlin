package com.example.foodsafetyapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import com.example.foodsafetyapp.databinding.DialogAlertDetailBinding
import com.example.foodsafetyapp.models.Alert
import com.example.foodsafetyapp.models.AlertSeverity
import com.example.foodsafetyapp.repository.AlertsRepository
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Locale

class AlertDetailDialog : DialogFragment() {
    private var _binding: DialogAlertDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var alert: Alert
    private lateinit var alertsRepository: AlertsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)

        // Deserialize alert from arguments
        arguments?.getString(ARG_ALERT)?.let {
            alert = Gson().fromJson(it, Alert::class.java)
        } ?: dismissAllowingStateLoss()

        alertsRepository = AlertsRepository(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogAlertDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
        setupCloseButton()
    }

    private fun setupUI() {
        // Set title and message
        binding.textViewTitle.text = alert.title
        binding.textViewMessage.text = alert.message

        // Set food name
        binding.textViewFoodName.text = alert.foodName

        // Format date
        val dateFormatter = SimpleDateFormat("MMMM dd, yyyy HH:mm", Locale.getDefault())
        binding.textViewDate.text = dateFormatter.format(alert.timestamp)

        // Set severity background color and icon
        val (backgroundColor, iconRes, severityText) = when (alert.severity) {
            AlertSeverity.LOW -> Triple(
                R.color.safe_green,
                R.drawable.info,
                "Low Risk"
            )
            AlertSeverity.MEDIUM -> Triple(
                R.color.safety_yellow,
                R.drawable.alert,
                "Medium Risk"
            )
            AlertSeverity.HIGH -> Triple(
                R.color.unsafe_red,
                R.drawable.warning,
                "High Risk"
            )
        }

        binding.cardViewSeverity.setCardBackgroundColor(
            resources.getColor(backgroundColor, null)
        )
        binding.imageViewSeverity.setImageResource(iconRes)
        binding.textViewSeverity.text = severityText

        // Set image if available
        val bitmap = alert.imageByteArray?.let { alertsRepository.byteArrayToBitmap(it) }
        if (bitmap != null) {
            binding.imageViewFood.setImageBitmap(bitmap)
            binding.imageViewFood.visibility = View.VISIBLE
        } else {
            binding.imageViewFood.visibility = View.GONE
        }
    }

    private fun setupCloseButton() {
        binding.buttonClose.setOnClickListener {
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_ALERT = "alert"

        fun newInstance(alert: Alert): AlertDetailDialog {
            return AlertDetailDialog().apply {
                arguments = bundleOf(
                    ARG_ALERT to Gson().toJson(alert)
                )
            }
        }
    }
}