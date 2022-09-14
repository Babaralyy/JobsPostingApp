package com.codecoy.bahdjol.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.codecoy.bahdjol.datamodels.SignInResponse
import com.codecoy.bahdjol.repository.Repository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyViewModel(private val repository: Repository) : ViewModel() {

    private var signInMutableLiveData: MutableLiveData<SignInResponse> = MutableLiveData()
    val signInLiveData: LiveData<SignInResponse> = signInMutableLiveData

    fun signInUser(
        userEmail: String,
        userPassword: String,
        deviceToken: String
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val response =
                repository.signInUser(userEmail, userPassword, deviceToken)

            if (response.isSuccessful) {
                if (response.code() == 200) {
                    signInMutableLiveData.postValue(response.body())

                } else {
                    val signInResponse = SignInResponse()
                    signInResponse.status = false
                    signInResponse.message = response.body().toString()
                    signInMutableLiveData.postValue(signInResponse)
                }
            } else {
                val signInResponse = SignInResponse()
                signInResponse.status = false
                signInResponse.message = response.errorBody().toString()
                signInMutableLiveData.postValue(signInResponse)
            }
        }
    }
}