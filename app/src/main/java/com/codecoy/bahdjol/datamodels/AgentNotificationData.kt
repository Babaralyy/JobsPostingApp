package com.codecoy.bahdjol.datamodels

import com.google.gson.annotations.SerializedName

class AgentNotificationData(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("user_id") var userId: String? = null,
    @SerializedName("title") var title: String? = null,
    @SerializedName("description") var description: String? = null,
    @SerializedName("agent_id") var agentId: String? = null,
    @SerializedName("role") var role: String? = null,
    @SerializedName("status") var status: String? = null,
    @SerializedName("book_id") var bookId: String? = null,
    @SerializedName("created_at") var createdAt: String? = null,
    @SerializedName("updated_at") var updatedAt: String? = null,
    @SerializedName("user_name") var userName: String? = null

)