package com.codecoy.bahdjol.roomdb

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*

@OptIn(DelicateCoroutinesApi::class)
class RoomServicesViewModel(private val roomServicesRepo: RoomServicesRepo): ViewModel()  {

    fun insertService(service: Service) = GlobalScope.launch {
        roomServicesRepo.insertService(service)
    }

    fun insertSubService(subService: SubService) = GlobalScope.launch {
        roomServicesRepo.insertSubService(subService)
    }

     suspend fun getAllServices() =
        withContext(Dispatchers.IO) {
            roomServicesRepo.getAllServices()
        }

    suspend fun getSubService(id: Int) =
        withContext(Dispatchers.IO) {
            roomServicesRepo.getSubService(id)
        }

}