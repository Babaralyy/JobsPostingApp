package com.codecoy.bahdjol.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.codecoy.bahdjol.R
import com.codecoy.bahdjol.callback.OngoingCallback
import com.codecoy.bahdjol.callback.StatusCallBack
import com.codecoy.bahdjol.constant.Constant
import com.codecoy.bahdjol.databinding.NewReqLayBinding
import com.codecoy.bahdjol.databinding.OngoingReqLayBinding
import com.codecoy.bahdjol.datamodels.NewReqData
import com.codecoy.bahdjol.datamodels.OngoingReqData

class OngoingReqAdapter(
    private val context: Context,
    private val ongoingReqDataList: MutableList<OngoingReqData>,
    private val ongoingCallback: OngoingCallback
) : RecyclerView.Adapter<OngoingReqAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val mBinding = OngoingReqLayBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(mBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val ongoingReqData = ongoingReqDataList[position]

        Glide.with(context).load(Constant.IMG_URL + ongoingReqData.bookingImg)
            .placeholder(R.drawable.ic_downloading)
            .error(R.drawable.ic_error)
            .into(holder.mBinding.ivProduct)

        holder.mBinding.tvProductName.text = ongoingReqData.subcategoryName
        holder.mBinding.tvReqDate.text = ongoingReqData.time + " " + ongoingReqData.date
        holder.mBinding.tvOrderPrice.text = "$ " + ongoingReqData.bookingPrice
        holder.mBinding.tvOrderId.text = "#${ongoingReqData.id}"

        holder.mBinding.btnComplete.setOnClickListener {
            ongoingCallback.onCompleteClick(position, ongoingReqData)
        }

    }

    override fun getItemCount(): Int {
        return ongoingReqDataList.size
    }

    class ViewHolder(val mBinding: OngoingReqLayBinding) : RecyclerView.ViewHolder(mBinding.root)
}