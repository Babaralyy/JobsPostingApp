package com.codecoy.bahdjol.repository

import com.codecoy.bahdjol.constant.Constant
import com.codecoy.bahdjol.datamodels.BookingDetails
import com.codecoy.bahdjol.network.ApiCall
import okhttp3.MultipartBody


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

    suspend fun updateBalance(user_id: Int, new_price: String) =
        apiCall.updateBalance(user_id, new_price)

    suspend fun allSubscriptions() = apiCall.allSubscriptions()

    suspend fun getSubscription(
        user_id: Int,
        subs_id: Int,
        pkg_name: String,
        pkg_price: Double,
        orders: String
    ) = apiCall.getSubscription(user_id, subs_id, pkg_name, pkg_price, orders)

    suspend  fun checkSubscription(user_id: Int) = apiCall.checkSubscriptions(user_id)

    suspend  fun userTransaction(user_id: Int) = apiCall.userTransaction(user_id)

    suspend  fun newRequests(agent_id: Int) = apiCall.newRequests(agent_id)

    suspend  fun ongoingRequests(agent_id: Int) = apiCall.ongoingRequests(agent_id)

    suspend  fun historyRequests(agent_id: Int) = apiCall.historyRequests(agent_id)
}