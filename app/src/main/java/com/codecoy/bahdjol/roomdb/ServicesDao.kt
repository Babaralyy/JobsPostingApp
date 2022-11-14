package com.codecoy.bahdjol.roomdb

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ServicesDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertService(services: Service)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertSubService(subService: SubService)

    @Query("SELECT * FROM Service")
    fun getAllServices(): LiveData<MutableList<Service>>

    @Query("SELECT * FROM SubService")
    fun getAllSubServices(): LiveData<MutableList<SubService>>
}