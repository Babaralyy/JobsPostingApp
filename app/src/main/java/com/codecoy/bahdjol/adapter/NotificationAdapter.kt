package com.codecoy.bahdjol.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.codecoy.bahdjol.callback.NotificationCallback
import com.codecoy.bahdjol.callback.OngoingCallback
import com.codecoy.bahdjol.databinding.NotificationLayBinding
import com.codecoy.bahdjol.datamodels.NotificationData

class NotificationAdapter(private val context: Context,
                          private val notificationList: MutableList<NotificationData>,
                          private val notificationCallback: NotificationCallback
): RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val mBinding = NotificationLayBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(mBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val notificationData = notificationList[position]

        holder.mBinding.tvTitle.text = notificationData.title
        holder.mBinding.tvDes.text = notificationData.description

        holder.itemView.setOnClickListener {
            notificationCallback.onNotificationClick(position)
        }
    }

    override fun getItemCount(): Int {
        return notificationList.size
    }

    class ViewHolder(val mBinding: NotificationLayBinding): RecyclerView.ViewHolder(mBinding.root)
}