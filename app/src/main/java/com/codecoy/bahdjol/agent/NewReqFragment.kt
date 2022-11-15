package com.codecoy.bahdjol.agent

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.codecoy.bahdjol.MainActivity
import com.codecoy.bahdjol.adapter.NewReqAdapter
import com.codecoy.bahdjol.adapter.TransactionAdapter
import com.codecoy.bahdjol.constant.Constant
import com.codecoy.bahdjol.databinding.FragmentNewReqBinding
import com.codecoy.bahdjol.datamodels.*
import com.codecoy.bahdjol.repository.Repository
import com.codecoy.bahdjol.utils.ServiceIds
import com.codecoy.bahdjol.utils.isNetworkConnected
import com.codecoy.bahdjol.viewmodel.MyViewModel
import com.codecoy.bahdjol.viewmodel.MyViewModelFactory


class NewReqFragment : Fragment() {

    private var agentLoginData: AgentLoginData? = null

    private lateinit var myViewModel: MyViewModel

    private lateinit var activity: MainActivity

    private lateinit var newReqAdapter: NewReqAdapter
    private lateinit var manager: LinearLayoutManager

    private lateinit var newReqList: MutableList<NewReqData>

    private lateinit var mBinding: FragmentNewReqBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentNewReqBinding.inflate(inflater)

        inIt()

        return mBinding.root
    }

    private fun inIt() {


        val repository = Repository()
        val myViewModelFactory = MyViewModelFactory(repository)
        myViewModel =
            ViewModelProvider(this, myViewModelFactory)[MyViewModel::class.java]

        newReqList = arrayListOf()

        mBinding.rvNewReq.setHasFixedSize(true)
        manager = LinearLayoutManager(activity)
        mBinding.rvNewReq.layoutManager = manager


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
            getNewRequests()
            mBinding.layNotConnected.visibility = View.GONE
        } else{
            mBinding.layNotConnected.visibility = View.VISIBLE
        }

    }

    private fun getNewRequests() {

        val dialog = Constant.getDialog(activity)
        dialog.show()

        myViewModel.newRequests(agentLoginData?.id!!)

        myViewModel.newReqLiveData.observe(viewLifecycleOwner
        ) {
            dialog.dismiss()
            if (it.status == true && it.data.isNotEmpty()) {


                Log.i(Constant.TAG, "response: add success ${it.data}")

                newReqList = it.data

                newReqList.reverse()

                newReqAdapter = NewReqAdapter(activity, newReqList)
                mBinding.rvNewReq.adapter = newReqAdapter



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