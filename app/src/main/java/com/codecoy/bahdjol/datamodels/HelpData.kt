package com.codecoy.bahdjol.datamodels

import com.google.gson.annotations.SerializedName

data class HelpData(@SerializedName("first_name"  ) var firstName   : String? = null,
                    @SerializedName("last_name"   ) var lastName    : String? = null,
                    @SerializedName("phone"       ) var phone       : String? = null,
                    @SerializedName("email"       ) var email       : String? = null,
                    @SerializedName("description" ) var description : String? = null,
                    @SerializedName("updated_at"  ) var updatedAt   : String? = null,
                    @SerializedName("created_at"  ) var createdAt   : String? = null,
                    @SerializedName("id"          ) var id          : Int?    = null)