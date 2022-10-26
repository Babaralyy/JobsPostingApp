package com.codecoy.bahdjol.ui

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.codecoy.bahdjol.R
import com.codecoy.bahdjol.databinding.FragmentSubscriptionBinding


class SubscriptionFragment : Fragment() {


    private lateinit var mBinding: FragmentSubscriptionBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentSubscriptionBinding.inflate(inflater)

        inIt()

        return mBinding.root
    }

    private fun inIt() {
        mBinding.orderLay1.setOnClickListener { 
            showDialog("You will get")
        }

        mBinding.orderLay2.setOnClickListener {
            showDialog("You will get")
        }

        mBinding.orderLay3.setOnClickListener {
            showDialog("You will get")
        }
    }

    private fun showDialog(s: String) {

        val dialog = Dialog(requireActivity())
        dialog.setContentView(R.layout.subs_dialog_lay)
        dialog.setCancelable(true)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog.show()

    }

}