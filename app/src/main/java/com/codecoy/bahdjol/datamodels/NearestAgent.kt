package com.codecoy.bahdjol.datamodels

import com.google.gson.annotations.SerializedName

data class NearestAgent(
    @SerializedName("agent_name") var agentName: String? = null,
    @SerializedName("agent_phone") var agentPhone: String? = null,
    @SerializedName("agent_email") var agentEmail: String? = null,
    @SerializedName("distance") var distance: String? = null
)