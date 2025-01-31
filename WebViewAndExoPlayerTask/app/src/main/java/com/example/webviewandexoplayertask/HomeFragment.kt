package com.example.webviewandexoplayertask

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
        var view:View = inflater.inflate(R.layout.fragment_home, container, false)
        view.findViewById<Button>(R.id.mbnavtowebviewfragment).setOnClickListener{
            findNavController().navigate(R.id.action_homeFragment_to_webViewFragment)
        }
        return view
    }
}