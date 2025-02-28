package com.example.qiblacompassthemes

import SharedViewModel
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class QiblaThemeChooserFragment : Fragment() {
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private var images = listOf<Int>(
        R.drawable.q1compass,
        R.drawable.q2compass,
        R.drawable.q3compass,
        R.drawable.q4compass,
        R.drawable.q5compass,
        R.drawable.q6compass,
        R.drawable.q7compass,
        R.drawable.q8compass,
        R.drawable.q9compass,
        R.drawable.q10compass,
        R.drawable.q11compass,
        R.drawable.q12compass,
        R.drawable.q13compass,
        R.drawable.q14compass,
        R.drawable.q15compass,
        //R.drawable.q16compass,
        R.drawable.q17compass,
        R.drawable.q18compass,
    )
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_qibla_theme_chooser, container, false)
        var qiblaAdapter = QiblaThemeChooseAdapter(images)

        view.findViewById<RecyclerView>(R.id.rcv).adapter = qiblaAdapter
        qiblaAdapter.onItemClick = { imageId ->
            sharedViewModel.selectImage(imageId)
            // Optionally, navigate to QiblaCompassFragment
        }
        view.findViewById<RecyclerView>(R.id.rcv).layoutManager = GridLayoutManager(requireContext(), 2)
        return view
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar?.apply {
            title = "Compass Themes"
            setDisplayHomeAsUpEnabled(true)
        }
    }

}