package com.codecoy.bahdjol.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.codecoy.bahdjol.MainActivity
import com.codecoy.bahdjol.R
import com.codecoy.bahdjol.adapter.AllServicesAdapter
import com.codecoy.bahdjol.callback.ServicesCallback
import com.codecoy.bahdjol.constant.Constant
import com.codecoy.bahdjol.databinding.FragmentServicesBinding
import com.codecoy.bahdjol.datamodels.AllServiceData
import com.codecoy.bahdjol.datamodels.UserData
import com.codecoy.bahdjol.repository.Repository
import com.codecoy.bahdjol.roomdb.*
import com.codecoy.bahdjol.utils.ServiceIds
import com.codecoy.bahdjol.utils.ServiceIds.serviceId
import com.codecoy.bahdjol.utils.isNetworkConnected
import com.codecoy.bahdjol.viewmodel.MyViewModel
import com.codecoy.bahdjol.viewmodel.MyViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class ServicesFragment : Fragment(), ServicesCallback {

    private lateinit var myViewModel: MyViewModel
    private lateinit var roomServicesViewModel: RoomServicesViewModel

    private lateinit var allServiceDataList: MutableList<AllServiceData>
    private lateinit var roomServicesList: MutableList<Service>
    private lateinit var gridManager: GridLayoutManager
    private lateinit var allServicesAdapter: AllServicesAdapter

    private var userData: UserData? = null

    private lateinit var drawerLayout: DrawerLayout

    private lateinit var activity: MainActivity

    private lateinit var mBinding: FragmentServicesBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentServicesBinding.inflate(inflater)

        inIt()

        return mBinding.root
    }

    private fun inIt() {

        val repository = Repository()
        val myViewModelFactory = MyViewModelFactory(repository)
        myViewModel =
            ViewModelProvider(this, myViewModelFactory)[MyViewModel::class.java]

        val roomServicesRepo = RoomServicesRepo(AppDatabase(activity))
        val roomServicesFactory = RoomServicesFactory(roomServicesRepo)

        roomServicesViewModel =
            ViewModelProvider(this, roomServicesFactory)[RoomServicesViewModel::class.java]

        roomServicesList = arrayListOf()
        allServiceDataList = arrayListOf()


        gridManager = GridLayoutManager(activity, 2)
        mBinding.rvServices.layoutManager = gridManager
        mBinding.rvServices.setHasFixedSize(true)

        servicesFromRoomDb()

        getUserData()

        drawerLayout = activity.findViewById(R.id.drawerLay)

        mBinding.toolBar.setNavigationOnClickListener {
            drawerLayout.openDrawer(Gravity.LEFT)
        }

    }

    private fun servicesFromRoomDb() {

//        val dialog = Constant.getDialog(activity)
//        dialog.show()

        CoroutineScope(Dispatchers.Main).launch {
//            dialog.dismiss()
            roomServicesViewModel.getAllServices().observe(
                activity
            ) {
                if (it.isEmpty()) {
                    if (activity.isNetworkConnected()){
                        allServices()
                    }

                } else {

                    roomServicesList = it
                    setRecyclerView(roomServicesList)

                    if (activity.isNetworkConnected()){
                        allServices()
                    }
                }
            }
        }

    }

    private fun setRecyclerView(roomServicesList: MutableList<Service>) {
        allServicesAdapter = AllServicesAdapter(activity, roomServicesList, this)
        mBinding.rvServices.adapter = allServicesAdapter
        allServicesAdapter.notifyDataSetChanged()
    }

    private fun allServices() {

        myViewModel.allServices()

        myViewModel.allServicesLiveData.observe(
            activity
        ) {
            if (it.status == true && it.data.isNotEmpty()) {

                Log.i(Constant.TAG, "response: success ${it.data.size}")

                allServiceDataList = it.data

                insertServicesIntoRoom(allServiceDataList as ArrayList<AllServiceData>)

            } else {
                Toast.makeText(activity, it.message, Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun insertServicesIntoRoom(allServiceDataList: ArrayList<AllServiceData>) {
        CoroutineScope(Dispatchers.IO).launch {

            for(item in allServiceDataList){
                roomServicesViewModel.insertService(Service(item.id, item.categoryName, item.img, item.createdAt, item.updatedAt))
            }

        }

    }

    private fun getUserData() {

        userData = ServiceIds.fetchUserFromPref(activity, "userInfo")

        if (userData != null) {

            if (activity.isNetworkConnected()){
                checkSubs(userData!!)
            }

            if (ServiceIds.notId != null && ServiceIds.notId.equals("1")){
              replaceFragment(NotificationFragment())
            }

        }

    }

    private fun checkSubs(userData: UserData) {

        myViewModel.checkSubscription(userData!!.id!!)

        myViewModel.checkSubsLiveData.observe(activity
        ) {

            Log.i(Constant.TAG, "response: outer ${it.data}")

            if (it.status == true && it.data != null) {

                Log.i(Constant.TAG, "response: success ${it.data!!.id}")

                val checkSubsData = it.data

                if (checkSubsData!!.status!!.toInt() == 1){

                    ServiceIds.saveSubsIntoPref(activity, "subsInfo", checkSubsData)

                } else{

                    ServiceIds.clearSubsInfo(activity, "subsInfo")

                }


            } else {
                Log.i(Constant.TAG, "checkSubs: ${it.message}")
            }
        }

    }

    override fun onServiceClick(position: Int) {

//        val dialog = Constant.getDialog(activity)
//        dialog.show()

//        Handler(Looper.getMainLooper()).postDelayed({
            val serviceData = roomServicesList[position]
            serviceId = serviceData.id
//            dialog.dismiss()
            replaceFragment(SubServicesFragment())
//        }, 500)
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
