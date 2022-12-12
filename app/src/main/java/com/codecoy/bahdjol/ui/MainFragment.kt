package com.codecoy.bahdjol.ui

import android.content.ContentValues
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
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.codecoy.bahdjol.MainActivity
import com.codecoy.bahdjol.R
import com.codecoy.bahdjol.constant.Constant
import com.codecoy.bahdjol.constant.Constant.TAG
import com.codecoy.bahdjol.databinding.FragmentMainBinding
import com.codecoy.bahdjol.datamodels.UserData
import com.codecoy.bahdjol.datamodels.UserResponse
import com.codecoy.bahdjol.network.ApiCall
import com.codecoy.bahdjol.repository.Repository
import com.codecoy.bahdjol.utils.GlobalClass
import com.codecoy.bahdjol.utils.ServiceIds
import com.codecoy.bahdjol.utils.isNetworkConnected
import com.codecoy.bahdjol.viewmodel.MyViewModel
import com.codecoy.bahdjol.viewmodel.MyViewModelFactory
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.zhanghai.android.materialratingbar.MaterialRatingBar
import retrofit2.Call
import retrofit2.Response


class MainFragment : Fragment(), NavigationView.OnNavigationItemSelectedListener {

    private var userData: UserData? = null
    private var userPass: String? = null
    private var deviceToken: String? = null

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


        getUserData()

        setUpNavDrawer()

        navViews()

        GlobalClass.drawer = mBinding.drawerLay
        GlobalClass.bottomNavigation = mBinding.bottomNav
        GlobalClass.frag = this

    }


    private fun setUpNavDrawer() {

        mBinding.navView.bringToFront()

        mBinding.navView.setNavigationItemSelectedListener(activity as NavigationView.OnNavigationItemSelectedListener)

    }



    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        return false
    }

    private fun getUserData() {

        userData = ServiceIds.fetchUserFromPref(activity, "userInfo")
        userPass = ServiceIds.fetchUserPasswordFromPref(activity, "userPassInfo")
        deviceToken = ServiceIds.fetchDeviceTokenFromPref(activity, "tokenInfo")

        if (userData != null) {

            userDataOnViews(userData!!)

            if (userPass != null && deviceToken != null){
                if (activity.isNetworkConnected()){
                    userSignIn(userData!!)
                }
            }


            checkConnectivity()

        }

    }

    private fun userSignIn(userData: UserData) {


        CoroutineScope(Dispatchers.IO).launch {

            val signInApi = Constant.getRetrofitInstance().create(ApiCall::class.java)
            val signInCall = signInApi.signInUser(userData.email!!, userPass!!, deviceToken!!)

            signInCall.enqueue(object : retrofit2.Callback<UserResponse> {
                override fun onResponse(
                    call: Call<UserResponse>,
                    response: Response<UserResponse>
                ) {

                    Log.i(TAG, "onResponse: userSignIn outer $response")

                    if (response.isSuccessful) {

                        Log.i(TAG, "onResponse: userSignIn inner ${response.body()}")

                        if (response.body() != null && response.body()?.status == true) {
                            val usersData = response.body()!!.data[0]

                            ServiceIds.saveUserIntoPref(activity, "userInfo", usersData)

                            this@MainFragment.userData = ServiceIds.fetchUserFromPref(activity, "userInfo")

                            CoroutineScope(Dispatchers.Main).launch {
                               userDataOnViews(this@MainFragment.userData!!)
                            }


                        } else {
                            Log.i(TAG, "onResponse: userSignIn ${response.body()?.message}")
                        }

                    } else {

                        Log.i(TAG, "onResponse: onFailure: userSignIn${response.message()}")

                    }
                }

                override fun onFailure(call: Call<UserResponse>, t: Throwable) {

                    Log.i(TAG, "onFailure: ${t.message}")

                }

            })
        }

    }

    private fun checkConnectivity() {
        if(activity.isNetworkConnected()){
            checkSubs()
            userBalance(userData!!)
        }
    }

    private fun userBalance(userData: UserData) {


        myViewModel.userBalance(userData.id!!)

        myViewModel.userBalanceLiveData.observe(activity
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

    private fun checkSubs() {

        myViewModel.checkSubscription(userData!!.id!!)

        myViewModel.checkSubsLiveData.observe(activity
        ) {

            Log.i(TAG, "response: outer ${it.data}")

            if (it.status == true && it.data != null) {

                Log.i(TAG, "response: success ${it.data!!.id}")

                val checkSubsData = it.data

                if (checkSubsData!!.status!!.toInt() == 1){

                    ServiceIds.saveSubsIntoPref(activity, "subsInfo", checkSubsData)

                } else{

                    ServiceIds.clearSubsInfo(activity, "subsInfo")

                }



            } else {

                Log.i(TAG, "checkSubs: ${it.message}")

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

        Log.i(TAG, "userDataOnViews: ${userData.userRating}")

        mBinding.navView.findViewById<MaterialRatingBar>(R.id.ageRating).rating = userData.userRating!!.toFloat()

        mBinding.navView.findViewById<TextView>(R.id.tvRatingNumber).text = "${userData.userRating} (${userData.totalAgentRateUser.toString()})"

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
            if (activity.isNetworkConnected()){
                replaceFragment(PaymentFragment())
                mBinding.drawerLay.close()
            } else {
                Toast.makeText(activity, "Connect to the internet and try again", Toast.LENGTH_SHORT).show()
                mBinding.drawerLay.close()
            }

        }
        mBinding.navView.findViewById<LinearLayout>(R.id.subsLay).setOnClickListener {
            replaceFragment(SubscriptionFragment())
            mBinding.drawerLay.close()
        }
        mBinding.navView.findViewById<LinearLayout>(R.id.contactLay).setOnClickListener {
            replaceFragment(ContactUsFragment())
            mBinding.drawerLay.close()
        }

        mBinding.navView.findViewById<LinearLayout>(R.id.logoutLay).setOnClickListener {
            logout()
            mBinding.drawerLay.close()
        }


    }

    private fun logout() {
        ServiceIds.userLogout(activity, "userInfo")

        val action = MainFragmentDirections.actionMainFragmentToStartingFragment()
        findNavController().navigate(action)
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager: FragmentManager = activity.supportFragmentManager

        if (!fragmentManager.isDestroyed){
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.frameLay, fragment)
            fragmentTransaction.commit()
        }

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity = context as MainActivity
    }

}