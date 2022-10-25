package com.codecoy.bahdjol.agent

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.codecoy.bahdjol.R
import com.codecoy.bahdjol.databinding.FragmentAgentSignInBinding


class AgentSignInFragment : Fragment() {


    private lateinit var mBinding: FragmentAgentSignInBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentAgentSignInBinding.inflate(inflater)

        inIt()

        return mBinding.root
    }

    private fun inIt() {

        mBinding.btnSignIn.setOnClickListener {

            val action = AgentSignInFragmentDirections.actionAgentSignInFragmentToAgentRequestsFragment()
            findNavController().navigate(action)

        }

    }

}