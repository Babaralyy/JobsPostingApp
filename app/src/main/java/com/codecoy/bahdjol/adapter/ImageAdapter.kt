package com.codecoy.bahdjol.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.codecoy.bahdjol.R
import com.codecoy.bahdjol.constant.Constant.TAG
import com.codecoy.bahdjol.databinding.ImageLayBinding

class ImageAdapter(
    private val context: Context,
    private val imageList: MutableList<String>
) : RecyclerView.Adapter<ImageAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val mBinding = ImageLayBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(mBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val imagePath = imageList[position]
        Log.i(TAG, "onBindViewHolder: $imagePath")

        Glide.with(context).load( imagePath)
            .placeholder(R.drawable.ic_downloading)
            .error(R.drawable.ic_error)
            .into(holder.mBinding.ivAddImage)
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

    class ViewHolder(val mBinding: ImageLayBinding) : RecyclerView.ViewHolder(mBinding.root)

    fun updateAdapterList(imgList: ArrayList<String>){
        this.imageList.clear()
        this.imageList.addAll(imgList)
        notifyDataSetChanged()
    }
}