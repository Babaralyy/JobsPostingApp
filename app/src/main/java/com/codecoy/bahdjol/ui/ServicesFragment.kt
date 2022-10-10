package com.codecoy.bahdjol.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import androidx.recyclerview.widget.GridLayoutManager
import com.codecoy.bahdjol.R
import com.codecoy.bahdjol.adapter.AllServicesAdapter
import com.codecoy.bahdjol.callback.ServicesCallback
import com.codecoy.bahdjol.constant.Constant
import com.codecoy.bahdjol.databinding.FragmentServicesBinding
import com.codecoy.bahdjol.datamodels.AllServiceData
import com.codecoy.bahdjol.repository.Repository
import com.codecoy.bahdjol.utils.ServiceIds.serviceId
import com.codecoy.bahdjol.viewmodel.MyViewModel
import com.codecoy.bahdjol.viewmodel.MyViewModelFactory


class ServicesFragment : Fragment(), ServicesCallback {

    private lateinit var myViewModel: MyViewModel

    private lateinit var allServiceDataList: MutableList<AllServiceData>
    private lateinit var gridManager: GridLayoutManager
    private lateinit var allServicesAdapter: AllServicesAdapter

    private lateinit var drawerLayout: DrawerLayout

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

        gridManager = GridLayoutManager(requireActivity(), 2)
        mBinding.rvServices.layoutManager = gridManager
        mBinding.rvServices.setHasFixedSize(true)

        allServices()

        drawerLayout = requireActivity().findViewById(R.id.drawerLay)

        mBinding.toolBar.setNavigationOnClickListener {
                drawerLayout.openDrawer(Gravity.LEFT)
        }

    }

    private fun allServices() {
        val dialog = Constant.getDialog(requireActivity())
        dialog.show()

        myViewModel.allServices()

        myViewModel.allServicesLiveData.observe(viewLifecycleOwner
        ) {
            dialog.dismiss()
            if (it.status == true && it.data.isNotEmpty()) {

                Log.i(Constant.TAG, "response: success ${it.data.size}")

                allServiceDataList = it.data
                allServicesAdapter = AllServicesAdapter(requireActivity(), allServiceDataList, this)
                mBinding.rvServices.adapter = allServicesAdapter
                allServicesAdapter.notifyDataSetChanged()


            } else {
                Toast.makeText(requireActivity(), it.message, Toast.LENGTH_SHORT).show()
            }

        }
    }

    override fun onServiceClick(position: Int) {

        val dialog = Constant.getDialog(requireActivity())
        dialog.show()

        Handler(Looper.getMainLooper()).postDelayed({
            val serviceData = allServiceDataList[position]
            serviceId = serviceData.id
            dialog.dismiss()
            replaceFragment(SubServicesFragment())
        }, 500)
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLay, fragment)
        fragmentTransaction.commit()
    }
}
