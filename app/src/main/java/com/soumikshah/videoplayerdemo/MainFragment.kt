package com.soumikshah.videoplayerdemo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.soumikshah.videoplayerdemo.MediaType
import com.soumikshah.videoplayerdemo.PlayerFragment

class MainFragment:Fragment() {

    private var startMP3: Button?= null
    private var startMP4: Button? = null
    private var startDash: Button? = null
    private var startHLS: Button? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_main,container,false)
        startMP3 = view.findViewById(R.id.startMP3)
        startMP4 = view.findViewById(R.id.startMP4)
        startDash = view.findViewById(R.id.startDASH)
        startHLS = view.findViewById(R.id.startHLS)

        startMP3!!.setOnClickListener {
            startVideoPlayerFragment(MediaType.MP3)
        }

        startMP4!!.setOnClickListener {
            startVideoPlayerFragment(MediaType.MP4)
        }

        startDash!!.setOnClickListener {
            startVideoPlayerFragment(MediaType.DASH)
        }

        startHLS!!.setOnClickListener {
            startVideoPlayerFragment(MediaType.HLS)
        }
        return view
    }

    private fun startVideoPlayerFragment(mediaType: MediaType){
        val transaction = activity?.supportFragmentManager!!.beginTransaction()
        transaction.replace(R.id.fragment, PlayerFragment(mediaType))
        transaction.addToBackStack(null)
        transaction.commit()
    }
}