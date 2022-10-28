package com.codecoy.bahdjol.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.codecoy.bahdjol.R
import com.codecoy.bahdjol.adapter.HistoryAdapter
import com.codecoy.bahdjol.callback.HistoryCallback
import com.codecoy.bahdjol.constant.Constant
import com.codecoy.bahdjol.constant.Constant.TAG
import com.codecoy.bahdjol.databinding.FragmentHistoryBinding
import com.codecoy.bahdjol.datamodels.BookingHistoryData
import com.codecoy.bahdjol.repository.Repository
import com.codecoy.bahdjol.utils.ServiceIds
import com.codecoy.bahdjol.viewmodel.MyViewModel
import com.codecoy.bahdjol.viewmodel.MyViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetDialog


class HistoryFragment : Fragment(), HistoryCallback {

    private lateinit var myViewModel: MyViewModel

    private lateinit var bookingHistoryList: MutableList<BookingHistoryData>

    private lateinit var pendingHistoryList: MutableList<BookingHistoryData>
    private lateinit var confirmedHistoryList: MutableList<BookingHistoryData>
    private lateinit var cancelledHistoryList: MutableList<BookingHistoryData>
    private lateinit var completedHistoryList: MutableList<BookingHistoryData>

    private lateinit var historyAdapter: HistoryAdapter

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


        bookingHistoryList  = arrayListOf()
        pendingHistoryList = arrayListOf()
        confirmedHistoryList = arrayListOf()
        cancelledHistoryList = arrayListOf()
        completedHistoryList = arrayListOf()


        mBinding.rvHistory.setHasFixedSize(true)
        mBinding.rvHistory.layoutManager = LinearLayoutManager(requireActivity())

        mBinding.tvAll.isSelected = true
        getBookingHistory()

        mBinding.tvAll.setOnClickListener {
            mBinding.tvAll.isSelected = true
            mBinding.tvConfirm.isSelected = false
            mBinding.tvPending.isSelected = false
            mBinding.tvCancel.isSelected = false
            mBinding.tvComplete.isSelected = false

            getBookingHistory()
        }

        mBinding.tvConfirm.setOnClickListener {
            mBinding.tvAll.isSelected = false
            mBinding.tvConfirm.isSelected = true
            mBinding.tvPending.isSelected = false
            mBinding.tvCancel.isSelected = false
            mBinding.tvComplete.isSelected = false

            this.confirmedHistoryList = bookingHistoryList.filter {
                it.status == "1"
            } as MutableList<BookingHistoryData>

            Log.i(TAG, "inIt: ${confirmedHistoryList.size}")


            if(confirmedHistoryList.isNotEmpty()){
                historyAdapter = HistoryAdapter(requireActivity(), confirmedHistoryList, this)
                mBinding.rvHistory.adapter = historyAdapter
            } else {
                mBinding.tvNotFound.visibility = View.VISIBLE
            }

        }
        mBinding.tvPending.setOnClickListener {
            mBinding.tvAll.isSelected = false
            mBinding.tvConfirm.isSelected = false
            mBinding.tvPending.isSelected = true
            mBinding.tvCancel.isSelected = false
            mBinding.tvComplete.isSelected = false

            this.pendingHistoryList = bookingHistoryList.filter {
                it.status == "0"
            } as MutableList<BookingHistoryData>

            Log.i(TAG, "inIt: ${pendingHistoryList.size}")

            if(pendingHistoryList.isNotEmpty()){
                historyAdapter = HistoryAdapter(requireActivity(), pendingHistoryList, this)
                mBinding.rvHistory.adapter = historyAdapter
            } else {
                mBinding.tvNotFound.visibility = View.VISIBLE
            }

        }
        mBinding.tvCancel.setOnClickListener {
            mBinding.tvAll.isSelected = false
            mBinding.tvConfirm.isSelected = false
            mBinding.tvPending.isSelected = false
            mBinding.tvCancel.isSelected = true
            mBinding.tvComplete.isSelected = false

            this.cancelledHistoryList = bookingHistoryList.filter {
                it.status == "2"
            } as MutableList<BookingHistoryData>

            Log.i(TAG, "inIt: ${cancelledHistoryList.size}")

            if(cancelledHistoryList.isNotEmpty()){
                historyAdapter = HistoryAdapter(requireActivity(), cancelledHistoryList, this)
                mBinding.rvHistory.adapter = historyAdapter
            } else {
                mBinding.tvNotFound.visibility = View.VISIBLE
            }

        }
        mBinding.tvComplete.setOnClickListener {
            mBinding.tvAll.isSelected = false
            mBinding.tvConfirm.isSelected = false
            mBinding.tvPending.isSelected = false
            mBinding.tvCancel.isSelected = false
            mBinding.tvComplete.isSelected = true

            this.completedHistoryList = bookingHistoryList.filter {
                it.status == "3"
            } as MutableList<BookingHistoryData>

            Log.i(TAG, "inIt: ${completedHistoryList.size}")

            if(completedHistoryList.isNotEmpty()){
                historyAdapter = HistoryAdapter(requireActivity(), completedHistoryList, this)
                mBinding.rvHistory.adapter = historyAdapter
            } else {
                mBinding.tvNotFound.visibility = View.VISIBLE
            }

        }

    }

    private fun getBookingHistory() {

        val dialog = Constant.getDialog(requireActivity())
        dialog.show()

        myViewModel.bookingHistory(ServiceIds.userId!!)

        myViewModel.bookingHistoryLiveData.observe(viewLifecycleOwner
        ) {

            dialog.dismiss()

            Log.i(TAG, "response: outer ${it.data.size}")

            if (it.status == true && it.data.isNotEmpty()) {

                Log.i(TAG, "response: success ${it.data.size}")

                bookingHistoryList = it.data

                historyAdapter = HistoryAdapter(requireActivity(), bookingHistoryList, this)
                mBinding.rvHistory.adapter = historyAdapter

            } else {
                Toast.makeText(requireActivity(), it.message, Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun onHistoryClick(position: Int, bookingHistoryData: BookingHistoryData) {

        showBottomDialog(bookingHistoryData)

    }

    private fun showBottomDialog(bookingHistoryData: BookingHistoryData) {

        val bottomDialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialog)
        bottomDialog.setContentView(R.layout.bottom_dialog_lay)
        bottomDialog.show()

        Toast.makeText(requireActivity(), bookingHistoryData.bookingDesc, Toast.LENGTH_SHORT).show()

    }


}