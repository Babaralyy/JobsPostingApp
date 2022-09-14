package com.codecoy.bahdjol.datamodels

import com.google.gson.annotations.SerializedName

data class SignInData(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("profile_img") var profileImg: String? = null,
    @SerializedName("marital_status_name") var maritalStatusName: String? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("address") var address: String? = null,
    @SerializedName("phone") var phone: String? = null,
    @SerializedName("email") var email: String? = null,
    @SerializedName("email_verified_at") var emailVerifiedAt: String? = null,
    @SerializedName("device_token") var deviceToken: String? = null,
    @SerializedName("created_at") var createdAt: String? = null,
    @SerializedName("updated_at") var updatedAt: String? = null
)