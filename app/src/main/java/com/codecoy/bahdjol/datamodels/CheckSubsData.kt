package com.codecoy.bahdjol.datamodels

import com.google.gson.annotations.SerializedName

class CheckSubsData(
    @SerializedName("id"         ) var id        : Int?    = null,
    @SerializedName("user_id"    ) var userId    : String? = null,
    @SerializedName("subs_id"    ) var subsId    : String? = null,
    @SerializedName("pkg_name"   ) var pkgName   : String? = null,
    @SerializedName("pkg_price"  ) var pkgPrice  : String? = null,
    @SerializedName("orders"     ) var orders    : String? = null,
    @SerializedName("start_date" ) var startDate : String? = null,
    @SerializedName("end_date"   ) var endDate   : String? = null,
    @SerializedName("status"     ) var status    : String? = null,
    @SerializedName("created_at" ) var createdAt : String? = null,
    @SerializedName("updated_at" ) var updatedAt : String? = null
)