package com.codecoy.bahdjol.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.codecoy.bahdjol.MainActivity
import com.codecoy.bahdjol.R
import com.codecoy.bahdjol.constant.Constant
import com.codecoy.bahdjol.databinding.FragmentMainBinding
import com.codecoy.bahdjol.datamodels.UserData
import com.codecoy.bahdjol.repository.Repository
import com.codecoy.bahdjol.utils.GlobalClass
import com.codecoy.bahdjol.utils.ServiceIds
import com.codecoy.bahdjol.utils.isNetworkConnected
import com.codecoy.bahdjol.viewmodel.MyViewModel
import com.codecoy.bahdjol.viewmodel.MyViewModelFactory
import com.google.android.material.navigation.NavigationView


class MainFragment : Fragment(), NavigationView.OnNavigationItemSelectedListener {

    private var userData: UserData? = null

    private lateinit var activity: MainActivity

    private lateinit var myViewModel: MyViewModel

    private lateinit var mBinding: FragmentMainBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentMainBinding.inflate(inflater)

        inIt()

        return mBinding.root
    }

    private fun inIt() {

        val repository = Repository()
        val myViewModelFactory = MyViewModelFactory(repository)
        myViewModel = ViewModelProvider(this, myViewModelFactory)[MyViewModel::class.java]

        getUserData()

        setUpNavDrawer()

        replaceFragment(ServicesFragment())
        mBinding.bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.iHome -> {
                    replaceFragment(ServicesFragment())
                    getUserData()
                }
                R.id.iCalendar -> {
                    replaceFragment(CalendarFragment())
                }
                R.id.iHistory -> {
                    replaceFragment(HistoryFragment())
                }
                R.id.iBell -> {
                    replaceFragment(NotificationFragment())
                }

            }
            true
        }

        navViews()

        GlobalClass.drawer = mBinding.drawerLay
        GlobalClass.bottomNavigation = mBinding.bottomNav
        GlobalClass.frag = this

    }


    private fun setUpNavDrawer() {

        mBinding.navView.bringToFront()

        mBinding.navView.setNavigationItemSelectedListener(activity as NavigationView.OnNavigationItemSelectedListener)

    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager: FragmentManager = activity.supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLay, fragment)
        fragmentTransaction.commit()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        return false
    }

    private fun getUserData() {

        userData = ServiceIds.fetchUserFromPref(activity, "userInfo")

        if (userData != null) {

            userDataOnViews(userData!!)

            checkConnectivity()

        }

    }

    private fun checkConnectivity() {
        if(activity.isNetworkConnected()){
            checkSubs(userData!!)
            userBalance(userData!!)
        }
    }

    private fun userBalance(userData: UserData) {


        myViewModel.userBalance(userData.id!!)

        myViewModel.userBalanceLiveData.observe(viewLifecycleOwner
        ) {

            if (it.status == true && it.data != null) {
                Log.i(Constant.TAG, "response: payment success ${it.data!!.balance}")

                val walletData = it.data

                val currentBalance: Double = walletData!!.balance!!.toDouble()

                ServiceIds.saveBalanceIntoPref(activity, "balanceInfo", currentBalance.toString())

            }else {
                Toast.makeText(activity, it.message, Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun checkSubs(userData: UserData) {

        myViewModel.checkSubscription(userData.id!!)

        myViewModel.checkSubsLiveData.observe(viewLifecycleOwner
        ) {

            Log.i(Constant.TAG, "response: outer ${it.data}")

            if (it.status == true && it.data != null) {

                Log.i(Constant.TAG, "response: success ${it.data!!.id}")

                val checkSubsData = it.data

                ServiceIds.saveSubsIntoPref(activity, "subsInfo", checkSubsData!!)

            } else {
                Log.i(Constant.TAG, "response: failure ${it.data!!.id}")
            }
        }

    }

    private fun userDataOnViews(userData: UserData) {

        Glide.with(activity).load(Constant.IMG_URL + userData.profileImg)
            .placeholder(R.drawable.ic_downloading)
            .error(R.drawable.ic_error)
            .into(mBinding.navView.findViewById(R.id.ivProfile))

        mBinding.navView.findViewById<TextView>(R.id.tvName).text = userData.name
        mBinding.navView.findViewById<TextView>(R.id.tvNumber).text = userData.phone
        mBinding.navView.findViewById<TextView>(R.id.tvEmail).text = userData.email

    }

    private fun navViews() {

        mBinding.navView.findViewById<ImageView>(R.id.ivClose).setOnClickListener {
            mBinding.drawerLay.close()
        }
        mBinding.navView.findViewById<LinearLayout>(R.id.profileLay).setOnClickListener {
            replaceFragment(ProfileFragment())
            mBinding.drawerLay.close()
        }
        mBinding.navView.findViewById<LinearLayout>(R.id.paymentLay).setOnClickListener {
            replaceFragment(PaymentFragment())
            mBinding.drawerLay.close()
        }
        mBinding.navView.findViewById<LinearLayout>(R.id.subsLay).setOnClickListener {
            replaceFragment(SubscriptionFragment())
            mBinding.drawerLay.close()
        }
        mBinding.navView.findViewById<LinearLayout>(R.id.contactLay).setOnClickListener {
            replaceFragment(ContactUsFragment())
            mBinding.drawerLay.close()
        }

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity = context as MainActivity
    }

}