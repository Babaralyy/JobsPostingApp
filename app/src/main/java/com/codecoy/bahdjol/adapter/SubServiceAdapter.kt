package com.codecoy.bahdjol.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.codecoy.bahdjol.databinding.SubServiceLayBinding
import com.codecoy.bahdjol.datamodels.SubServicesData

class SubServiceAdapter(private val context: Context,
                        private val subServicesList: MutableList<SubServicesData>): RecyclerView.Adapter<SubServiceAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val mBinding = SubServiceLayBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(mBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

    }

    override fun getItemCount(): Int {
        return subServicesList.size
    }

    class ViewHolder(val mBinding: SubServiceLayBinding): RecyclerView.ViewHolder(mBinding.root)
}