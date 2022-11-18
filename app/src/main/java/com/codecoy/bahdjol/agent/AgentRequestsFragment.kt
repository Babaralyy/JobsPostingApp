package com.codecoy.bahdjol.agent

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.codecoy.bahdjol.MainActivity
import com.codecoy.bahdjol.R
import com.codecoy.bahdjol.databinding.FragmentAgentRequestsBinding
import com.codecoy.bahdjol.utils.ServiceIds


class AgentRequestsFragment : Fragment(R.layout.fragment_agent_requests) {


    private lateinit var activity: MainActivity

    private lateinit var mBinding: FragmentAgentRequestsBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentAgentRequestsBinding.inflate(inflater)

        setHasOptionsMenu(true)

        inIt()

        return mBinding.root
    }

    private fun inIt() {


        mBinding.btnNewReq.setOnClickListener {
            val action =
                AgentRequestsFragmentDirections.actionAgentRequestsFragmentToNewReqFragment()
            findNavController().navigate(action)
        }

        mBinding.btnOngoingReq.setOnClickListener {
            val action =
                AgentRequestsFragmentDirections.actionAgentRequestsFragmentToOngoinReqFragment()
            findNavController().navigate(action)
        }

        mBinding.btnReqHistory.setOnClickListener {
            val action =
                AgentRequestsFragmentDirections.actionAgentRequestsFragmentToReqHistoryFragment()
            findNavController().navigate(action)
        }

        mBinding.ivExit.setOnClickListener {
            ServiceIds.agentLogout(activity, "agentInfo")
            val action = AgentRequestsFragmentDirections.actionAgentRequestsFragmentToStartingFragment()
            findNavController().navigate(action)
        }

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity = context as MainActivity
    }

}