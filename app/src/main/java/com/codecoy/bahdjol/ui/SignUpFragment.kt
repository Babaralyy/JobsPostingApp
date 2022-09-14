package com.codecoy.bahdjol.ui

import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.codecoy.bahdjol.R
import com.codecoy.bahdjol.databinding.FragmentSignUpBinding


class SignUpFragment : Fragment() {


    private lateinit var mBinding: FragmentSignUpBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentSignUpBinding.inflate(inflater)

        inIt()

        return mBinding.root
    }

    private fun inIt() {

        mBinding.btnSignUp.setOnClickListener {

            checkCredentials()

        }

    }

    private fun checkCredentials() {

        val firstName: String = mBinding.etFirstName.text.toString().trim()
        val lastName: String = mBinding.etLastName.text.toString().trim()
        val userEmail: String = mBinding.etEmail.text.toString().trim()
        val userPassword: String = mBinding.etPassword.text.toString().trim()

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
        if (userPassword.isEmpty()) {
            mBinding.etPassword.error = "Password can't be empty!"
            mBinding.etPassword.requestFocus()
            return
        }
        if (userPassword.length < 8) {
            mBinding.etPassword.error = "Password should be greater than 8 characters!"
            mBinding.etPassword.requestFocus()
        } else {
            signUp(firstName, lastName, userEmail, userPassword)
        }


    }

    private fun signUp(firstName: String, lastName: String, userEmail: String, userPassword: String) {



    }


}