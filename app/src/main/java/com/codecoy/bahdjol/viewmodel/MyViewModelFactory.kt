package com.codecoy.bahdjol.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.codecoy.bahdjol.repository.Repository

class MyViewModelFactory (private val repository: Repository): ViewModelProvider.Factory{

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MyViewModel(repository) as T
    }
}