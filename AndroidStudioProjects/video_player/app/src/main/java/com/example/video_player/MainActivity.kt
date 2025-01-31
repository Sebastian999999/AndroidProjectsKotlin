package com.example.video_player

import android.net.Uri
import android.os.Bundle
import android.widget.MediaController
import android.widget.VideoView
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.video_player.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val vidview= findViewById<VideoView>(R.id.videoView)
        val mediaController = MediaController(this)
        mediaController.setAnchorView(vidview)


        val uri = Uri.parse("android.resource://"
                + packageName + "/raw"
                + "/capacityultimatecountryside")

        vidview.setMediaController(mediaController)
        vidview.setVideoURI(uri)
        vidview.requestFocus()
        vidview.start()

    }

}