package com.codecoy.bahdjol.roomdb

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@OptIn(DelicateCoroutinesApi::class)
class RoomServicesViewModel(private val roomServicesRepo: RoomServicesRepo): ViewModel()  {

    fun insertService(service: Service) = GlobalScope.launch {
        roomServicesRepo.insertService(service)
    }

    fun insertSubService(subService: SubService) = GlobalScope.launch {
        roomServicesRepo.insertSubService(subService)
    }

}