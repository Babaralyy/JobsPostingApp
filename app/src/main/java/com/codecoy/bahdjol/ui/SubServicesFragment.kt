package com.codecoy.bahdjol.ui

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
import com.codecoy.bahdjol.R
import com.codecoy.bahdjol.adapter.SubServiceAdapter
import com.codecoy.bahdjol.callback.ServicesCallback
import com.codecoy.bahdjol.constant.Constant
import com.codecoy.bahdjol.databinding.FragmentSubServicesBinding
import com.codecoy.bahdjol.datamodels.SubServicesData
import com.codecoy.bahdjol.repository.Repository
import com.codecoy.bahdjol.utils.ServiceIds
import com.codecoy.bahdjol.viewmodel.MyViewModel
import com.codecoy.bahdjol.viewmodel.MyViewModelFactory


class SubServicesFragment : Fragment(), ServicesCallback {

    private lateinit var myViewModel: MyViewModel
    private lateinit var subServicesDataList: MutableList<SubServicesData>

    private lateinit var gridManager: GridLayoutManager
    private lateinit var subServiceAdapter: SubServiceAdapter

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

        subServicesDataList = arrayListOf()

        gridManager = GridLayoutManager(requireActivity(), 2)
        mBinding.rvSubServices.layoutManager = gridManager
        mBinding.rvSubServices.setHasFixedSize(true)

        subServices()

    }

    private fun subServices() {
        val dialog = Constant.getDialog(requireActivity())
        dialog.show()

        myViewModel.subServices(ServiceIds.serviceId!!)

        myViewModel.subServicesLiveData.observe(
            viewLifecycleOwner
        ) {
            dialog.dismiss()

            Log.i(Constant.TAG, "response: outer ${it.data.size}")

            if (it.status == true && it.data.isNotEmpty()) {

                Log.i(Constant.TAG, "response: success ${it.data.size}")

                subServicesDataList = it.data

                subServiceAdapter = SubServiceAdapter(requireActivity(), subServicesDataList, this)
                mBinding.rvSubServices.adapter = subServiceAdapter

            } else {
                Toast.makeText(requireActivity(), it.message, Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun onServiceClick(position: Int) {

        val dialog = Constant.getDialog(requireActivity())
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
        val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLay, fragment)
        fragmentTransaction.commit()
    }

}