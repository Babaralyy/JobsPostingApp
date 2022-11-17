package com.codecoy.bahdjol.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.codecoy.bahdjol.callback.SubsCallback
import com.codecoy.bahdjol.databinding.SubscriptionLayBinding
import com.codecoy.bahdjol.datamodels.SubsData
import java.util.*

class SubsAdapter(private val context: Context,
                  private val subsDataList: MutableList<SubsData>,
                  private val subsCallback: SubsCallback
): RecyclerView.Adapter<SubsAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val mBinding = SubscriptionLayBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(mBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val subsData = subsDataList[position]

        holder.mBinding.tvMember.text = "${subsData.packageName!!.uppercase(Locale.ROOT)} MEMBERSHIP FOR ${subsData.duration} MONTH PLAN"
        holder.mBinding.tvPrice.text = subsData.packagePrice + " $"
        holder.mBinding.tvOrders.text = subsData.totalOrders + " ORDERS"

        holder.itemView.setOnClickListener {
            subsCallback.onSubsClick(position, subsData)
        }
    }

    override fun getItemCount(): Int {
     return subsDataList.size
    }

    class ViewHolder(val mBinding: SubscriptionLayBinding): RecyclerView.ViewHolder(mBinding.root)
}