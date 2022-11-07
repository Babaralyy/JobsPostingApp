package com.codecoy.bahdjol.agent

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.codecoy.bahdjol.R
import com.codecoy.bahdjol.databinding.FragmentNewReqBinding


class NewReqFragment : Fragment() {


    private lateinit var mBinding: FragmentNewReqBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentNewReqBinding.inflate(inflater)

        inIt()

        return mBinding.root
    }

    private fun inIt() {
        mBinding.toolBar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

}