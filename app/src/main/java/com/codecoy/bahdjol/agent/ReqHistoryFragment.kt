package com.codecoy.bahdjol.agent

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.codecoy.bahdjol.MainActivity
import com.codecoy.bahdjol.adapter.HistoryReqAdapter
import com.codecoy.bahdjol.constant.Constant
import com.codecoy.bahdjol.databinding.FragmentReqHistoryBinding
import com.codecoy.bahdjol.datamodels.AgentLoginData
import com.codecoy.bahdjol.datamodels.HistoryReqData
import com.codecoy.bahdjol.repository.Repository
import com.codecoy.bahdjol.utils.ServiceIds
import com.codecoy.bahdjol.utils.isNetworkConnected
import com.codecoy.bahdjol.viewmodel.MyViewModel
import com.codecoy.bahdjol.viewmodel.MyViewModelFactory


class ReqHistoryFragment : Fragment() {

    private var agentLoginData: AgentLoginData? = null

    private lateinit var myViewModel: MyViewModel

    private lateinit var activity: MainActivity

    private lateinit var historyReqAdapter: HistoryReqAdapter
    private lateinit var manager: LinearLayoutManager

    private lateinit var historyReqList: MutableList<HistoryReqData>

    private lateinit var mBinding: FragmentReqHistoryBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentReqHistoryBinding.inflate(inflater)

        inIt()

        return mBinding.root
    }

    private fun inIt() {

        val repository = Repository()
        val myViewModelFactory = MyViewModelFactory(repository)
        myViewModel =
            ViewModelProvider(this, myViewModelFactory)[MyViewModel::class.java]

        historyReqList = arrayListOf()

        mBinding.rvReqHistory.setHasFixedSize(true)
        manager = LinearLayoutManager(activity)
        mBinding.rvReqHistory.layoutManager = manager


        mBinding.toolBar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        mBinding.tvRetry.setOnClickListener {
            checkConnectivity()
        }

        getAgent()

    }

    private fun getAgent() {
        agentLoginData = ServiceIds.fetchAgentFromPref(requireContext(), "agentInfo")

        if (agentLoginData != null){

            checkConnectivity()

        }
    }

    private fun checkConnectivity() {

        if (activity.isNetworkConnected()){
            getHistoryRequests()
            mBinding.layNotConnected.visibility = View.GONE
        } else{
            mBinding.layNotConnected.visibility = View.VISIBLE
        }

    }

    private fun getHistoryRequests() {
        val dialog = Constant.getDialog(activity)
        dialog.show()

        myViewModel.historyRequests(agentLoginData?.id!!)

        myViewModel.historyReqLiveData.observe(activity
        ) {
            dialog.dismiss()
            if (it.status == true && it.data.isNotEmpty()) {

                Log.i(Constant.TAG, "response: add success ${it.data}")

                historyReqList = it.data

                historyReqList.reverse()

                historyReqAdapter = HistoryReqAdapter(activity, historyReqList)
                mBinding.rvReqHistory.adapter = historyReqAdapter

            } else{
                mBinding.tvNotFound.visibility = View.VISIBLE
                Toast.makeText(activity, it.message, Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity = context as MainActivity
    }

}