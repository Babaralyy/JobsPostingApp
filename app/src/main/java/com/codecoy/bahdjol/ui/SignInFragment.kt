package com.codecoy.bahdjol.ui

import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.codecoy.bahdjol.constant.Constant
import com.codecoy.bahdjol.constant.Constant.TAG
import com.codecoy.bahdjol.databinding.FragmentSignInBinding
import com.codecoy.bahdjol.datamodels.UserResponse
import com.codecoy.bahdjol.network.ApiCall
import com.codecoy.bahdjol.utils.ServiceIds
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response


class SignInFragment : Fragment() {

    private var deviceToken: String? = null

    private lateinit var mBinding: FragmentSignInBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentSignInBinding.inflate(inflater)

        inIt()

        return mBinding.root
    }

    private fun inIt() {

        getDeviceToken()

        mBinding.btnSignIn.setOnClickListener {
            checkCredentials()
        }

        mBinding.btnSignUp.setOnClickListener {
            val action = SignInFragmentDirections.actionSignInFragmentToSignUpFragment()
            findNavController().navigate(action)
        }

    }

    private fun checkCredentials() {

        val userEmail: String = mBinding.etEmail.text.toString().trim()
        val userPassword: String = mBinding.etPassword.text.toString().trim()

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

        } else {
            signIn(userEmail, userPassword)
        }

    }

    private fun signIn(userEmail: String, userPassword: String) {

        val dialog = Constant.getDialog(requireActivity())
        dialog.show()

        CoroutineScope(Dispatchers.IO).launch {

            val signInApi = Constant.getRetrofitInstance().create(ApiCall::class.java)
            val signInCall = signInApi.signInUser(userEmail, userPassword, deviceToken!!)

            signInCall.enqueue(object : retrofit2.Callback<UserResponse> {
                override fun onResponse(
                    call: Call<UserResponse>,
                    response: Response<UserResponse>
                ) {

                    if (response.isSuccessful) {
                        dialog.dismiss()

                        if (response.body() != null && response.body()?.status == true) {
                            val userData = response.body()!!.data

                            if (userData != null) {

                                ServiceIds.saveUserIntoPref(requireActivity(), "userInfo", userData)

                                Toast.makeText(
                                    requireActivity(),
                                    response.body()!!.message,
                                    Toast.LENGTH_SHORT
                                ).show()

                                val action =
                                    SignInFragmentDirections.actionSignInFragmentToMainFragment()
                                findNavController().navigate(action)

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

                    } else {
                        dialog.dismiss()
                        Toast.makeText(requireActivity(), response.message(), Toast.LENGTH_SHORT)
                            .show()
                    }
                }

                override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                    dialog.dismiss()
                    Toast.makeText(requireActivity(), t.message, Toast.LENGTH_SHORT).show()
                }

            })
        }

    }

    private fun getDeviceToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.i(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            deviceToken = task.result

            Log.i(TAG, " token:----> : $deviceToken")

        })
    }

}