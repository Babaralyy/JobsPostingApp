package com.codecoy.bahdjol

import androidx.room.TypeConverter
import com.codecoy.bahdjol.datamodels.SubServicesData
import com.google.gson.Gson

import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type


class Converter {

    @TypeConverter
    fun fromString(value: String?): ArrayList<SubServicesData> {
        val listType: Type = object : TypeToken<ArrayList<SubServicesData>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromArrayList(list: ArrayList<SubServicesData>): String? {
        val gson = Gson()
        return gson.toJson(list)
    }
}