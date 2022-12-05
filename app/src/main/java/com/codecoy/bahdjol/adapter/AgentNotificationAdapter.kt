package com.codecoy.bahdjol.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.codecoy.bahdjol.callback.NotificationCallback
import com.codecoy.bahdjol.databinding.NotificationLayBinding
import com.codecoy.bahdjol.datamodels.AgentNotificationData
import com.codecoy.bahdjol.datamodels.NotificationData

class AgentNotificationAdapter(
    private val context: Context,
    private val agentNotificationList: MutableList<AgentNotificationData>,
    private val notificationCallback: NotificationCallback
): RecyclerView.Adapter<AgentNotificationAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val mBinding = NotificationLayBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(mBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val notificationData = agentNotificationList[position]

        holder.mBinding.tvTitle.text = notificationData.title
        holder.mBinding.tvDes.text = notificationData.description

        if (notificationData.status?.toInt() == 1){
            holder.mBinding.giveRating.visibility = View.VISIBLE
        }

        holder.mBinding.giveRating.setOnClickListener {
            notificationCallback.onAgentNotificationClick(position, notificationData)
        }
    }

    override fun getItemCount(): Int {
        return agentNotificationList.size
    }

    class ViewHolder(val mBinding: NotificationLayBinding): RecyclerView.ViewHolder (mBinding.root)
}