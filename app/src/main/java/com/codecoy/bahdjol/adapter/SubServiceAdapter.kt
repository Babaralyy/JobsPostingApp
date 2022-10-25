package com.codecoy.bahdjol.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.codecoy.bahdjol.R
import com.codecoy.bahdjol.callback.ServicesCallback
import com.codecoy.bahdjol.constant.Constant
import com.codecoy.bahdjol.databinding.SubServiceLayBinding
import com.codecoy.bahdjol.datamodels.SubServicesData

class SubServiceAdapter(private val context: Context,
                        private val subServicesList: MutableList<SubServicesData>,
                        private val serviceCallback: ServicesCallback
): RecyclerView.Adapter<SubServiceAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val mBinding = SubServiceLayBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(mBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val subServicesData = subServicesList[position]

        holder.mBinding.tvServiceTitle.text = subServicesData.subcategoryName

        Glide.with(context).load(Constant.IMG_URL + subServicesData.img)
            .placeholder(R.drawable.ic_downloading)
            .error(R.drawable.ic_error)
            .into(holder.mBinding.ivServiceImg)

        holder.mBinding.tvServicePrice.text = subServicesData.price + " $"

        holder.itemView.setOnClickListener {

            serviceCallback.onServiceClick(position)

        }
    }

    override fun getItemCount(): Int {
        return subServicesList.size
    }

    class ViewHolder(val mBinding: SubServiceLayBinding): RecyclerView.ViewHolder(mBinding.root)
}