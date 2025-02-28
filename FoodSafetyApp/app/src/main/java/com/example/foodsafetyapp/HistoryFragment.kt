package com.example.foodsafetyapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodsafetyapp.adapter.HistoryAdapter
import com.example.foodsafetyapp.databinding.FragmentHistoryBinding
import com.example.foodsafetyapp.repository.FirebaseRepository
import kotlinx.coroutines.launch

class HistoryFragment : Fragment(R.layout.fragment_history) {
    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    private val firebaseRepository = FirebaseRepository()
    private lateinit var historyAdapter: HistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupSearch()
        loadHistory()
    }

    private fun setupRecyclerView() {
        historyAdapter = HistoryAdapter { historyEntry ->
            // Navigate to detail page
            val action = HistoryFragmentDirections.actionHistoryFragmentToDetailFragment(
                foodName = historyEntry.foodName,
                confidence = historyEntry.confidence,
                calories = historyEntry.calories,
                protein = historyEntry.protein,
                carbs = historyEntry.carbs,
                fat = historyEntry.fat,
                isSafe = historyEntry.isSafe!!,
                freshnessScore = historyEntry.freshnessScore,
                spoilageDetails = historyEntry.spoilageDetails.toTypedArray(),
                allergens = historyEntry.allergens.toTypedArray(),
                storageTemp = historyEntry.storageTemp,
                storageMethod = historyEntry.storageMethod,
                handlingTips = historyEntry.handlingTips.toTypedArray(),
                shelfLife = historyEntry.shelfLife,
                safetyWarnings = historyEntry.safetyWarnings.toTypedArray(),
                recommendedAction = historyEntry.recommendedAction,
                imageByteArray = historyEntry.imageByteArray.toIntArray()
            )
            findNavController().navigate(action)  // Fixed: Added the action parameter
        }

        binding.rvHistory.apply {
            adapter = historyAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun loadHistory(searchQuery: String = "") {
        lifecycleScope.launch {
            try {
                binding.progressBar.isVisible = true  // Add a progress bar
                val history = firebaseRepository.getUserHistory(searchQuery)
                historyAdapter.submitList(history)
                binding.progressBar.isVisible = false

                // Show/hide empty state
                binding.emptyState.isVisible = history.isEmpty()
            } catch (e: Exception) {
                binding.progressBar.isVisible = false
                Toast.makeText(context, "Error loading history: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setupSearch() {
        binding.searchViewHistory.apply {
            setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    query?.let { loadHistory(it) }
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    if (newText.isNullOrBlank()) {
                        loadHistory()  // Load all when search is cleared
                    }
                    return true
                }
            })
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}