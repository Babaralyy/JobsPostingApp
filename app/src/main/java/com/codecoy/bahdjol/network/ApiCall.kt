package com.codecoy.bahdjol.network

import com.codecoy.bahdjol.constant.SubServicesResponse
import com.codecoy.bahdjol.datamodels.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface ApiCall {

    @Multipart
    @POST("api/add_users")
    fun createUser(
        @Part profile_img: MultipartBody.Part,
        @Part("marital_status") marital_status: RequestBody,
        @Part("name") name: RequestBody,
        @Part("address") address: RequestBody,
        @Part("phone") phone: RequestBody,
        @Part("email") email: RequestBody,
        @Part("password") password: RequestBody
    ): Call<UserResponse>

    @FormUrlEncoded
    @POST("api/login")
    fun signInUser(
        @Field("email") userEmail: String,
        @Field("password") userPassword: String,
        @Field("device_token") deviceToken: String
    ): Call<UserResponse>

    @GET("api/category")
    suspend fun allServices(): Response<AllServiceResponse>

    @GET("api/Subcategory")
    suspend fun subServices(@Query("cat_id") cat_id: Int): Response<SubServicesResponse>

    @Multipart
    @POST("api/upload_img?")
    suspend fun uploadImage(
        @Part profile_img: MultipartBody.Part
    ): Response<ImageUploadResponse>

    @Headers("Accept: application/json")
    @POST("api/add_booking")
    suspend fun sendBookingDetails(@Body bookingDetails: BookingDetails): Response<BookingResponse>

    @GET("api/booking_list")
    suspend fun bookingHistory(@Query("user_id") user_id: Int): Response<BookingHistoryResponse>

    @Multipart
    @POST("api/edit_user")
     fun updateUser(
        @Part profile_img: MultipartBody.Part?,
        @Part("user_id") user_id: RequestBody,
        @Part("marital_status") marital_status: RequestBody,
        @Part("name") name: RequestBody,
        @Part("address") address: RequestBody,
        @Part("phone") phone: RequestBody
    ): Call<UpdateProfileResponse>


    @GET("api/wallet_user")
    suspend fun userBalance(@Query("user_id") user_id: Int): Response<WalletResponse>

    @FormUrlEncoded
    @POST("api/payment")
    suspend fun updateBalance(
        @Field("user_id") user_id: Int,
        @Field("new_balance") new_balance: String
    ): Response<WalletResponse>

    @FormUrlEncoded
    @POST("api/wallet")
    suspend fun addBalance(
        @Field("user_id") user_id: Int,
        @Field("code") new_code: String
    ): Response<WalletResponse>

}