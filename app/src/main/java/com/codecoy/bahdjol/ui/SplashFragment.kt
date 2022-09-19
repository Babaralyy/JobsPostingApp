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
import com.codecoy.bahdjol.datamodels.UserData
import com.codecoy.bahdjol.utils.ServiceIds

class SplashFragment : Fragment() {

    private var userData: UserData? = null

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

        userData = ServiceIds.fetchUserFromPref(requireContext(), "userInfo")

        Handler(Looper.getMainLooper()).postDelayed({

            if (userData != null) {
                val action = SplashFragmentDirections.actionSplashFragmentToMainFragment()
                findNavController().navigate(action)
            } else {
                val action = SplashFragmentDirections.actionSplashFragmentToSignInFragment()
                findNavController().navigate(action)
            }

        }, 1000)

    }

}