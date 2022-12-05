package com.codecoy.bahdjol.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.codecoy.bahdjol.R
import com.codecoy.bahdjol.callback.HistoryCallback
import com.codecoy.bahdjol.constant.Constant
import com.codecoy.bahdjol.databinding.BookingHistoryLayBinding
import com.codecoy.bahdjol.datamodels.BookingHistoryData
import kotlinx.coroutines.flow.combine

class HistoryAdapter(
    private val context: Context,
    private val bookingHistoryList: MutableList<BookingHistoryData>,
    private val historyCallback: HistoryCallback
) : RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val mBinding = BookingHistoryLayBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(mBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val bookingHistoryData = bookingHistoryList[position]

        Glide.with(context).load(Constant.IMG_URL + bookingHistoryData.bookingImage)
            .placeholder(R.drawable.ic_downloading)
            .error(R.drawable.ic_error)
            .into(holder.mBinding.ivProduct)

        holder.mBinding.tvProductName.text = bookingHistoryData.subcategoryName
        holder.mBinding.tvOrderId.text = bookingHistoryData.id.toString()
        holder.mBinding.tvOrderDate.text = bookingHistoryData.date

        holder.mBinding.tvOrderPrice.text = bookingHistoryData.bookingPrice

        if (bookingHistoryData.status == "0"){
            holder.mBinding.tvOrderStatus.text = "Pending"
        }
        if (bookingHistoryData.status == "1"){
            holder.mBinding.tvOrderStatus.text = "Confirmed"
        }
        if (bookingHistoryData.status == "2"){
            holder.mBinding.tvOrderStatus.text = "Cancelled"
        }
        if (bookingHistoryData.status == "3"){
            holder.mBinding.tvOrderStatus.text = "Completed"
        }

        holder.itemView.setOnClickListener {
            historyCallback.onHistoryClick(position, bookingHistoryData)
        }
    }

    override fun getItemCount(): Int {
        return bookingHistoryList.size
    }

    class ViewHolder(val mBinding: BookingHistoryLayBinding) :
        RecyclerView.ViewHolder(mBinding.root)
}