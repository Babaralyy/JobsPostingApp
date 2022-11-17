package com.codecoy.bahdjol.ui

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.codecoy.bahdjol.MainActivity
import com.codecoy.bahdjol.R
import com.codecoy.bahdjol.adapter.SubsAdapter
import com.codecoy.bahdjol.callback.SubsCallback
import com.codecoy.bahdjol.constant.Constant
import com.codecoy.bahdjol.constant.Constant.TAG
import com.codecoy.bahdjol.databinding.FragmentSubscriptionBinding
import com.codecoy.bahdjol.databinding.SubsDialogLayBinding
import com.codecoy.bahdjol.datamodels.GetSubsResponse
import com.codecoy.bahdjol.datamodels.SubsData
import com.codecoy.bahdjol.datamodels.UserData
import com.codecoy.bahdjol.repository.Repository
import com.codecoy.bahdjol.utils.ServiceIds
import com.codecoy.bahdjol.utils.isNetworkConnected
import com.codecoy.bahdjol.viewmodel.MyViewModel
import com.codecoy.bahdjol.viewmodel.MyViewModelFactory


class SubscriptionFragment : Fragment(), SubsCallback {

    private lateinit var myViewModel: MyViewModel
    private lateinit var subsDataList: MutableList<SubsData>

    private lateinit var manager: LinearLayoutManager
    private lateinit var subscriptionAdapter: SubsAdapter

    private lateinit var activity: MainActivity

    private var totalBalance: Double = 0.0
    private var subsBalance: Double = 0.0

    private lateinit var currentBalance: String

    private var userData: UserData? = null


    private lateinit var mBinding: FragmentSubscriptionBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentSubscriptionBinding.inflate(inflater)

        inIt()

        return mBinding.root
    }

    private fun inIt() {

        val repository = Repository()
        val myViewModelFactory = MyViewModelFactory(repository)
        myViewModel = ViewModelProvider(this, myViewModelFactory)[MyViewModel::class.java]

        subsDataList = arrayListOf()

        manager = LinearLayoutManager(activity)
        mBinding.rvSubs.layoutManager = manager
        mBinding.rvSubs.setHasFixedSize(true)

        mBinding.tvRetry.setOnClickListener {
            checkConnectivity()
        }

        getUserData()

        getBalance()

       checkConnectivity()

    }

    private fun checkConnectivity() {

        if (activity.isNetworkConnected()){
            allSubs()
            checkSubs()
            mBinding.layNotConnected.visibility = View.GONE
        } else{
            mBinding.layNotConnected.visibility = View.VISIBLE
        }

    }



    private fun getBalance() {

        currentBalance =
            ServiceIds.fetchBalanceFromPref(activity, "balanceInfo").toString()
        totalBalance = currentBalance.toDouble()

    }

    private fun getUserData() {

        userData = ServiceIds.fetchUserFromPref(activity, "userInfo")


    }

    private fun checkSubs() {

        myViewModel.checkSubscription(userData!!.id!!)

        myViewModel.checkSubsLiveData.observe(viewLifecycleOwner
        ) {

            Log.i(TAG, "response: outer ${it.data}")

            if (it.status == true && it.data != null) {

                Log.i(TAG, "response: success ${it.data!!.id}")

                val checkSubsData = it.data

                if (checkSubsData!!.status!!.toInt() == 1){
                    mBinding.tvMember.text = "You have ${checkSubsData.pkgName} Subscription"
                    mBinding.tvPrice.text = "Price: ${checkSubsData.pkgPrice}"
                    mBinding.tvStart.text = "Start date: ${checkSubsData.startDate}"
                    mBinding.tvEnd.text = "End date: ${checkSubsData.endDate}"
                    mBinding.tvOrders.text = "Remaining orders: ${checkSubsData.orders}"
                    mBinding.subsLay.visibility = View.VISIBLE
                    mBinding.rvSubs.visibility = View.GONE
                } else{
                    mBinding.tvActiveSubs.text = "You have not Active Subscription"
                    mBinding.subsLay.visibility = View.GONE
                    mBinding.rvSubs.visibility = View.VISIBLE
                }

                ServiceIds.saveSubsIntoPref(activity, "subsInfo", checkSubsData!!)

            } else {
                Log.i(TAG, "response: failure ${it.data!!.id}")
                Toast.makeText(activity, it.message, Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun allSubs() {

        val dialog = Constant.getDialog(activity)
        dialog.show()

        myViewModel.allSubscriptions()

        myViewModel.allSubsLiveData.observe(
            viewLifecycleOwner
        ) {
            dialog.dismiss()

            Log.i(TAG, "response: outer ${it.data.size}")

            if (it.status == true && it.data.isNotEmpty()) {

                mBinding.tvNotFound.visibility = View.GONE

                Log.i(TAG, "response: success ${it.data.size}")

                subsDataList = it.data

                subscriptionAdapter = SubsAdapter(activity, subsDataList, this)
                mBinding.rvSubs.adapter = subscriptionAdapter

            } else {
                mBinding.tvNotFound.visibility = View.VISIBLE
                Toast.makeText(activity, it.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showDialog(subsData: SubsData) {

        val subsBinding: SubsDialogLayBinding =
            SubsDialogLayBinding.inflate(LayoutInflater.from(requireContext()))

        val dialog = Dialog(activity)
        dialog.setContentView(subsBinding.root)
        dialog.setCancelable(true)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        subsBalance = subsData.packagePrice.toString().toDouble()

        subsBinding.tvBalance.text = currentBalance
        subsBinding.tvOrders.text = "You Will Get ${subsData.totalOrders} Orders in ${subsData.packagePrice} for ${subsData.duration} month"

        subsBinding.btnContinue.setOnClickListener {


            if (subsBalance > totalBalance){
                Toast.makeText(activity, "You don't have enough balance!", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            } else {
                buySubs(userData, subsData)
                dialog.dismiss()
            }
        }

        dialog.show()

    }

    private fun buySubs(userData: UserData?, subsData: SubsData) {

        val dialog = Constant.getDialog(activity)
        dialog.show()

        if (userData != null){
            myViewModel.getSubscription(userData.id!!, subsData.id!!.toInt(), subsData.packageName.toString(), subsData.packagePrice!!.toDouble(), subsData.totalOrders.toString())

            Log.i(TAG, "buySubs: ${userData.id!!} ${subsData.id!!.toInt()}  ${subsData.packageName.toString()} ${subsData.packagePrice!!.toDouble()} ${subsData.duration.toString()}")

            myViewModel.getSubsLiveData.observe(viewLifecycleOwner
            ) {
                dialog.dismiss()

                Log.i(TAG, "response: outer ${it.data}")

                if (it.status == true && it.data != null) {

                    Log.i(TAG, "response: success ${it.data!!.id}")

                    checkSubs(userData)

                    totalBalance -= subsBalance

                    updateBalance(totalBalance)

                    Toast.makeText(activity, "Subscription Added!", Toast.LENGTH_SHORT).show()

                    replaceFragment(ServicesFragment())

                } else {
                    Log.i(TAG, "response: failure ${it.data!!.id}")
                    Toast.makeText(activity, it.message, Toast.LENGTH_SHORT).show()
                }
            }

        }

    }

    private fun checkSubs(userData: UserData) {

        myViewModel.checkSubscription(userData.id!!)

        myViewModel.checkSubsLiveData.observe(viewLifecycleOwner
        ) {

            Log.i(TAG, "response: outer ${it.data}")

            if (it.status == true && it.data != null) {

                Log.i(TAG, "response: success ${it.data!!.id}")

                val checkSubsData = it.data

                ServiceIds.saveSubsIntoPref(activity, "subsInfo", checkSubsData!!)

            } else {
                Log.i(TAG, "response: failure ${it.data!!.id}")
                Toast.makeText(activity, it.message, Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun updateBalance(totalBalance: Double) {

        myViewModel.updateBalance(userData?.id!!, totalBalance.toString())

        myViewModel.updateBalanceLiveData.observe(viewLifecycleOwner
        ) {

            if (it.status == true && it.data != null) {

                Log.i(TAG, "updateBalance: success ${it.message}")

                val walletData = it.data

                ServiceIds.saveBalanceIntoPref(activity, "balanceInfo",
                    walletData!!.balance!!
                )

            } else {
                Log.i(TAG, "updateBalance: failure ${it.message}")
                Toast.makeText(activity, it.message, Toast.LENGTH_SHORT).show()
            }
        }

    }


    override fun onSubsClick(position: Int, subsData: SubsData) {
        showDialog(subsData)
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager: FragmentManager = activity.supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLay, fragment)
        fragmentTransaction.commit()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity = context as MainActivity
    }

}