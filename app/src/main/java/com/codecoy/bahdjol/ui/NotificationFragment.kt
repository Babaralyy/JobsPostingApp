package com.codecoy.bahdjol.ui

import android.app.Dialog
import android.content.ContentValues.TAG
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
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.codecoy.bahdjol.MainActivity
import com.codecoy.bahdjol.R
import com.codecoy.bahdjol.adapter.NotificationAdapter
import com.codecoy.bahdjol.callback.NotificationCallback
import com.codecoy.bahdjol.constant.Constant
import com.codecoy.bahdjol.databinding.FragmentNotificationBinding
import com.codecoy.bahdjol.databinding.RatingDialogLayBinding
import com.codecoy.bahdjol.datamodels.AgentNotificationData
import com.codecoy.bahdjol.datamodels.AgentRatingResponse
import com.codecoy.bahdjol.datamodels.NotificationData
import com.codecoy.bahdjol.datamodels.UserData
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


class NotificationFragment : Fragment(), NotificationCallback {

    private var userData: UserData? = null

    private lateinit var activity: MainActivity

    private lateinit var myViewModel: MyViewModel
    private lateinit var notificationAdapter: NotificationAdapter
    private lateinit var manager: LinearLayoutManager
    private lateinit var notificationList: MutableList<NotificationData>

    private var mRating : Float = 0.0F

    private lateinit var mBinding: FragmentNotificationBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentNotificationBinding.inflate(inflater)

        inIt()

        return mBinding.root
    }

    private fun inIt() {

        ServiceIds.notId = null

        val repository = Repository()
        val myViewModelFactory = MyViewModelFactory(repository)
        myViewModel =
            ViewModelProvider(this, myViewModelFactory)[MyViewModel::class.java]

        notificationList = arrayListOf()

        mBinding.rvNotification.setHasFixedSize(true)
        manager = LinearLayoutManager(activity)
        mBinding.rvNotification.layoutManager = manager

        getUserData()

        mBinding.tvRetry.setOnClickListener {
            checkConnectivity()
        }

        mBinding.toolBar.setNavigationOnClickListener {
            replaceFragment(ServicesFragment())
        }

    }

    private fun getUserData() {
        userData = ServiceIds.fetchUserFromPref(activity, "userInfo")

        if (userData != null){
            checkConnectivity()
        }

    }

    private fun checkConnectivity() {

        if (activity.isNetworkConnected()){
            getNotifications()
            mBinding.layNotConnected.visibility = View.GONE
        } else{
            mBinding.layNotConnected.visibility = View.VISIBLE
        }

    }

    private fun getNotifications() {

        val dialog = Constant.getDialog(activity)
        dialog.show()

        myViewModel.userNotifications(userData?.id!!)

        myViewModel.notificationLiveData.observe(activity
        ) {
            dialog.dismiss()
            if (it.status == true && it.data.isNotEmpty()) {

                Log.i(Constant.TAG, "response: add success ${it.data}")

                notificationList = it.data

                notificationList.reverse()

                notificationAdapter  = NotificationAdapter(activity, notificationList, this)
                mBinding.rvNotification.adapter = notificationAdapter

            } else{
                mBinding.tvNotFound.visibility = View.VISIBLE
                Toast.makeText(activity, it.message, Toast.LENGTH_SHORT).show()
            }
        }

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

    override fun onNotificationClick(position: Int, notificationData: NotificationData) {
      showRatingDialog(notificationData)
    }

    override fun onAgentNotificationClick(position: Int, notificationData: AgentNotificationData) {

    }

    private fun showRatingDialog(notificationData: NotificationData) {
        val ratingDialogLayBinding: RatingDialogLayBinding =
            RatingDialogLayBinding.inflate(LayoutInflater.from(requireContext()))

        val dialog = Dialog(activity)
        dialog.setContentView(ratingDialogLayBinding.root)
        dialog.setCancelable(true)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

//        val width = (resources.displayMetrics.widthPixels * 0.90).toInt()
//        val height = (resources.displayMetrics.heightPixels * 0.60).toInt()
//
//        dialog.window?.setLayout(width, height)
//
//        val lp = WindowManager.LayoutParams()
//        lp.copyFrom(dialog.window!!.attributes)
//        lp.width = WindowManager.LayoutParams.MATCH_PARENT
//        dialog.window!!.attributes = lp

        ratingDialogLayBinding.tvRateName.text = notificationData.agentName
        
        ratingDialogLayBinding.btnSave.setOnClickListener {

            val aDes: String = ratingDialogLayBinding.etDes.text.toString().trim()

            if (mRating <= 0.0){
                Toast.makeText(activity, "Rating can not be empty!", Toast.LENGTH_SHORT).show()

            }else if (aDes.isEmpty()){
                Toast.makeText(activity, "Description can not be empty!", Toast.LENGTH_SHORT).show()
            }
            else  {
                giveRating(notificationData, aDes)
                dialog.dismiss()
            }
        }

        ratingDialogLayBinding.aRating.onRatingChangeListener =
            MaterialRatingBar.OnRatingChangeListener { _, rating ->

                mRating = rating

                Log.i(TAG, "showRatingDialog: $rating")
                
            }
        

        dialog.show()
    }

    private fun giveRating(notificationData: NotificationData, aDes: String) {

        val dialog = Constant.getDialog(activity)
        dialog.show()

        CoroutineScope(Dispatchers.IO).launch {

            val ratingApi = Constant.getRetrofitInstance().create(ApiCall::class.java)
            val ratingCall = ratingApi.rateToAgent(notificationData.userId!!.toInt(), notificationData.agentId!!.toInt(), mRating.toString(), aDes, notificationData.bookId!!.toInt())

            ratingCall.enqueue(object : Callback<AgentRatingResponse>{
                override fun onResponse(
                    call: Call<AgentRatingResponse>,
                    response: Response<AgentRatingResponse>
                ) {

                    Log.i(TAG, "onResponse: outer $response")

                    if (response.isSuccessful) {
                        dialog.dismiss()

                        Log.i(TAG, "onResponse: successful $response")

                        if (response.body() != null && response.body()?.status == true) {

                            replaceFragment(ServicesFragment())

                            Toast.makeText(
                                activity,
                                response.body()!!.message,
                                Toast.LENGTH_SHORT
                            ).show()
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
                    Toast.makeText(activity, t.message, Toast.LENGTH_SHORT).show()
                }

            })

        }

    }



}