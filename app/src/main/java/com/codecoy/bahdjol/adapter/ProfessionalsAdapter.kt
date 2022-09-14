package com.codecoy.bahdjol.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.codecoy.bahdjol.databinding.ProLayBinding

class ProfessionalsAdapter(private val context: Context): RecyclerView.Adapter<ProfessionalsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val mBinding = ProLayBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(mBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

     class ViewHolder(mBinding: ProLayBinding): RecyclerView.ViewHolder(mBinding.root)
}