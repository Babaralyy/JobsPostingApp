package com.codecoy.bahdjol.agent

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.codecoy.bahdjol.R
import com.codecoy.bahdjol.databinding.FragmentAgentRequestsBinding


class AgentRequestsFragment : Fragment() {

    private lateinit var mBinding: FragmentAgentRequestsBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentAgentRequestsBinding.inflate(inflater)

        inIt()

        return mBinding.root
    }

    private fun inIt() {

        mBinding.btnNewReq.setOnClickListener {
            val action = AgentRequestsFragmentDirections.actionAgentRequestsFragmentToNewReqFragment()
            findNavController().navigate(action)
        }

        mBinding.btnOngoingReq.setOnClickListener {
            val action = AgentRequestsFragmentDirections.actionAgentRequestsFragmentToOngoinReqFragment()
            findNavController().navigate(action)
        }

        mBinding.btnReqHistory.setOnClickListener {
            val action = AgentRequestsFragmentDirections.actionAgentRequestsFragmentToReqHistoryFragment()
            findNavController().navigate(action)
        }

    }

}