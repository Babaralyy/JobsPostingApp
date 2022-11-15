package com.codecoy.bahdjol.datamodels

import com.google.gson.annotations.SerializedName

class NotificationData(
    @SerializedName("id"          ) var id          : Int?    = null,
    @SerializedName("user_id"     ) var userId      : String? = null,
    @SerializedName("title"       ) var title       : String? = null,
    @SerializedName("description" ) var description : String? = null,
    @SerializedName("created_at"  ) var createdAt   : String? = null,
    @SerializedName("updated_at"  ) var updatedAt   : String? = null
)