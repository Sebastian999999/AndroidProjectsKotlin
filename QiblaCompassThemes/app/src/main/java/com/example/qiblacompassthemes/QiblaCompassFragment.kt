package com.example.qiblacompassthemes

import SharedViewModel
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels

class QiblaCompassFragment : Fragment() {

    private val sharedViewModel: SharedViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_qibla_compass, container, false)
        val sharedPref = requireActivity().getSharedPreferences("theme_prefs", Context.MODE_PRIVATE)

        val ImageId = sharedPref.getInt("selectedImageId", R.drawable.q1compass) // default if not set
        view.findViewById<ImageView>(R.id.ivqiblacompasstheme).setBackgroundResource(ImageId) // default if not set
        sharedViewModel.selectedImageId.observe(viewLifecycleOwner) { imageId ->
            view.findViewById<ImageView>(R.id.ivqiblacompasstheme)
                .setBackgroundResource(ImageId)
        }
        view.findViewById<ImageButton>(R.id.mbapplytheme).setOnClickListener {
            (activity as AppCompatActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.fcv, QiblaThemeChooserFragment())
                .addToBackStack(null)
                .commit()
        }
        return view
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar?.apply {
            title = "Qibla Compass"
            setDisplayHomeAsUpEnabled(false)  // Show back button for navigation
        }
    }
}