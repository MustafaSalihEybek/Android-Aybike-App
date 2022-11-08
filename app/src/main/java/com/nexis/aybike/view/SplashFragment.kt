package com.nexis.aybike.view

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavDirections
import androidx.navigation.Navigation
import com.nexis.aybike.R

class SplashFragment : Fragment() {
    private lateinit var v: View
    private lateinit var navDirections: NavDirections
    private val waitTime: Long = 1500

    private fun init(){
        Handler(Looper.getMainLooper()).postDelayed({
            goToMainPage()
        }, waitTime)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        v = view
        init()
    }

    private fun goToMainPage(){
        navDirections = SplashFragmentDirections.actionSplashFragmentToSignFragment(false)
        Navigation.findNavController(v).navigate(navDirections)
    }
}