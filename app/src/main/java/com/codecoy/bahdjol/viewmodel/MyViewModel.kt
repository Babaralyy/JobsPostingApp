package com.codecoy.bahdjol.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.codecoy.bahdjol.constant.SubServicesResponse
import com.codecoy.bahdjol.datamodels.AllServiceResponse
import com.codecoy.bahdjol.datamodels.ImageUploadResponse
import com.codecoy.bahdjol.datamodels.UserResponse
import com.codecoy.bahdjol.repository.Repository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody

class MyViewModel(private val repository: Repository) : ViewModel() {

    private var signInMutableLiveData: MutableLiveData<UserResponse> = MutableLiveData()
    private var signUpMutableLiveData: MutableLiveData<UserResponse> = MutableLiveData()
    private var allServicesMutableLiveData: MutableLiveData<AllServiceResponse> = MutableLiveData()
    private var subServicesMutableLiveData: MutableLiveData<SubServicesResponse> = MutableLiveData()
    private var imageUploadMutableLiveData: MutableLiveData<ImageUploadResponse> = MutableLiveData()

    val signInLiveData: LiveData<UserResponse> = signInMutableLiveData
    val signUpLiveData: LiveData<UserResponse> = signUpMutableLiveData
    val allServicesLiveData: LiveData<AllServiceResponse> = allServicesMutableLiveData
    val subServicesLiveData: LiveData<SubServicesResponse> = subServicesMutableLiveData
    val imageUploadLiveData: LiveData<ImageUploadResponse> = imageUploadMutableLiveData

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
                    val userResponse = UserResponse()
                    userResponse.status = false
                    userResponse.message = response.body().toString()
                    signInMutableLiveData.postValue(userResponse)
                }
            } else {
                val userResponse = UserResponse()
                userResponse.status = false
                userResponse.message = response.errorBody().toString()
                signInMutableLiveData.postValue(userResponse)
            }
        }
    }

    fun signUpUser(
        profileImg: MultipartBody.Part,
        maritalStatus: RequestBody,
        userName: RequestBody,
        userAddress: RequestBody,
        userNumber: RequestBody,
        userEmail: RequestBody,
        userPassword: RequestBody
    ) {
        CoroutineScope(Dispatchers.IO).launch {

            val response =
                repository.signUpUser(
                    profileImg,
                    maritalStatus,
                    userName,
                    userAddress,
                    userNumber,
                    userEmail,
                    userPassword
                )

            if (response.isSuccessful) {
                if (response.code() == 200) {
                    signUpMutableLiveData.postValue(response.body())

                } else {
                    val userResponse = UserResponse()
                    userResponse.status = false
                    userResponse.message = response.body().toString()
                    signInMutableLiveData.postValue(userResponse)
                }
            } else {
                val userResponse = UserResponse()
                userResponse.status = false
                userResponse.message = response.errorBody().toString()
                signInMutableLiveData.postValue(userResponse)
            }

        }

    }

    fun allServices(){

        CoroutineScope(Dispatchers.IO).launch {

            val response =
                repository.allServices()

            if (response.isSuccessful) {
                if (response.code() == 200) {
                    allServicesMutableLiveData.postValue(response.body())
                } else {
                    val allServiceResponse = AllServiceResponse()
                    allServiceResponse.status = false
                    allServiceResponse.message = response.body().toString()
                    allServicesMutableLiveData.postValue(allServiceResponse)
                }
            } else {
                val allServiceResponse = AllServiceResponse()
                allServiceResponse.status = false
                allServiceResponse.message = response.errorBody().toString()
                allServicesMutableLiveData.postValue(allServiceResponse)
            }

        }

    }

     fun subServices(cat_id: Int) {

        CoroutineScope(Dispatchers.IO).launch {
            val response =
                repository.subServices(cat_id)

            if (response.isSuccessful) {
                if (response.code() == 200) {
                    subServicesMutableLiveData.postValue(response.body())
                } else {
                    val subServicesResponse = SubServicesResponse()
                    subServicesResponse.status = false
                    subServicesResponse.message = response.body().toString()
                    subServicesMutableLiveData.postValue(subServicesResponse)
                }
            } else {
                val subServicesResponse = SubServicesResponse()
                subServicesResponse.status = false
                subServicesResponse.message = response.errorBody().toString()
                subServicesMutableLiveData.postValue(subServicesResponse)
            }

        }
    }

    fun imageUpload(itemImg: MultipartBody.Part){
        CoroutineScope(Dispatchers.IO).launch {
            val response =
                repository.uploadImage(itemImg)

            if (response.isSuccessful) {
                if (response.code() == 200) {
                    imageUploadMutableLiveData.postValue(response.body())
                } else {
                    val imageUploadResponse = ImageUploadResponse()
                    imageUploadResponse.status = false
                    imageUploadResponse.message = response.body().toString()
                    imageUploadMutableLiveData.postValue(imageUploadResponse)
                }
            } else {
                val imageUploadResponse = ImageUploadResponse()
                imageUploadResponse.status = false
                imageUploadResponse.message = response.errorBody().toString()
                imageUploadMutableLiveData.postValue(imageUploadResponse)
            }

        }

    }
}