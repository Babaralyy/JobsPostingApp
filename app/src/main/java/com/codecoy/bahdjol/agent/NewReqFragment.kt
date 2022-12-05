package com.codecoy.bahdjol.agent

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.codecoy.bahdjol.MainActivity
import com.codecoy.bahdjol.adapter.NewReqAdapter
import com.codecoy.bahdjol.callback.StatusCallBack
import com.codecoy.bahdjol.constant.Constant
import com.codecoy.bahdjol.constant.Constant.TAG
import com.codecoy.bahdjol.databinding.FragmentNewReqBinding
import com.codecoy.bahdjol.datamodels.AgentLoginData
import com.codecoy.bahdjol.datamodels.NewReqData
import com.codecoy.bahdjol.network.ApiCall
import com.codecoy.bahdjol.repository.Repository
import com.codecoy.bahdjol.utils.ServiceIds
import com.codecoy.bahdjol.utils.isNetworkConnected
import com.codecoy.bahdjol.viewmodel.MyViewModel
import com.codecoy.bahdjol.viewmodel.MyViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class NewReqFragment : Fragment(), StatusCallBack {

    private var agentLoginData: AgentLoginData? = null

    private lateinit var activity: MainActivity

    private lateinit var myViewModel: MyViewModel
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

        myViewModel.newReqLiveData.observe(activity
        ) {
            dialog.dismiss()
            if (it.status == true && it.data.isNotEmpty()) {


                Log.i(TAG, "response: add success ${it.data}")

                newReqList = it.data

                newReqList.reverse()

                newReqAdapter = NewReqAdapter(activity, newReqList, this)
                mBinding.rvNewReq.adapter = newReqAdapter

                mBinding.tvNotFound.visibility = View.GONE



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

    override fun onAcceptClick(position: Int, newReqData: NewReqData) {

        if (activity.isNetworkConnected()){
            changeStatus(1, position, newReqData)
        } else {
            Toast.makeText(activity, "Connect to the internet and try again!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDeclineClick(position: Int, newReqData: NewReqData) {

        if (activity.isNetworkConnected()){
            changeStatus(0, position, newReqData)
        } else {
            Toast.makeText(activity, "Connect to the internet and try again!", Toast.LENGTH_SHORT).show()
        }
    }



    private fun changeStatus(i: Int, position: Int, newReqData: NewReqData) {

        val dialog = Constant.getDialog(activity)
        dialog.show()

        lifecycleScope.launch {
            val statusApi = Constant.getRetrofitInstance().create(ApiCall::class.java)
            val statusCall = withContext(Dispatchers.IO) {
                statusApi.changeStatus(
                    agentLoginData?.id!!, newReqData.requestId!!.toInt(), i
                )
            }

            if (statusCall.isSuccessful) {

                dialog.dismiss()

                Log.i(TAG, "changeStatus: isSuccessful")

                val statusResponse = statusCall.body()

                Log.i(TAG, "changeStatus: isSuccessful ${statusCall.body()?.status}")

                if (statusResponse != null) {

                    if (statusResponse.status == true && statusResponse.data != null) {

                        val statusData = statusResponse.data
                        newReqList.removeAt(position)
                        newReqAdapter.notifyDataSetChanged()

                        if (newReqList.isEmpty()){
                            mBinding.tvNotFound.visibility = View.VISIBLE
                        }

                    } else {
                        Toast.makeText(activity, "Something went wrong!", Toast.LENGTH_SHORT).show()
                    }

                } else {
                    Toast.makeText(activity, "Something went wrong!", Toast.LENGTH_SHORT).show()
                }

            } else {
                dialog.dismiss()
                Toast.makeText(activity, statusCall.message(), Toast.LENGTH_SHORT).show()
            }

        }

    }

}