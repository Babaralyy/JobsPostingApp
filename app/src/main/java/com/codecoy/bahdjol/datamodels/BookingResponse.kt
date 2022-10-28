package com.codecoy.bahdjol.datamodels

import com.google.gson.annotations.SerializedName

class BookingResponse(
    @SerializedName("status") var status: Boolean? = null,
    @SerializedName("message") var message: String? = null,
    @SerializedName("nearest_agent") var nearestAgentList: ArrayList<NearestAgent> = arrayListOf()
)
