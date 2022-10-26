package com.codecoy.bahdjol.repository

import com.codecoy.bahdjol.constant.Constant
import com.codecoy.bahdjol.datamodels.BookingDetails
import com.codecoy.bahdjol.network.ApiCall
import okhttp3.MultipartBody
import okhttp3.RequestBody


class Repository {

    private var mApi = Constant.getRetrofitInstance()

    private val apiCall = mApi.create(ApiCall::class.java)


    suspend fun allServices() = apiCall.allServices()

    suspend fun subServices(cat_id: Int) = apiCall.subServices(cat_id)

    suspend fun uploadImage(itemImg: MultipartBody.Part) = apiCall.uploadImage(itemImg)

    suspend fun sendBookingDetails(bookingDetails: BookingDetails) =
        apiCall.sendBookingDetails(bookingDetails)

    suspend fun bookingHistory(user_id: Int) = apiCall.bookingHistory(user_id)

    suspend fun userBalance(user_id: Int) = apiCall.userBalance(user_id)

    suspend fun addBalance(user_id: Int, new_code: String) = apiCall.addBalance(user_id, new_code)

    suspend fun updateBalance(user_id: Int, new_price: String) = apiCall.updateBalance(user_id, new_price)

}