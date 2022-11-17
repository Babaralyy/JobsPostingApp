package com.codecoy.bahdjol.datamodels

import com.google.gson.annotations.SerializedName

class StatusData(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("request_id") var requestId: String? = null,
    @SerializedName("agent_id") var agentId: String? = null,
    @SerializedName("status") var status: String? = null,
    @SerializedName("created_at") var createdAt: String? = null,
    @SerializedName("updated_at") var updatedAt: String? = null
)