package com.codecoy.bahdjol.roomdb

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class RoomServicesFactory(private val roomServicesRepo: RoomServicesRepo): ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return RoomServicesViewModel(roomServicesRepo) as T
    }
}