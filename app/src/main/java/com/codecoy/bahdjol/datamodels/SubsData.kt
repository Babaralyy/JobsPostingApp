package com.codecoy.bahdjol.datamodels

import com.google.gson.annotations.SerializedName

data class SubsData(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("package_name") var packageName: String? = null,
    @SerializedName("package_price") var packagePrice: String? = null,
    @SerializedName("total_orders") var totalOrders: String? = null,
    @SerializedName("duration") var duration: String? = null,
    @SerializedName("created_at") var createdAt: String? = null,
    @SerializedName("updated_at") var updatedAt: String? = null
)