package com.codecoy.bahdjol.network

import com.codecoy.bahdjol.datamodels.SignInResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ApiCall {

    @FormUrlEncoded
    @POST("api/login")
    suspend fun signInUser(
        @Field("email") userEmail: String,
        @Field("password") userPassword: String,
        @Field("device_token") deviceToken: String
    ): Response<SignInResponse>

}