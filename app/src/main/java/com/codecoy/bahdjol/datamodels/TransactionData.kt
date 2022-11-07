package com.codecoy.bahdjol.datamodels

import com.google.gson.annotations.SerializedName

data class TransactionData(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("user_id") var userId: String? = null,
    @SerializedName("trans_name") var transName: String? = null,
    @SerializedName("trans_price") var transPrice: String? = null,
    @SerializedName("trans_date") var transDate: String? = null,
    @SerializedName("trans_time") var transTime: String? = null,
    @SerializedName("created_at") var createdAt: String? = null,
    @SerializedName("updated_at") var updatedAt: String? = null
)