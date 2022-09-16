package com.codecoy.bahdjol.constant

import com.codecoy.bahdjol.datamodels.SubServicesData
import com.google.gson.annotations.SerializedName

data class SubServicesResponse(
    @SerializedName("status") var status: Boolean? = null,
    @SerializedName("message") var message: String? = null,
    @SerializedName("data") var data: ArrayList<SubServicesData> = arrayListOf()
)