package com.example.webviewandexoplayertask

import YouTubeVideoInfoRetriever
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.common.util.Util
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.dash.DashMediaSource
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.session.MediaSession
import androidx.media3.ui.PlayerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

class WebViewFragment : Fragment() {

    private lateinit var webView: WebView
    private lateinit var fab: FloatingActionButton
    private lateinit var exoPlayer: ExoPlayer
    private lateinit var playerView: PlayerView
    private lateinit var mediaSession: MediaSession
    private var currentVideoId: String? = null
    private var currentHlsUrl: String? = null
    private var currentDashUrl: String? = null

    companion object {
         val YOUTUBE_DOMAINS = arrayOf(
            "https://m.youtube.com/",
            "https://www.youtube.com/",
            "https://youtube.com/",
            "https://youtu.be/"
        )
        const val YOUTUBE_WATCH_PATH = "watch"
        const val YOUTUBE_SHORT_PATH = "shorts/"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_web_view, container, false)
        webView = view.findViewById(R.id.wvyoutube)
        fab = view.findViewById(R.id.mfabexoyoutubevidplayer)
        playerView = view.findViewById(R.id.pv)
        playerView.visibility = View.GONE

        setupWebView()
        initializePlayer()
        setupFabClickListener()
        return view
    }

    private fun initializePlayer() {
        exoPlayer = ExoPlayer.Builder(requireContext())
            .setHandleAudioBecomingNoisy(true)
            .build()
            .apply {
                addListener(object : Player.Listener {
                    override fun onPlaybackStateChanged(playbackState: Int) {
                        if (playbackState == Player.STATE_ENDED) {
                            activity?.runOnUiThread {
                                playerView.visibility = View.GONE
                            }
                        }
                    }
                })

                setAudioAttributes(
                    androidx.media3.common.AudioAttributes.Builder()
                        .setUsage(C.USAGE_MEDIA)
                        .setContentType(C.AUDIO_CONTENT_TYPE_MOVIE)
                        .build(),
                    true
                )
            }

        mediaSession = MediaSession.Builder(requireContext(), exoPlayer).build()
        playerView.player = exoPlayer
    }

    private fun setupWebView() {
        with(webView.settings) {
            javaScriptEnabled = true
            domStorageEnabled = true
            mediaPlaybackRequiresUserGesture = false
            javaScriptCanOpenWindowsAutomatically = true
            userAgentString = "Mozilla/5.0 (Linux; Android 10; Mobile) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.120 Mobile Safari/537.36"
        }

        val history = mutableListOf<String>()
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                handleUrl(request.url.toString())
                return super.shouldOverrideUrlLoading(view, request)
            }

            override fun doUpdateVisitedHistory(view: WebView?, url: String?, isReload: Boolean) {
                super.doUpdateVisitedHistory(view, url, isReload)
                url?.let {
                    if (history.isEmpty() || history.last() != it) {
                        history.add(it)
                    }
                }
            }
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                url?.let { handleUrl(it) }
            }
        }

        webView.webChromeClient = WebChromeClient()
        webView.loadUrl("https://m.youtube.com")
    }

    private fun setupFabClickListener() {
        fab.setOnClickListener {
            currentHlsUrl?.let { playStream(it, isHls = true) }
                ?: currentDashUrl?.let { playStream(it, isHls = false) }
                ?: showError("No stream available")
        }
    }

    private fun handleUrl(url: String) {
        Toast.makeText(requireActivity(), url, Toast.LENGTH_SHORT).show()

        if (YOUTUBE_DOMAINS.any { url.startsWith(it) }) {
            when {
                url.contains(YOUTUBE_WATCH_PATH) -> extractWatchVideoId(url)
                url.contains(YOUTUBE_SHORT_PATH) -> extractShortsVideoId(url)
                else -> disableFab()
            }?.let { videoId ->
                if (currentVideoId != videoId) loadStream(videoId.toString())
            }
        } else {
            disableFab()
        }
    }

    private fun extractWatchVideoId(url: String): String? {
        val pattern1 = """v=([^&#]*)""".toRegex()
        val pattern2 = """v=([^&#]*)&pp=([^&#]*)""".toRegex()
        return pattern1.find(url)?.groupValues?.get(1)
    }

    private fun extractShortsVideoId(url: String): String? {
        val pattern = """shorts/([^?/]*)""".toRegex()
        return pattern.find(url)?.groupValues?.get(1)
    }

    private suspend fun fetchVideoInfo(videoId: String): Pair<String?, String?> {
        return withContext(Dispatchers.IO) {
            try {
                val url = URL("https://www.youtube.com/watch?v=$videoId")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")

                val html = connection.inputStream.bufferedReader().use { it.readText() }
                //Toast.makeText(requireActivity(),"video Id = $videoId",Toast.LENGTH_SHORT).show()
                // Parse HLS/DASH URLs from HTML
                val hlsUrl = extractFromHtml(html, "hlsManifestUrl")
                val dashUrl = extractFromHtml(html, "dashManifestUrl")

                Pair(dashUrl, hlsUrl)
            } catch (e: Exception) {
                Log.e("Stream Error", "HTML parsing failed", e)
                Pair(null, null)
            }
        }
    }

    private fun extractFromHtml(html: String, key: String): String? {
        val pattern = """"$key":"(.*?)"""".toRegex()
        return pattern.find(html)?.groupValues?.get(1)?.replace("\\/", "/")
    }

    private fun loadStream(videoId: String) {
        if (currentVideoId == videoId) return
        currentVideoId = videoId

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val (dashUrl, hlsUrl) = fetchVideoInfo(videoId)

                currentDashUrl = dashUrl
                currentHlsUrl = hlsUrl
                Toast.makeText(requireActivity(),"current dash url = $dashUrl",Toast.LENGTH_SHORT).show()
                Toast.makeText(requireActivity(),"current hls url = $hlsUrl",Toast.LENGTH_SHORT).show()
                activity?.runOnUiThread {
                    fab.isEnabled = !hlsUrl.isNullOrEmpty() || !dashUrl.isNullOrEmpty()
                    if (fab.isEnabled) {
                        fab.show()
                    } else {
                        fab.hide()
                    }
                }
            } catch (e: Exception) {
                Log.e("Stream Error", "Stream loading failed", e)
                showError("Stream loading failed: ${e.message}")
                disableFab()
            }
        }
    }

    @androidx.annotation.OptIn(UnstableApi::class)
    @OptIn(UnstableApi::class)
    private fun playStream(streamUrl: String, isHls: Boolean) {
        activity?.runOnUiThread {
            try {
                exoPlayer.stop()

                val dataSourceFactory = DefaultHttpDataSource.Factory()
                    .setUserAgent(Util.getUserAgent(requireContext(), "YTPlayer"))
                    .setAllowCrossProtocolRedirects(true)

                val mediaSource = if (isHls) {
                    HlsMediaSource.Factory(dataSourceFactory)
                        .createMediaSource(MediaItem.fromUri(streamUrl))
                } else {
                    DashMediaSource.Factory(dataSourceFactory)
                        .createMediaSource(MediaItem.fromUri(streamUrl))
                }

                playerView.visibility = View.VISIBLE
                exoPlayer.setMediaSource(mediaSource)
                exoPlayer.prepare()
                exoPlayer.playWhenReady = true
            } catch (e: Exception) {
                Log.e("PlayerError", "Stream playback failed", e)
                showError("Playback failed: ${e.message}")
            }
        }
    }

    private fun disableFab() {
        activity?.runOnUiThread {
            fab.isEnabled = false
            currentHlsUrl = null
            currentDashUrl = null
        }
    }

    private fun showError(message: String) {
        activity?.runOnUiThread {
            Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
        }
    }

    override fun onPause() {
        super.onPause()
        exoPlayer.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaSession.release()
        exoPlayer.release()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (webView.canGoBack()) webView.goBack() else isEnabled = false
                }
            }
        )
    }
}