package com.codecoy.bahdjol.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.codecoy.bahdjol.constant.SubServicesResponse
import com.codecoy.bahdjol.datamodels.*
import com.codecoy.bahdjol.repository.Repository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody

class MyViewModel(private val repository: Repository) : ViewModel() {

    private var allServicesMutableLiveData: MutableLiveData<AllServiceResponse> = MutableLiveData()
    private var subServicesMutableLiveData: MutableLiveData<SubServicesResponse> = MutableLiveData()
    private var imageUploadMutableLiveData: MutableLiveData<ImageUploadResponse> = MutableLiveData()
    private var sendBookingMutableLiveData: MutableLiveData<BookingResponse> = MutableLiveData()
    private var bookingHistoryMutableLiveData: MutableLiveData<BookingHistoryResponse> =
        MutableLiveData()

    private var userBalanceMutableLiveData: MutableLiveData<WalletResponse> =
        MutableLiveData()

    private var addBalanceMutableLiveData: MutableLiveData<WalletResponse> =
        MutableLiveData()

    private var updateBalanceMutableLiveData: MutableLiveData<WalletResponse> =
        MutableLiveData()

    val allServicesLiveData: LiveData<AllServiceResponse> = allServicesMutableLiveData
    val subServicesLiveData: LiveData<SubServicesResponse> = subServicesMutableLiveData
    val imageUploadLiveData: LiveData<ImageUploadResponse> = imageUploadMutableLiveData
    val bookingLiveData: LiveData<BookingResponse> = sendBookingMutableLiveData
    val bookingHistoryLiveData: LiveData<BookingHistoryResponse> = bookingHistoryMutableLiveData
    val userBalanceLiveData: LiveData<WalletResponse> = userBalanceMutableLiveData
    val addBalanceLiveData: LiveData<WalletResponse> = addBalanceMutableLiveData
    val updateBalanceLiveData: LiveData<WalletResponse> = updateBalanceMutableLiveData



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

    fun sendBookingDetails(bookingDetails: BookingDetails){

        CoroutineScope(Dispatchers.IO).launch {
            val response =
                repository.sendBookingDetails(bookingDetails)

            if (response.isSuccessful) {
                if (response.code() == 200) {
                    sendBookingMutableLiveData.postValue(response.body())
                } else {
                    val bookingResponse = BookingResponse()
                    bookingResponse.status = false
                    bookingResponse.message = response.body().toString()
                    sendBookingMutableLiveData.postValue(bookingResponse)
                }
            } else {
                val bookingResponse = BookingResponse()
                bookingResponse.status = false
                bookingResponse.message = response.errorBody().toString()
                sendBookingMutableLiveData.postValue(bookingResponse)
            }

        }
    }

     fun bookingHistory(user_id: Int){

         CoroutineScope(Dispatchers.IO).launch {
             val response =
                 repository.bookingHistory(user_id)

             if (response.isSuccessful) {
                 if (response.code() == 200) {
                     bookingHistoryMutableLiveData.postValue(response.body())
                 } else {
                     val bookingHistoryResponse = BookingHistoryResponse()
                     bookingHistoryResponse.status = false
                     bookingHistoryResponse.message = response.body().toString()
                     bookingHistoryMutableLiveData.postValue(bookingHistoryResponse)
                 }
             } else {
                 val bookingHistoryResponse = BookingHistoryResponse()
                 bookingHistoryResponse.status = false
                 bookingHistoryResponse.message = response.errorBody().toString()
                 bookingHistoryMutableLiveData.postValue(bookingHistoryResponse)
             }

         }

     }

    fun userBalance(user_id: Int){

        CoroutineScope(Dispatchers.IO).launch {

            val response =
                repository.userBalance(user_id)

            if (response.isSuccessful) {
                if (response.code() == 200) {
                    userBalanceMutableLiveData.postValue(response.body())
                } else {
                    val walletResponse = WalletResponse()
                    walletResponse.status = false
                    walletResponse.message = response.body().toString()
                    userBalanceMutableLiveData.postValue(walletResponse)
                }
            } else {
                val walletResponse = WalletResponse()
                walletResponse.status = false
                walletResponse.message = response.errorBody().toString()
                userBalanceMutableLiveData.postValue(walletResponse)
            }

        }
    }

    fun addBalance(user_id: Int, new_code: String){

        CoroutineScope(Dispatchers.IO).launch {

            val response =
                repository.addBalance(user_id, new_code)

            if (response.isSuccessful) {
                if (response.code() == 200) {
                    addBalanceMutableLiveData.postValue(response.body())
                } else {
                    val walletResponse = WalletResponse()
                    walletResponse.status = false
                    walletResponse.message = response.body().toString()
                    addBalanceMutableLiveData.postValue(walletResponse)
                }
            } else {
                val walletResponse = WalletResponse()
                walletResponse.status = false
                walletResponse.message = response.errorBody().toString()
                addBalanceMutableLiveData.postValue(walletResponse)
            }

        }
    }

    fun updateBalance(user_id: Int, new_price: String){
        CoroutineScope(Dispatchers.IO).launch {
            val response =
                repository.updateBalance(user_id, new_price)

            if (response.isSuccessful) {
                if (response.code() == 200) {
                    updateBalanceMutableLiveData.postValue(response.body())
                } else {
                    val walletResponse = WalletResponse()
                    walletResponse.status = false
                    walletResponse.message = response.body().toString()
                    updateBalanceMutableLiveData.postValue(walletResponse)
                }
            } else {
                val walletResponse = WalletResponse()
                walletResponse.status = false
                walletResponse.message = response.errorBody().toString()
                updateBalanceMutableLiveData.postValue(walletResponse)
            }

        }
    }

}