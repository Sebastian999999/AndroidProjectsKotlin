package com.example.mediaplayerfragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.mediaplayerfragments.databinding.FragmentInAppPremiumBanner1Binding


class InAppPremiumBannerFragment1 : Fragment() {

    private lateinit var view : FragmentInAppPremiumBanner1Binding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        view = FragmentInAppPremiumBanner1Binding.inflate(layoutInflater)
        view.mtvgetstarted.setOnClickListener {
            findNavController().navigate(R.id.action_InAppPremiumBannerFragment1_to_OBFragment)
        }
        return view.root
    }

}