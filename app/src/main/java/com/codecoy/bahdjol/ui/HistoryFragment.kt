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
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.codecoy.bahdjol.MainActivity
import com.codecoy.bahdjol.R
import com.codecoy.bahdjol.adapter.HistoryAdapter
import com.codecoy.bahdjol.callback.HistoryCallback
import com.codecoy.bahdjol.constant.Constant
import com.codecoy.bahdjol.constant.Constant.TAG
import com.codecoy.bahdjol.databinding.BottomDialogLayBinding
import com.codecoy.bahdjol.databinding.DatePickerLayoutBinding
import com.codecoy.bahdjol.databinding.FragmentHistoryBinding
import com.codecoy.bahdjol.datamodels.BookingHistoryData
import com.codecoy.bahdjol.repository.Repository
import com.codecoy.bahdjol.utils.ServiceIds
import com.codecoy.bahdjol.utils.isNetworkConnected
import com.codecoy.bahdjol.viewmodel.MyViewModel
import com.codecoy.bahdjol.viewmodel.MyViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetDialog


class HistoryFragment : Fragment(), HistoryCallback {

    private lateinit var activity: MainActivity

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
        mBinding.rvHistory.layoutManager = LinearLayoutManager(activity)

        mBinding.tvAll.isSelected = true
        checkConnectivity()

        mBinding.tvAll.setOnClickListener {
            mBinding.tvAll.isSelected = true
            mBinding.tvConfirm.isSelected = false
            mBinding.tvPending.isSelected = false
            mBinding.tvCancel.isSelected = false
            mBinding.tvComplete.isSelected = false

            mBinding.tvNotFound.visibility = View.GONE

           checkConnectivity()
        }

        mBinding.tvConfirm.setOnClickListener {

            mBinding.tvAll.isSelected = false
            mBinding.tvConfirm.isSelected = true
            mBinding.tvPending.isSelected = false
            mBinding.tvCancel.isSelected = false
            mBinding.tvComplete.isSelected = false

            mBinding.layNotConnected.visibility = View.GONE

            this.confirmedHistoryList = bookingHistoryList.filter {
                it.status == "1"
            } as MutableList<BookingHistoryData>

            Log.i(TAG, "inIt: ${confirmedHistoryList.size}")


            if (confirmedHistoryList.isNotEmpty()) {

                mBinding.tvNotFound.visibility = View.GONE
                historyAdapter = HistoryAdapter(activity, confirmedHistoryList, this)
                mBinding.rvHistory.adapter = historyAdapter

            } else {

                confirmedHistoryList.clear()

                historyAdapter = HistoryAdapter(activity, confirmedHistoryList, this)
                mBinding.rvHistory.adapter = historyAdapter

                mBinding.tvNotFound.visibility = View.VISIBLE
            }

        }
        mBinding.tvPending.setOnClickListener {
            mBinding.tvAll.isSelected = false
            mBinding.tvConfirm.isSelected = false
            mBinding.tvPending.isSelected = true
            mBinding.tvCancel.isSelected = false
            mBinding.tvComplete.isSelected = false

            mBinding.layNotConnected.visibility = View.GONE

            this.pendingHistoryList = bookingHistoryList.filter {
                it.status == "0"
            } as MutableList<BookingHistoryData>

            Log.i(TAG, "inIt: ${pendingHistoryList.size}")

            if (pendingHistoryList.isNotEmpty()) {

                mBinding.tvNotFound.visibility = View.GONE
                historyAdapter = HistoryAdapter(activity, pendingHistoryList, this)
                mBinding.rvHistory.adapter = historyAdapter

            } else {

                pendingHistoryList.clear()

                historyAdapter = HistoryAdapter(activity, pendingHistoryList, this)
                mBinding.rvHistory.adapter = historyAdapter

                mBinding.tvNotFound.visibility = View.VISIBLE
            }

        }
        mBinding.tvCancel.setOnClickListener {
            mBinding.tvAll.isSelected = false
            mBinding.tvConfirm.isSelected = false
            mBinding.tvPending.isSelected = false
            mBinding.tvCancel.isSelected = true
            mBinding.tvComplete.isSelected = false

            mBinding.layNotConnected.visibility = View.GONE

            this.cancelledHistoryList = bookingHistoryList.filter {
                it.status == "2"
            } as MutableList<BookingHistoryData>

            Log.i(TAG, "inIt: ${cancelledHistoryList.size}")

            if (cancelledHistoryList.isNotEmpty()) {

                mBinding.tvNotFound.visibility = View.GONE
                historyAdapter = HistoryAdapter(activity, cancelledHistoryList, this)
                mBinding.rvHistory.adapter = historyAdapter

            } else {

                cancelledHistoryList.clear()

                historyAdapter = HistoryAdapter(activity, cancelledHistoryList, this)
                mBinding.rvHistory.adapter = historyAdapter

                mBinding.tvNotFound.visibility = View.VISIBLE
            }

        }
        mBinding.tvComplete.setOnClickListener {
            mBinding.tvAll.isSelected = false
            mBinding.tvConfirm.isSelected = false
            mBinding.tvPending.isSelected = false
            mBinding.tvCancel.isSelected = false
            mBinding.tvComplete.isSelected = true

            mBinding.layNotConnected.visibility = View.GONE

            this.completedHistoryList = bookingHistoryList.filter {
                it.status == "3"
            } as MutableList<BookingHistoryData>

            Log.i(TAG, "inIt: ${completedHistoryList.size}")

            if (completedHistoryList.isNotEmpty()) {

                mBinding.tvNotFound.visibility = View.GONE
                historyAdapter = HistoryAdapter(activity, completedHistoryList, this)
                mBinding.rvHistory.adapter = historyAdapter

            } else {

                completedHistoryList.clear()
                historyAdapter = HistoryAdapter(activity, completedHistoryList, this)
                mBinding.rvHistory.adapter = historyAdapter

                mBinding.tvNotFound.visibility = View.VISIBLE
            }

        }

        mBinding.tvRetry.setOnClickListener {
            checkConnectivity()
        }


        mBinding.toolBar.setNavigationOnClickListener {
            replaceFragment(ServicesFragment())
        }

    }


    private fun checkConnectivity() {

        if (activity.isNetworkConnected()){
            getBookingHistory()
            mBinding.layNotConnected.visibility = View.GONE
        } else{
            mBinding.layNotConnected.visibility = View.VISIBLE
        }

    }

    private fun getBookingHistory() {

        val dialog = Constant.getDialog(activity)
        dialog.show()

        myViewModel.bookingHistory(ServiceIds.userId!!)

        myViewModel.bookingHistoryLiveData.observe(activity
        ) {

            dialog.dismiss()

            Log.i(TAG, "response: outer ${it.data.size}")

            if (it.status == true && it.data.isNotEmpty()) {

                Log.i(TAG, "response: success ${it.data.size}")

                mBinding.tvNotFound.visibility = View.GONE
                bookingHistoryList = it.data

                historyAdapter = HistoryAdapter(activity, bookingHistoryList, this)
                mBinding.rvHistory.adapter = historyAdapter

            } else {
                mBinding.tvNotFound.visibility = View.VISIBLE
                Toast.makeText(activity, it.message, Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun onHistoryClick(position: Int, bookingHistoryData: BookingHistoryData) {

        val dialog = Constant.getDialog(activity)
        dialog.show()
        Handler(Looper.getMainLooper()).postDelayed({

            showBottomDialog(bookingHistoryData)
            dialog.dismiss()

        }, 500)


    }

    private fun showBottomDialog(bookingHistoryData: BookingHistoryData) {

        val bottomBinding: BottomDialogLayBinding =
            BottomDialogLayBinding.inflate(LayoutInflater.from(requireContext()))

        val bottomDialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialog)
        bottomDialog.setContentView(bottomBinding.root)

        Glide.with(activity).load(Constant.IMG_URL + bookingHistoryData.bookingImage)
            .placeholder(R.drawable.ic_downloading)
            .error(R.drawable.ic_error)
            .into(bottomBinding.ivProduct)

        Log.i(TAG, "showBottomDialog: ${Constant.IMG_URL + bookingHistoryData.bookingImage}")

        if(bookingHistoryData.status!!.toInt() == 0){
            bottomBinding.btnConfirm.text = "Pending"
        }
        if(bookingHistoryData.status!!.toInt() == 1){
            bottomBinding.btnConfirm.text = "Confirmed"
        }
        if(bookingHistoryData.status!!.toInt() == 2){
            bottomBinding.btnConfirm.text = "Cancelled"
        }
        if(bookingHistoryData.status!!.toInt() == 3){
            bottomBinding.btnConfirm.text = "Completed"
        }


        bottomBinding.tvDetails.text = bookingHistoryData.bookingDesc

        bottomDialog.show()



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