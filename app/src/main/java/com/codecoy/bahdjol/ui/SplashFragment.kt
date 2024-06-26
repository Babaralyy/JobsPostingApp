package com.codecoy.bahdjol.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.codecoy.bahdjol.databinding.FragmentSplashBinding

class SplashFragment : Fragment() {



    private lateinit var mBinding: FragmentSplashBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentSplashBinding.inflate(inflater)

        inIt()

        return mBinding.root
    }

    private fun inIt() {

        Handler(Looper.getMainLooper()).postDelayed({

                val action = SplashFragmentDirections.actionSplashFragmentToStartingFragment()
                findNavController().navigate(action)

        }, 1000)

    }

}