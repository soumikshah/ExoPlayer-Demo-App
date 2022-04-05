package com.soumikshah.videoplayerdemo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.util.MimeTypes
import com.google.android.exoplayer2.util.Util

enum class MediaType {
    MP3,
    MP4,
    DASH,
    HLS
}

class PlayerFragment internal constructor(mediaType: MediaType): Fragment() {
    private var player: ExoPlayer?=null
    private var videoView:PlayerView? = null
    private var playWhenReady = true
    private var currentWindow = 0
    private var playbackPosition = 0L
    private var localMediaType: MediaType?= null
    private var progressBar: ProgressBar? = null

    init {
        this.localMediaType = mediaType
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_player,container,false)
        videoView = view.findViewById(R.id.video_view)
        progressBar = view.findViewById(R.id.progressBar)
        progressBar?.visibility = GONE
        return view
    }

    override fun onStart() {
        super.onStart()
        if(Util.SDK_INT>=24){
            initializePlayer()
        }
    }

    override fun onResume() {
        super.onResume()
//        hideSystemUi()
        if (Util.SDK_INT<24 || player == null){
            initializePlayer()
        }
    }

    override fun onPause() {
        super.onPause()
        if(Util.SDK_INT<24){
            releasePlayer()
        }
    }

    override fun onStop() {
        super.onStop()
        if(Util.SDK_INT>= 24){
            releasePlayer()
        }
    }

    private fun initializePlayer(){
        val trackSelector = DefaultTrackSelector(requireContext()).apply {
            setParameters(buildUponParameters().setMaxVideoSizeSd())
        }
        player = ExoPlayer.Builder(requireContext())
            .setTrackSelector(trackSelector)
            .build()
            .also { exoPlayer ->

                val mediaItem = when (localMediaType) {
                    MediaType.MP3 -> {
                        MediaItem.fromUri(getString(R.string.media_url_mp3))
                    }
                    //Implemented adaptive streaming for dash and HLS
                    MediaType.DASH -> {
                        MediaItem.Builder()
                            .setUri(getString(R.string.media_url_dash))
                            .setMimeType(MimeTypes.APPLICATION_MPD)
                            .build()
                    }
                    MediaType.HLS -> {
                        MediaItem.Builder()
                            .setUri(getString(R.string.media_url_hls))
                            .setMimeType(MimeTypes.APPLICATION_M3U8)
                            .build()
                    }
                    else -> {
                        MediaItem.fromUri(getString(R.string.media_url_mp4))
                    }
                }
                exoPlayer.setMediaItem(mediaItem)
                exoPlayer.addListener(object : Player.EventListener {
                    override fun onPlayerStateChanged(playWhenReady: Boolean,playbackState: Int) {
                        when (playbackState) {
                            Player.STATE_IDLE -> {}
                            Player.STATE_BUFFERING -> {
                                progressBar?.visibility = VISIBLE
                            }
                            Player.STATE_READY -> {
                                progressBar?.visibility = GONE
                            }
                            Player.STATE_ENDED -> {
                                Toast.makeText(requireContext(),"Video has ended!",Toast.LENGTH_SHORT).show()
                                activity?.supportFragmentManager?.popBackStack()
                            }
                        }
                    }

                    override fun onPlayerError(error: PlaybackException) {
                        Toast.makeText(requireContext(),
                            "There was an error (${error.errorCodeName}) while playing the video.",
                            Toast.LENGTH_LONG).show()
                        error.printStackTrace()
                        activity?.supportFragmentManager?.popBackStack()
                    }
                })
                exoPlayer.playWhenReady = playWhenReady
                exoPlayer.seekTo(currentWindow, playbackPosition)
                exoPlayer.prepare()
                exoPlayer.play()
            }
        videoView!!.player = player
    }

    private fun releasePlayer() {
        player?.run {
            playbackPosition = this.currentPosition
            currentWindow = this.currentWindowIndex
            playWhenReady = this.playWhenReady
            release()
        }
        player = null
    }

}
