package com.codecoy.bahdjol.ui

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.codecoy.bahdjol.MainActivity
import com.codecoy.bahdjol.R
import com.codecoy.bahdjol.adapter.SubServiceAdapter
import com.codecoy.bahdjol.callback.ServicesCallback
import com.codecoy.bahdjol.constant.Constant
import com.codecoy.bahdjol.constant.Constant.TAG
import com.codecoy.bahdjol.databinding.FragmentSubServicesBinding
import com.codecoy.bahdjol.datamodels.SubServicesData
import com.codecoy.bahdjol.repository.Repository
import com.codecoy.bahdjol.roomdb.*
import com.codecoy.bahdjol.utils.ServiceIds
import com.codecoy.bahdjol.utils.isNetworkConnected
import com.codecoy.bahdjol.viewmodel.MyViewModel
import com.codecoy.bahdjol.viewmodel.MyViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class SubServicesFragment : Fragment(), ServicesCallback {

    private lateinit var myViewModel: MyViewModel
    private lateinit var roomServicesViewModel: RoomServicesViewModel

    private lateinit var subServicesDataList: MutableList<SubServicesData>

    private lateinit var gridManager: GridLayoutManager
    private lateinit var subServiceAdapter: SubServiceAdapter

    private lateinit var activity: MainActivity

    private lateinit var mBinding: FragmentSubServicesBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentSubServicesBinding.inflate(inflater)

        inIt()

        return mBinding.root
    }

    private fun inIt() {

        val repository = Repository()
        val myViewModelFactory = MyViewModelFactory(repository)
        myViewModel = ViewModelProvider(this, myViewModelFactory)[MyViewModel::class.java]

        val roomServicesRepo = RoomServicesRepo(AppDatabase(activity))
        val roomServicesFactory = RoomServicesFactory(roomServicesRepo)
        roomServicesViewModel =
            ViewModelProvider(this, roomServicesFactory)[RoomServicesViewModel::class.java]

        subServicesDataList = arrayListOf()

        gridManager = GridLayoutManager(activity, 2)
        mBinding.rvSubServices.layoutManager = gridManager
        mBinding.rvSubServices.setHasFixedSize(true)

        mBinding.toolBar.setNavigationOnClickListener {
            replaceFragment(ServicesFragment())
        }

        subServicesFromRoom()

    }

    private fun subServicesFromRoom() {

        CoroutineScope(Dispatchers.Main).launch {
            roomServicesViewModel.getSubService(ServiceIds.serviceId!!).observe(
                activity
            ) { subService ->
                Log.i(TAG, "subServicesFromRoom: $subService")
                Log.i(TAG, "subServicesFromRoom: ${subService?.data}")

                if(subService != null && subService.data.isNotEmpty()){

                    subServicesDataList = subService.data

                    setRecyclerView(subService.data)

                    if (activity.isNetworkConnected()){
                        subServices()
                    }

                } else {
                    if (activity.isNetworkConnected()){
                        subServices()
                    }
                }

            }
        }
    }

    private fun setRecyclerView(data: ArrayList<SubServicesData>) {
        subServiceAdapter = SubServiceAdapter(activity, data, this)
        mBinding.rvSubServices.adapter = subServiceAdapter
        subServiceAdapter.notifyDataSetChanged()
    }

    private fun subServices() {

        myViewModel.subServices(ServiceIds.serviceId!!)

        myViewModel.subServicesLiveData.observe(
            activity
        ) {

            Log.i(TAG, "response: outer ${it.data.size}")

            if (it.status == true && it.data.isNotEmpty()) {

                Log.i(TAG, "response: success ${it.data.size}")

                subServicesDataList = it.data

                insertSubServicesIntoRoom(subServicesDataList as ArrayList<SubServicesData>, ServiceIds.serviceId!!)

            } else {
                Toast.makeText(activity, it.message, Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun insertSubServicesIntoRoom(subServicesDataList: ArrayList<SubServicesData>, serviceId: Int) {
        roomServicesViewModel.insertSubService(SubService(serviceId, subServicesDataList))
    }

    override fun onServiceClick(position: Int) {

        val dialog = Constant.getDialog(activity)
        dialog.show()
        Handler(Looper.getMainLooper()).postDelayed({

            val subServiceData = subServicesDataList[position]
            ServiceIds.subServiceId = subServiceData.id
            ServiceIds.subServicePrice = subServiceData.price
            dialog.dismiss()
            replaceFragment(UserFormFragment())

        }, 500)
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