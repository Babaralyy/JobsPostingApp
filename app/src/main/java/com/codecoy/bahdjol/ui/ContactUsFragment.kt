package com.codecoy.bahdjol.ui

import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.codecoy.bahdjol.R
import com.codecoy.bahdjol.databinding.FragmentContactUsBinding


class ContactUsFragment : Fragment() {


    private lateinit var mBinding: FragmentContactUsBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentContactUsBinding.inflate(inflater)

        inIt()

        return mBinding.root
    }

    private fun inIt() {

        mBinding.btnSubmit.setOnClickListener {

            checkCredentials()

        }

    }

    private fun checkCredentials() {


        val firstName: String = mBinding.etFirstName.text.toString().trim()
        val lastName: String = mBinding.etLastName.text.toString().trim()
        val userNumber: String = mBinding.etNumber.text.toString().trim()
        val userEmail: String = mBinding.etEmail.text.toString().trim()
        val userDes: String = mBinding.etDes.text.toString().trim()


        if (firstName.isEmpty()) {
            mBinding.etFirstName.error = "First name is required!"
            mBinding.etFirstName.requestFocus()
            return
        }

        if (lastName.isEmpty()) {
            mBinding.etLastName.error = "Last name is required!"
            mBinding.etLastName.requestFocus()
            return
        }

        if (userNumber.isEmpty()) {
            mBinding.etNumber.error = "Phone Number is required!"
            mBinding.etNumber.requestFocus()
            return
        }
        if (userEmail.isEmpty()) {
            mBinding.etEmail.error = "Email is required!"
            mBinding.etEmail.requestFocus()
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
            mBinding.etEmail.error = "Please provide valid email!"
            mBinding.etEmail.requestFocus()
            return
        }

        if (userDes.isEmpty()) {
            mBinding.etDes.error = "Description is required!"
            mBinding.etDes.requestFocus()
        } else {

        }
    }


}