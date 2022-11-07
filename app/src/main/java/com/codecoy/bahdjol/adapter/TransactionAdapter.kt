package com.codecoy.bahdjol.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.codecoy.bahdjol.databinding.TransactionLayBinding
import com.codecoy.bahdjol.datamodels.TransactionData

class TransactionAdapter(private val context: Context,
                         private val transDataList: MutableList<TransactionData>): RecyclerView.Adapter<TransactionAdapter.ViewHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val mBinding = TransactionLayBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(mBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val transactionData = transDataList[position]

        holder.mBinding.tvTransName.text = transactionData.transName
        holder.mBinding.tvTransPrice.text = "$ " + transactionData.transPrice
        holder.mBinding.tvTransDate.text = transactionData.transTime + " " + transactionData.transDate

    }

    override fun getItemCount(): Int {
        return transDataList.size
    }

    class ViewHolder(val mBinding: TransactionLayBinding): RecyclerView.ViewHolder(mBinding.root) {

    }
}