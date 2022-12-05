package com.codecoy.bahdjol.datamodels

import com.google.gson.annotations.SerializedName

data class AgentLoginData(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("cat_id") var catId: String? = null,
    @SerializedName("subcat_id") var subCatId: String? = null,
    @SerializedName("agent_name") var agentName: String? = null,
    @SerializedName("lat") var lat: String? = null,
    @SerializedName("longitude") var longitude: String? = null,
    @SerializedName("agent_phone") var agentPhone: String? = null,
    @SerializedName("agent_email") var agentEmail: String? = null,
    @SerializedName("password") var password: String? = null,
    @SerializedName("device_token") var deviceToken: String? = null,
    @SerializedName("created_at") var createdAt: String? = null,
    @SerializedName("updated_at") var updatedAt: String? = null,
    @SerializedName("agent_rating") var agentRating: Double? = null,
    @SerializedName("total_user_rate_agent") var totalUserRateAgent: Int? = null
)