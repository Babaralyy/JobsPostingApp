package com.codecoy.bahdjol.datamodels

import com.google.gson.annotations.SerializedName

data class UserData(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("profile_img") var profileImg: String? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("last_name") var lastName: String? = null,
    @SerializedName("address") var address: String? = null,
    @SerializedName("phone") var phone: String? = null,
    @SerializedName("email") var email: String? = null,
    @SerializedName("email_verified_at") var emailVerifiedAt: String? = null,
    @SerializedName("balance") var balance: String? = null,
    @SerializedName("subscription_status") var subscriptionStatus: String? = null,
    @SerializedName("device_token") var deviceToken: String? = null,
    @SerializedName("created_at") var createdAt: String? = null,
    @SerializedName("updated_at") var updatedAt: String? = null,
    @SerializedName("user_rating") var userRating: Double? = null,
    @SerializedName("total_agent_rate_user") var totalAgentRateUser: Int? = null
)