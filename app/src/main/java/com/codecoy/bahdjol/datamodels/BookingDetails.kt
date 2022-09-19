package com.codecoy.bahdjol.datamodels

import com.google.gson.annotations.SerializedName

data class BookingDetails(
    @SerializedName("user_id") var userId: Int? = null,
    @SerializedName("cat_id") var catId: Int? = null,
    @SerializedName("subcat_id") var subcatId: Int? = null,
    @SerializedName("booking_desc") var bookingDesc: String? = null,
    @SerializedName("lat") var lat: Double? = null,
    @SerializedName("long") var long: Double? = null,
    @SerializedName("date") var date: String? = null,
    @SerializedName("time") var time: String? = null,
    @SerializedName("booking_pics") var bookingPics: ArrayList<BookingPics> = arrayListOf()
)