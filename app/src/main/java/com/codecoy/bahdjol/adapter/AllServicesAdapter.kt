package com.codecoy.bahdjol.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.codecoy.bahdjol.R
import com.codecoy.bahdjol.constant.Constant
import com.codecoy.bahdjol.constant.Constant.TAG
import com.codecoy.bahdjol.databinding.ServiceLayBinding
import com.codecoy.bahdjol.datamodels.AllServiceData

class AllServicesAdapter(
    private val context: Context,
    private val allServiceDataList: MutableList<AllServiceData>
) : RecyclerView.Adapter<AllServicesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val mBinding = ServiceLayBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(mBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val allServiceData = allServiceDataList[position]

        Glide.with(context).load(Constant.IMG_URL + allServiceData.img)
            .placeholder(R.drawable.ic_image)
            .error(R.drawable.ic_error)
            .into(holder.mBinding.ivServiceImg)

        Log.i(TAG, "onBindViewHolder: ${Constant.IMG_URL + allServiceData.img}")

        holder.mBinding.tvServiceTitle.text = allServiceData.categoryName

    }

    override fun getItemCount(): Int {
        return allServiceDataList.size
    }

    inner class ViewHolder(val mBinding: ServiceLayBinding) : RecyclerView.ViewHolder(mBinding.root)
}