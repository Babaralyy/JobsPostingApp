package com.codecoy.bahdjol.datamodels

import com.google.gson.annotations.SerializedName

class UpdateProfileResponse(
    @SerializedName("status") var status: Boolean? = null,
    @SerializedName("message") var message: String? = null,
    @SerializedName("data") var data: UpdateProfileData? = UpdateProfileData()
)