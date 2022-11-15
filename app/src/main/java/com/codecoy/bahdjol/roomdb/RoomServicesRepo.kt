package com.codecoy.bahdjol.roomdb

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel

class RoomServicesRepo(private val appDatabase: AppDatabase){

    fun insertService(service: Service) = appDatabase.servicesDao().insertService(service)

    fun insertSubService(subService: SubService) = appDatabase.servicesDao().insertSubService(subService)

    fun getAllServices()= appDatabase.servicesDao().getAllServices()

    fun getSubService(id: Int) = appDatabase.servicesDao().getSubService(id)

}