package com.codecoy.bahdjol.datamodels

import com.google.gson.annotations.SerializedName

class AgentRatingData(@SerializedName("user_id"    ) var userId    : String? = null,
                      @SerializedName("agent_id"   ) var agentId   : String? = null,
                      @SerializedName("ratings"    ) var ratings   : String? = null,
                      @SerializedName("agent_desc" ) var agentDesc : String? = null,
                      @SerializedName("updated_at" ) var updatedAt : String? = null,
                      @SerializedName("created_at" ) var createdAt : String? = null,
                      @SerializedName("id"         ) var id        : Int?    = null)