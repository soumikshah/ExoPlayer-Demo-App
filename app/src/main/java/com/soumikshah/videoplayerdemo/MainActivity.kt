package com.soumikshah.videoplayerdemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.FragmentContainerView

class MainActivity : AppCompatActivity() {


    private var fragment:FragmentContainerView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fragment = findViewById(R.id.fragment)
        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.fragment, MainFragment())
        transaction.commit()
    }



}