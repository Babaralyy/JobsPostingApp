package com.codecoy.bahdjol.roomdb

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.codecoy.bahdjol.datamodels.SubServicesData

@Entity(tableName = "SubService")
data class SubService(
    @PrimaryKey var id: Int? = null,
    @ColumnInfo(name = "data") var data: ArrayList<SubServicesData> = arrayListOf())