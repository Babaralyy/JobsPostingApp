package com.codecoy.bahdjol.datamodels

import com.google.gson.annotations.SerializedName

data class OngoingReqData(
    @SerializedName("id") var id: String? = null,
    @SerializedName("user_id") var userId: String? = null,
    @SerializedName("cat_id") var catId: String? = null,
    @SerializedName("subcat_id") var subcatId: String? = null,
    @SerializedName("booking_desc") var bookingDesc: String? = null,
    @SerializedName("lat") var lat: String? = null,
    @SerializedName("long") var long: String? = null,
    @SerializedName("status") var status: String? = null,
    @SerializedName("date") var date: String? = null,
    @SerializedName("time") var time: String? = null,
    @SerializedName("booking_price") var bookingPrice: String? = null,
    @SerializedName("agent_id") var agentId: String? = null,
    @SerializedName("created_at") var createdAt: String? = null,
    @SerializedName("updated_at") var updatedAt: String? = null,
    @SerializedName("subcategory_name") var subcategoryName: String? = null,
    @SerializedName("booking_img") var bookingImg: String? = null
)