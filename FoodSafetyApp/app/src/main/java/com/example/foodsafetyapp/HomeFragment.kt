package com.example.foodsafetyapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController

class HomeFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        view.findViewById<Button>(R.id.btnAnalyzeFood).setOnClickListener {
            // Navigate from HomeFragment to FoodAnalysisFragment
            findNavController().navigate(R.id.action_homeFragment_to_foodAnalysisFragment)
        }

        // Quick Access: Check Alerts
        view.findViewById<Button>(R.id.btnCheckAlerts).setOnClickListener {
            // Navigate from HomeFragment to AlertsFragment
            findNavController().navigate(R.id.action_homeFragment_to_alertsFragment)
        }

        // Quick Access: Recommendations
        view.findViewById<Button>(R.id.btnRecommendations).setOnClickListener {
            // Navigate from HomeFragment to RecommendationsFragment
            findNavController().navigate(R.id.action_homeFragment_to_recommendationsFragment)
        }
        return view  // Return the modified view with listeners attached.
    }


}