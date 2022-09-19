package com.codecoy.bahdjol.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.codecoy.bahdjol.R
import com.codecoy.bahdjol.constant.Constant
import com.codecoy.bahdjol.databinding.FragmentHistoryBinding
import com.codecoy.bahdjol.datamodels.BookingHistoryResponse
import com.codecoy.bahdjol.datamodels.SubServicesData
import com.codecoy.bahdjol.repository.Repository
import com.codecoy.bahdjol.viewmodel.MyViewModel
import com.codecoy.bahdjol.viewmodel.MyViewModelFactory


class HistoryFragment : Fragment() {

    private lateinit var myViewModel: MyViewModel

    private lateinit var mBinding: FragmentHistoryBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentHistoryBinding.inflate(inflater)

        inIt()

        return mBinding.root
    }

    private fun inIt() {

        val repository = Repository()
        val myViewModelFactory = MyViewModelFactory(repository)
        myViewModel = ViewModelProvider(this, myViewModelFactory)[MyViewModel::class.java]

        getBookingHistory()

    }

    private fun getBookingHistory() {

        val dialog = Constant.getDialog(requireActivity())
        dialog.show()

        myViewModel.bookingHistory(8)

        myViewModel.bookingHistoryLiveData.observe(viewLifecycleOwner
        ) {

            dialog.dismiss()

            Log.i(Constant.TAG, "response: outer ${it.data.size}")

            if (it.status == true && it.data.isNotEmpty()) {

                Log.i(Constant.TAG, "response: success ${it.data.size}")

                Toast.makeText(activity, it.data.size.toString(), Toast.LENGTH_SHORT).show()

            } else {
                Toast.makeText(requireActivity(), it.message, Toast.LENGTH_SHORT).show()
            }
        }

    }
}