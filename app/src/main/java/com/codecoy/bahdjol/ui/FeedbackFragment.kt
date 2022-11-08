package com.codecoy.bahdjol.ui

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.codecoy.bahdjol.MainActivity
import com.codecoy.bahdjol.R
import com.codecoy.bahdjol.databinding.FragmentFeedbackBinding


class FeedbackFragment : Fragment() {

    private lateinit var activity: MainActivity

    private lateinit var mBinding: FragmentFeedbackBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentFeedbackBinding.inflate(inflater)

        inIt()

        return mBinding.root
    }

    private fun inIt() {

        mBinding.ratingBar.setOnRatingBarChangeListener { ratingBar, fl, b ->

        }

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity = context as MainActivity
    }

}