package com.codecoy.bahdjol.roomdb

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Service")
data class Service(@PrimaryKey var id: Int? = null,
              @ColumnInfo(name ="category_name") var categoryName: String? = null,
              @ColumnInfo(name ="img") var img: String? = null,
              @ColumnInfo(name ="created_at") var createdAt: String? = null,
              @ColumnInfo(name ="updated_at") var updatedAt: String? = null)