package com.codecoy.bahdjol.agent

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.codecoy.bahdjol.R
import com.codecoy.bahdjol.databinding.FragmentNewReqBinding
import com.codecoy.bahdjol.databinding.FragmentReqHistoryBinding


class ReqHistoryFragment : Fragment() {

    private lateinit var mBinding: FragmentReqHistoryBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentReqHistoryBinding.inflate(inflater)

        inIt()

        return mBinding.root
    }

    private fun inIt() {

        mBinding.toolBar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

    }
}