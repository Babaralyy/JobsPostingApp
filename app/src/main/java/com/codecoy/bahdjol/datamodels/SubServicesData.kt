package com.codecoy.bahdjol.datamodels

import com.google.gson.annotations.SerializedName

data class SubServicesData(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("cat_id") var catId: String? = null,
    @SerializedName("subcategory_name") var subcategoryName: String? = null,
    @SerializedName("img") var img: String? = null,
    @SerializedName("price") var price: String? = null,
    @SerializedName("created_at") var createdAt: String? = null,
    @SerializedName("updated_at") var updatedAt: String? = null
)