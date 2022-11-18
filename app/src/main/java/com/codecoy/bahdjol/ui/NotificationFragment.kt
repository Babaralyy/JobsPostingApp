package com.codecoy.bahdjol.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.codecoy.bahdjol.MainActivity
import com.codecoy.bahdjol.R
import com.codecoy.bahdjol.adapter.NotificationAdapter
import com.codecoy.bahdjol.constant.Constant
import com.codecoy.bahdjol.databinding.FragmentNotificationBinding
import com.codecoy.bahdjol.datamodels.NotificationData
import com.codecoy.bahdjol.datamodels.UserData
import com.codecoy.bahdjol.repository.Repository
import com.codecoy.bahdjol.utils.ServiceIds
import com.codecoy.bahdjol.utils.isNetworkConnected
import com.codecoy.bahdjol.viewmodel.MyViewModel
import com.codecoy.bahdjol.viewmodel.MyViewModelFactory


class NotificationFragment : Fragment() {

    private var userData: UserData? = null

    private lateinit var activity: MainActivity

    private lateinit var myViewModel: MyViewModel
    private lateinit var notificationAdapter: NotificationAdapter
    private lateinit var manager: LinearLayoutManager
    private lateinit var notificationList: MutableList<NotificationData>

    private lateinit var mBinding: FragmentNotificationBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentNotificationBinding.inflate(inflater)

        inIt()

        return mBinding.root
    }

    private fun inIt() {

        val repository = Repository()
        val myViewModelFactory = MyViewModelFactory(repository)
        myViewModel =
            ViewModelProvider(this, myViewModelFactory)[MyViewModel::class.java]

        notificationList = arrayListOf()

        mBinding.rvNotification.setHasFixedSize(true)
        manager = LinearLayoutManager(activity)
        mBinding.rvNotification.layoutManager = manager

        getUserData()

        mBinding.tvRetry.setOnClickListener {
            checkConnectivity()
        }

        mBinding.toolBar.setNavigationOnClickListener {
            replaceFragment(ServicesFragment())
        }

    }

    private fun getUserData() {
        userData = ServiceIds.fetchUserFromPref(activity, "userInfo")

        if (userData != null){
            checkConnectivity()
        }

    }

    private fun checkConnectivity() {

        if (activity.isNetworkConnected()){
            getNotifications()
            mBinding.layNotConnected.visibility = View.GONE
        } else{
            mBinding.layNotConnected.visibility = View.VISIBLE
        }

    }

    private fun getNotifications() {

        val dialog = Constant.getDialog(activity)
        dialog.show()

        myViewModel.userNotifications(userData?.id!!)

        myViewModel.notificationLiveData.observe(activity
        ) {
            dialog.dismiss()
            if (it.status == true && it.data.isNotEmpty()) {

                Log.i(Constant.TAG, "response: add success ${it.data}")

                notificationList = it.data

                notificationList.reverse()

                notificationAdapter  = NotificationAdapter(activity, notificationList)
                mBinding.rvNotification.adapter = notificationAdapter

            } else{
                mBinding.tvNotFound.visibility = View.VISIBLE
                Toast.makeText(activity, it.message, Toast.LENGTH_SHORT).show()
            }
        }

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