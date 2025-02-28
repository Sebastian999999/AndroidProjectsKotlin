package com.example.mediaplayerfragments

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.example.mediaplayerfragments.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var view: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        view = ActivityMainBinding.inflate(layoutInflater)
        setContentView(view.root)

        // Get the NavHostFragment and set the navigation graph.
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fcv) as? NavHostFragment
            ?: throw IllegalStateException("NavController not found")
        navHostFragment.navController.setGraph(R.navigation.nav_graph)
    }
}
