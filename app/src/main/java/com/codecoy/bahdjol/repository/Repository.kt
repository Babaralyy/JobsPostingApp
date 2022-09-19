package com.codecoy.bahdjol.repository

import com.codecoy.bahdjol.constant.Constant
import com.codecoy.bahdjol.datamodels.BookingDetails
import com.codecoy.bahdjol.network.ApiCall
import okhttp3.MultipartBody
import okhttp3.RequestBody


class Repository {

    private var mApi = Constant.getRetrofitInstance()

    private val apiCall = mApi.create(ApiCall::class.java)

    suspend fun signInUser(userEmail: String, userPassword: String, deviceToken: String) =
        apiCall.signInUser(userEmail, userPassword, deviceToken)

    suspend fun signUpUser(
        profileImg: MultipartBody.Part,
        maritalStatus: RequestBody,
        userName: RequestBody,
        userAddress: RequestBody,
        userNumber: RequestBody,
        userEmail: RequestBody,
        userPassword: RequestBody
    ) = apiCall.createUser(
        profileImg,
        maritalStatus,
        userName,
        userAddress,
        userNumber,
        userEmail,
        userPassword
    )

    suspend fun allServices() = apiCall.allServices()

    suspend fun subServices(cat_id: Int) = apiCall.subServices(cat_id)

    suspend fun uploadImage(itemImg: MultipartBody.Part) = apiCall.uploadImage(itemImg)

    suspend fun sendBookingDetails(bookingDetails: BookingDetails) = apiCall.sendBookingDetails(bookingDetails)

    suspend fun bookingHistory(user_id: Int) = apiCall.bookingHistory(user_id)
}