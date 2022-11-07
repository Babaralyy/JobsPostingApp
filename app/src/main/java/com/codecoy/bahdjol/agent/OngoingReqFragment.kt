package com.codecoy.bahdjol.agent

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.codecoy.bahdjol.databinding.FragmentOngoingReqBinding


class OngoingReqFragment : Fragment() {


    private lateinit var mBinding: FragmentOngoingReqBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentOngoingReqBinding.inflate(inflater)

        inIt()

        return mBinding.root
    }

    private fun inIt() {
        mBinding.toolBar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

}