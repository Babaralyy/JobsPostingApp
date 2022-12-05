package com.codecoy.bahdjol.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.codecoy.bahdjol.R
import com.codecoy.bahdjol.constant.Constant
import com.codecoy.bahdjol.databinding.OngoingReqLayBinding
import com.codecoy.bahdjol.datamodels.HistoryReqData

class HistoryReqAdapter(
    private val context: Context,
    private val historyReqDataList: MutableList<HistoryReqData>
) : RecyclerView.Adapter<HistoryReqAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val mBinding = OngoingReqLayBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(mBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val historyReqData = historyReqDataList[position]

        Glide.with(context).load(Constant.IMG_URL + historyReqData.bookingImg)
            .placeholder(R.drawable.ic_downloading)
            .error(R.drawable.ic_error)
            .into(holder.mBinding.ivProduct)

        holder.mBinding.tvProductName.text = historyReqData.subcategoryName
        holder.mBinding.tvReqDate.text = historyReqData.time + " " + historyReqData.date
        holder.mBinding.tvOrderPrice.text = "$ " + historyReqData.bookingPrice
        holder.mBinding.tvOrderId.text = "#${historyReqData.id}"

        if(historyReqData.status!!.toInt() == 0){
            holder.mBinding.tvReqStatus.text = "Rejected"
        }
        if(historyReqData.status!!.toInt() == 1){
            holder.mBinding.tvReqStatus.text = "Accepted"
        }
        if(historyReqData.status!!.toInt() == 2){
            holder.mBinding.tvReqStatus.text = "Completed"
        }

        holder.mBinding.btnComplete.visibility = View.GONE
    }

    override fun getItemCount(): Int {
        return historyReqDataList.size
    }

    class ViewHolder(val mBinding: OngoingReqLayBinding) : RecyclerView.ViewHolder(mBinding.root)

}