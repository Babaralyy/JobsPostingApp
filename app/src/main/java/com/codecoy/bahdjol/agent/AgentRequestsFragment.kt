package com.codecoy.bahdjol.agent

import android.content.ContentValues.TAG
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.codecoy.bahdjol.MainActivity
import com.codecoy.bahdjol.R
import com.codecoy.bahdjol.constant.Constant
import com.codecoy.bahdjol.databinding.FragmentAgentRequestsBinding
import com.codecoy.bahdjol.datamodels.AgentLoginData
import com.codecoy.bahdjol.datamodels.AgentLoginResponse
import com.codecoy.bahdjol.network.ApiCall
import com.codecoy.bahdjol.ui.StartingFragmentDirections
import com.codecoy.bahdjol.utils.ServiceIds
import com.codecoy.bahdjol.utils.isNetworkConnected
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.zhanghai.android.materialratingbar.MaterialRatingBar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class AgentRequestsFragment : Fragment(R.layout.fragment_agent_requests),
    NavigationView.OnNavigationItemSelectedListener {

    private var agentLoginData: AgentLoginData? = null
    private var agentPass: String? = null
    private var deviceToken: String? = null

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

        navViews()

        setUpNavDrawer()



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


        mBinding.toolBar.setNavigationOnClickListener {
            mBinding.drawerLay.openDrawer(Gravity.LEFT)
        }

        getAgent()


    }


    private fun getAgent() {

        agentLoginData = ServiceIds.fetchAgentFromPref(requireContext(), "agentInfo")
        agentPass = ServiceIds.fetchAgentPasswordFromPref(activity, "agentPassInfo")
        deviceToken = ServiceIds.fetchDeviceTokenFromPref(activity, "tokenInfo")

        if (agentLoginData != null){

            if (agentPass != null && deviceToken != null){
                if (activity.isNetworkConnected()){
                    agentSignIn(agentLoginData!!)
                }
            }

            if (ServiceIds.notId != null && ServiceIds.notId.equals("2")){
                val action = AgentRequestsFragmentDirections.actionAgentRequestsFragmentToAgentNotificationFragment()
                findNavController().navigate(action)
            }

            agentDataOnViews(agentLoginData!!)

        }
    }

    private fun agentSignIn(agentInData: AgentLoginData) {

        CoroutineScope(Dispatchers.IO).launch {

            val signInApi = Constant.getRetrofitInstance().create(ApiCall::class.java)
            val signInCall =
                signInApi.signInAgent(agentInData.agentEmail!!, agentPass!!, deviceToken!!)

            signInCall.enqueue(object : Callback<AgentLoginResponse> {
                override fun onResponse(
                    call: Call<AgentLoginResponse>, response: Response<AgentLoginResponse>
                ) {

                    Log.i(TAG, "onResponse: agentSignIn outer${response.body()}")

                    if (response.isSuccessful) {

                        Log.i(TAG, "onResponse: agentSignIn inner${response.body()}")

                        if (response.body() != null && response.body()?.status == true) {

                            val agentData = response.body()!!.data[0]

                            ServiceIds.saveAgentIntoPref(activity, "agentInfo", agentData)
                            this@AgentRequestsFragment.agentLoginData = ServiceIds.fetchAgentFromPref(activity, "agentInfo")

                            CoroutineScope(Dispatchers.Main).launch {
                                agentDataOnViews(agentLoginData!!)
                            }

                        } else {
                            Log.i(TAG, "onResponse: agentSignIn ${response.body()?.message}")
                        }
                    }
                }
                override fun onFailure(call: Call<AgentLoginResponse>, t: Throwable) {
                    Log.i(TAG, "onResponse: agentSignIn failure ${t.message}")
                }
            })
        }

    }

    private fun agentDataOnViews(agentLoginData: AgentLoginData) {

        mBinding.navView.findViewById<TextView>(R.id.tvName).text = agentLoginData.agentName
        mBinding.navView.findViewById<TextView>(R.id.tvNumber).text = agentLoginData.agentPhone
        mBinding.navView.findViewById<TextView>(R.id.tvEmail).text = agentLoginData.agentEmail

        mBinding.navView.findViewById<MaterialRatingBar>(R.id.agentRating).rating = agentLoginData.agentRating!!.toFloat()

        mBinding.navView.findViewById<TextView>(R.id.tvRatingNumber).text = "${agentLoginData.agentRating} (${agentLoginData.totalUserRateAgent.toString()})"

    }

    private fun setUpNavDrawer() {

        mBinding.navView.bringToFront()

    }

    private fun navViews() {

        mBinding.navView.findViewById<ImageView>(R.id.ivClose).setOnClickListener {


            mBinding.drawerLay.close()

        }

        mBinding.navView.findViewById<LinearLayout>(R.id.notLay).setOnClickListener {

            val action =
                AgentRequestsFragmentDirections.actionAgentRequestsFragmentToAgentNotificationFragment()
            findNavController().navigate(action)

            mBinding.drawerLay.close()

        }

        mBinding.navView.findViewById<LinearLayout>(R.id.logoutLay).setOnClickListener {

            ServiceIds.agentLogout(activity, "agentInfo")
            val action =
                AgentRequestsFragmentDirections.actionAgentRequestsFragmentToStartingFragment()
            findNavController().navigate(action)

            Toast.makeText(activity, "Clicked", Toast.LENGTH_SHORT).show()

            mBinding.drawerLay.close()

        }

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        return false
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity = context as MainActivity
    }


}