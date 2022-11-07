package com.codecoy.bahdjol.ui

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.codecoy.bahdjol.constant.Constant
import com.codecoy.bahdjol.databinding.FragmentContactUsBinding
import com.codecoy.bahdjol.datamodels.HelpResponse
import com.codecoy.bahdjol.network.ApiCall
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


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
            contactUs(
                firstName, lastName, userNumber, userEmail, userDes
            )
        }
    }

    private fun contactUs(
        firstName: String, lastName: String, userNumber: String, userEmail: String, userDes: String
    ) {

        val dialog = Constant.getDialog(requireActivity())
        dialog.show()

        CoroutineScope(Dispatchers.IO).launch {


            val signInApi = Constant.getRetrofitInstance().create(ApiCall::class.java)
            val signInCall = signInApi.contactUs(firstName, lastName, userNumber, userEmail, userDes)

            signInCall.enqueue(object : Callback<HelpResponse>{
                override fun onResponse(
                    call: Call<HelpResponse>,
                    response: Response<HelpResponse>
                ) {

                    if (response.isSuccessful) {
                        dialog.dismiss()

                        if (response.body() != null && response.body()?.status == true) {
                            val helpData = response.body()!!.data

                            if (helpData != null) {

                                Toast.makeText(
                                    requireActivity(),
                                    "Your query has been sent!",
                                    Toast.LENGTH_SHORT
                                ).show()

                                clearViews()


                            } else {
                                Toast.makeText(
                                    requireActivity(),
                                    "Something went wrong",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                        } else {
                            Toast.makeText(
                                requireActivity(),
                                response.body()?.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    }

                }

                override fun onFailure(call: Call<HelpResponse>, t: Throwable) {
                    dialog.dismiss()
                    Toast.makeText(requireActivity(), t.message, Toast.LENGTH_SHORT).show()

                }

            })

        }

    }

    private fun clearViews() {

         mBinding.etFirstName.setText("")
        mBinding.etLastName.setText("")
        mBinding.etNumber.setText("")
        mBinding.etEmail.setText("")
        mBinding.etDes.setText("")

    }


}