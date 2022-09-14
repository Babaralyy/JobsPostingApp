package com.codecoy.bahdjol.repository

import com.codecoy.bahdjol.constant.Constant
import com.codecoy.bahdjol.network.ApiCall


class Repository {

    private var signInApi = Constant.getRetrofitInstance()

    private val signInApiCall = signInApi.create(ApiCall::class.java)

    suspend fun signInUser(userEmail: String, userPassword: String, deviceToken: String) = signInApiCall.signInUser(userEmail, userPassword, deviceToken)
}