package com.codecoy.bahdjol.agent

import android.app.Dialog
import android.content.ContentValues
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.codecoy.bahdjol.MainActivity
import com.codecoy.bahdjol.adapter.AgentNotificationAdapter
import com.codecoy.bahdjol.callback.NotificationCallback
import com.codecoy.bahdjol.constant.Constant
import com.codecoy.bahdjol.constant.Constant.TAG
import com.codecoy.bahdjol.databinding.FragmentAgentNotificationBinding
import com.codecoy.bahdjol.databinding.RatingDialogLayBinding
import com.codecoy.bahdjol.datamodels.AgentLoginData
import com.codecoy.bahdjol.datamodels.AgentNotificationData
import com.codecoy.bahdjol.datamodels.AgentRatingResponse
import com.codecoy.bahdjol.datamodels.NotificationData
import com.codecoy.bahdjol.network.ApiCall
import com.codecoy.bahdjol.repository.Repository
import com.codecoy.bahdjol.utils.ServiceIds
import com.codecoy.bahdjol.utils.isNetworkConnected
import com.codecoy.bahdjol.viewmodel.MyViewModel
import com.codecoy.bahdjol.viewmodel.MyViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.zhanghai.android.materialratingbar.MaterialRatingBar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class AgentNotificationFragment : Fragment(), NotificationCallback {

    private var agentLoginData: AgentLoginData? = null

    private lateinit var activity: MainActivity

    private var mRating: Float = 0.0F

    private lateinit var myViewModel: MyViewModel
    private lateinit var agentNotificationAdapter: AgentNotificationAdapter
    private lateinit var manager: LinearLayoutManager

    private lateinit var agentNotificationList: MutableList<AgentNotificationData>


    private lateinit var mBinding: FragmentAgentNotificationBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentAgentNotificationBinding.inflate(inflater)

        inIt()

        return mBinding.root
    }

    private fun inIt() {

        ServiceIds.notId = null

        val repository = Repository()
        val myViewModelFactory = MyViewModelFactory(repository)
        myViewModel =
            ViewModelProvider(this, myViewModelFactory)[MyViewModel::class.java]

        agentNotificationList = arrayListOf()

        mBinding.rvNotification.setHasFixedSize(true)
        manager = LinearLayoutManager(activity)
        mBinding.rvNotification.layoutManager = manager


        mBinding.toolBar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        getAgent()

    }

    private fun getAgent() {
        agentLoginData = ServiceIds.fetchAgentFromPref(requireContext(), "agentInfo")

        if (agentLoginData != null) {
            checkConnectivity()
        }
    }

    private fun checkConnectivity() {

        if (activity.isNetworkConnected()) {
            agentNotifications()
            mBinding.layNotConnected.visibility = View.GONE
        } else {
            mBinding.layNotConnected.visibility = View.VISIBLE
        }

    }

    private fun agentNotifications() {
        val dialog = Constant.getDialog(activity)
        dialog.show()

        myViewModel.agentNotifications(agentLoginData?.id!!)

        myViewModel.agentNotificationLiveData.observe(
            activity
        ) {
            dialog.dismiss()
            if (it.status == true && it.data.isNotEmpty()) {

                Log.i(Constant.TAG, "response: add success ${it.data}")

                agentNotificationList = it.data

                agentNotificationList.reverse()

                agentNotificationAdapter =
                    AgentNotificationAdapter(activity, agentNotificationList, this)
                mBinding.rvNotification.adapter = agentNotificationAdapter

            } else {
                mBinding.tvNotFound.visibility = View.VISIBLE
                Toast.makeText(activity, it.message, Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity = context as MainActivity
    }

    override fun onNotificationClick(position: Int, notificationData: NotificationData) {

    }

    override fun onAgentNotificationClick(position: Int, notificationData: AgentNotificationData) {
        showRatingDialog(notificationData)
    }

    private fun showRatingDialog(notificationData: AgentNotificationData) {

        val ratingDialogLayBinding: RatingDialogLayBinding =
            RatingDialogLayBinding.inflate(LayoutInflater.from(requireContext()))

        val dialog = Dialog(activity)
        dialog.setContentView(ratingDialogLayBinding.root)
        dialog.setCancelable(true)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

//        val width = (resources.displayMetrics.widthPixels * 0.90).toInt()
//        val height = (resources.displayMetrics.heightPixels * 0.60).toInt()
//
//        dialog.window?.setLayout(width, height)
//
//        val lp = WindowManager.LayoutParams()
//        lp.copyFrom(dialog.window!!.attributes)
//        lp.width = WindowManager.LayoutParams.MATCH_PARENT
//        dialog.window!!.attributes = lp

        ratingDialogLayBinding.btnSave.setOnClickListener {

            val aDes: String = ratingDialogLayBinding.etDes.text.toString().trim()

            if (mRating <= 0.0) {
                Toast.makeText(activity, "Rating can not be empty!", Toast.LENGTH_SHORT).show()

            } else if (aDes.isEmpty()) {
                Toast.makeText(activity, "Description can not be empty!", Toast.LENGTH_SHORT).show()
            } else {
                giveRating(notificationData, aDes)
                dialog.dismiss()
            }
        }

        ratingDialogLayBinding.aRating.onRatingChangeListener =
            MaterialRatingBar.OnRatingChangeListener { _, rating ->

                mRating = rating

                Log.i(ContentValues.TAG, "showRatingDialog: $rating")

            }


        dialog.show()

    }

    private fun giveRating(notificationData: AgentNotificationData, aDes: String) {

        val dialog = Constant.getDialog(activity)
        dialog.show()

        CoroutineScope(Dispatchers.IO).launch {

            val ratingApi = Constant.getRetrofitInstance().create(ApiCall::class.java)
            val ratingCall = ratingApi.rateToUser(
                notificationData.agentId!!.toInt(),
                notificationData.userId!!.toInt(),
                mRating.toString(),
                aDes,
                notificationData.bookId!!.toInt()
            )

            ratingCall.enqueue(object : Callback<AgentRatingResponse> {
                override fun onResponse(
                    call: Call<AgentRatingResponse>,
                    response: Response<AgentRatingResponse>
                ) {

                    Log.i(TAG, "onResponse: giveRating outer ${response.body()?.message}")

                    if (response.isSuccessful) {
                        dialog.dismiss()

                        Log.i(TAG, "onResponse: giveRating successful ${response.body()}")

                        if (response.body() != null && response.body()?.status == true) {

                            Toast.makeText(
                                activity,
                                response.body()!!.message,
                                Toast.LENGTH_SHORT
                            ).show()

                            findNavController().popBackStack()
                        }

                    } else {
                        dialog.dismiss()
                        Toast.makeText(
                            activity,
                            response.body()?.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }

                override fun onFailure(call: Call<AgentRatingResponse>, t: Throwable) {
                    dialog.dismiss()
                    Log.i(TAG, "onResponse: giveRating successful ${t.message}")
                    Toast.makeText(activity, t.message, Toast.LENGTH_SHORT).show()
                }

            })

        }

    }

}