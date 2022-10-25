package com.codecoy.bahdjol.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.codecoy.bahdjol.R
import com.codecoy.bahdjol.databinding.FragmentStartingBinding
import com.codecoy.bahdjol.datamodels.UserData
import com.codecoy.bahdjol.utils.ServiceIds


class StartingFragment : Fragment() {

    private var userData: UserData? = null

    private lateinit var mBinding: FragmentStartingBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentStartingBinding.inflate(inflater)

        inIt()

        return mBinding.root
    }

    private fun inIt() {

        userData = ServiceIds.fetchUserFromPref(requireContext(), "userInfo")

        mBinding.ivUser.setOnClickListener {

            if (userData != null){
                val action = StartingFragmentDirections.actionStartingFragmentToMainFragment()
                findNavController().navigate(action)
            } else {
                val action = StartingFragmentDirections.actionStartingFragmentToSignInFragment()
                findNavController().navigate(action)
            }

        }

        mBinding.ivAgent.setOnClickListener {

            val action = StartingFragmentDirections.actionStartingFragmentToAgentSignInFragment()
            findNavController().navigate(action)

        }

    }

}