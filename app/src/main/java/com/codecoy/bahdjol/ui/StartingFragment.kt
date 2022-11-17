package com.codecoy.bahdjol.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.codecoy.bahdjol.R
import com.codecoy.bahdjol.constant.Constant.TAG
import com.codecoy.bahdjol.databinding.FragmentStartingBinding
import com.codecoy.bahdjol.datamodels.AgentLoginData
import com.codecoy.bahdjol.datamodels.UserData
import com.codecoy.bahdjol.utils.GlobalClass
import com.codecoy.bahdjol.utils.ServiceIds


class StartingFragment : Fragment() {

    private var userData: UserData? = null
    private var agentLoginData: AgentLoginData? = null

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

        ServiceIds.fetchBalanceFromPref(requireActivity(), "balanceInfo")

        userData = ServiceIds.fetchUserFromPref(requireContext(), "userInfo")
        agentLoginData = ServiceIds.fetchAgentFromPref(requireContext(), "agentInfo")

        mBinding.ivUser.setOnClickListener {

            Log.i(TAG, "inIt: user")

            if (userData != null){

                Log.i(TAG, "inIt: user not null")

                val action = StartingFragmentDirections.actionStartingFragmentToMainFragment()
                findNavController().navigate(action)
            } else {

                Log.i(TAG, "inIt: user null")

                val action = StartingFragmentDirections.actionStartingFragmentToSignInFragment()
                findNavController().navigate(action)

            }

        }

        mBinding.ivAgent.setOnClickListener {

            GlobalClass.drawer = null

            if (agentLoginData != null) {
                val action =
                    StartingFragmentDirections.actionStartingFragmentToAgentRequestsFragment()
                findNavController().navigate(action)
            } else {
                val action = StartingFragmentDirections.actionStartingFragmentToAgentSignInFragment()
                findNavController().navigate(action)
            }

        }

    }

}