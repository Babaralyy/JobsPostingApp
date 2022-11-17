package com.codecoy.bahdjol.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.codecoy.bahdjol.R
import com.codecoy.bahdjol.callback.StatusCallBack
import com.codecoy.bahdjol.databinding.NewReqLayBinding
import com.codecoy.bahdjol.datamodels.NewReqData


class NewReqAdapter(
    private val context: Context,
    private val newReqDataList: MutableList<NewReqData>,
    private val statusCallBack: StatusCallBack
) : RecyclerView.Adapter<NewReqAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val mBinding = NewReqLayBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(mBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val newReqData = newReqDataList[position]

        Glide.with(context).load(newReqData.bookingImg)
            .placeholder(R.drawable.ic_downloading)
            .error(R.drawable.ic_error)
            .into(holder.mBinding.ivProduct)

        holder.mBinding.tvProductName.text = newReqData.bookingDesc
        holder.mBinding.tvReqDate.text = newReqData.time + " " + newReqData.date
        holder.mBinding.tvOrderPrice.text = "$ " + newReqData.bookingPrice
        holder.mBinding.tvOrderId.text = newReqData.id

        holder.mBinding.btnAccept.setOnClickListener {
            statusCallBack.onAcceptClick(position, newReqData)
        }

        holder.mBinding.btnDecline.setOnClickListener {
            statusCallBack.onDeclineClick(position, newReqData)
        }

    }

    override fun getItemCount(): Int {
        return newReqDataList.size
    }

    class ViewHolder(val mBinding: NewReqLayBinding) : RecyclerView.ViewHolder(mBinding.root)
}