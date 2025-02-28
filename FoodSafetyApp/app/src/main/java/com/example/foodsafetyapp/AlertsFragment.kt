package com.example.foodsafetyapp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodsafetyapp.adapter.AlertsAdapter
import com.example.foodsafetyapp.databinding.FragmentAlertsBinding
import com.example.foodsafetyapp.models.Alert
import com.example.foodsafetyapp.models.AlertSeverity
import com.example.foodsafetyapp.repository.AlertsRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date

class AlertsFragment : Fragment() {
    private var _binding: FragmentAlertsBinding? = null
    private val binding get() = _binding!!

    private lateinit var alertsRepository: AlertsRepository
    private lateinit var alertsAdapter: AlertsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAlertsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRepository()
        setupRecyclerView()
        setupSwipeRefresh()
        setupMarkAllReadButton()

        // Manually add test data for debugging
        // addTestData()

        // Load real alerts
        loadAlerts()
    }

    private fun setupRepository() {
        alertsRepository = AlertsRepository(requireContext())
    }

    private fun setupRecyclerView() {
        alertsAdapter = AlertsAdapter(
            onAlertClicked = { alert ->
                showAlertDetails(alert)
            },
            alertsRepository = alertsRepository
        )

        binding.recyclerViewAlerts.apply {
            adapter = alertsAdapter
            layoutManager = LinearLayoutManager(context)

            // Add this to help with debugging visual issues
            setHasFixedSize(true)
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            loadAlerts()
        }
    }

    private fun setupMarkAllReadButton() {
        binding.btnMarkAllRead.setOnClickListener {
            markAllAlertsAsRead()
        }
    }

    // This is for testing only
    private fun addTestData() {
        val testAlerts = listOf(
            Alert(
                id = "test1",
                userId = FirebaseAuth.getInstance().currentUser?.uid ?: "",
                timestamp = Date(),
                title = "Test Critical Warning",
                message = "This is a test critical warning.",
                foodName = "Raw Chicken",
                severity = AlertSeverity.HIGH,
                hasBeenRead = false
            ),
            Alert(
                id = "test2",
                userId = FirebaseAuth.getInstance().currentUser?.uid ?: "",
                timestamp = Date(System.currentTimeMillis() - 3600000), // 1 hour ago
                title = "Test Medium Warning",
                message = "This is a test medium warning.",
                foodName = "Milk",
                severity = AlertSeverity.MEDIUM,
                hasBeenRead = true
            )
        )

        alertsAdapter.submitList(testAlerts)
        binding.emptyStateView.visibility = View.GONE
    }

    private fun loadAlerts() {
        Log.d("AlertsFragment", "Loading alerts...")
        binding.progressBar.visibility = View.VISIBLE
        binding.emptyStateView.visibility = View.GONE

        lifecycleScope.launch {
            try {
                val alerts = alertsRepository.getAllAlerts()
                Log.d("AlertsFragment", "Loaded ${alerts.size} alerts")

                // Dump alert contents for debugging
                alerts.forEachIndexed { index, alert ->
                    Log.d("AlertsFragment", "Alert $index: ${alert.id}, ${alert.title}, ${alert.foodName}")
                }

                withContext(Dispatchers.Main) {
                    alertsAdapter.submitList(alerts)

                    binding.progressBar.visibility = View.GONE
                    binding.swipeRefreshLayout.isRefreshing = false

                    // Show empty state if no alerts
                    if (alerts.isEmpty()) {
                        binding.emptyStateView.visibility = View.VISIBLE
                        Log.d("AlertsFragment", "No alerts to display, showing empty state")
                    } else {
                        binding.emptyStateView.visibility = View.GONE
                        Log.d("AlertsFragment", "Displaying ${alerts.size} alerts")
                    }
                }

            } catch (e: Exception) {
                Log.e("AlertsFragment", "Error loading alerts: ${e.message}")
                e.printStackTrace()

                withContext(Dispatchers.Main) {
                    binding.progressBar.visibility = View.GONE
                    binding.swipeRefreshLayout.isRefreshing = false
                    Toast.makeText(context, "Error loading alerts: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun markAllAlertsAsRead() {
        lifecycleScope.launch {
            try {
                val success = alertsRepository.markAllAlertsAsRead()
                if (success) {
                    loadAlerts() // Reload to update UI
                    Toast.makeText(context, "All alerts marked as read", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Error marking alerts as read: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun showAlertDetails(alert: Alert) {
        // Mark the alert as read
        lifecycleScope.launch {
            try {
                alertsRepository.markAlertAsRead(alert.id)

                // Show alert details dialog
                AlertDetailDialog.newInstance(alert).show(
                    childFragmentManager,
                    "AlertDetailDialog"
                )

                // Refresh the list to update read status
                loadAlerts()
            } catch (e: Exception) {
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}