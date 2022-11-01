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

    private var allSubsMutableLiveData: MutableLiveData<SubsResponse> =
        MutableLiveData()

    private var getSubsMutableLiveData: MutableLiveData<GetSubsResponse> =
        MutableLiveData()

    private var checkSubsMutableLiveData: MutableLiveData<CheckSubsResponse> =
        MutableLiveData()

    val allServicesLiveData: LiveData<AllServiceResponse> = allServicesMutableLiveData
    val subServicesLiveData: LiveData<SubServicesResponse> = subServicesMutableLiveData
    val imageUploadLiveData: LiveData<ImageUploadResponse> = imageUploadMutableLiveData
    val bookingLiveData: LiveData<BookingResponse> = sendBookingMutableLiveData
    val bookingHistoryLiveData: LiveData<BookingHistoryResponse> = bookingHistoryMutableLiveData
    val userBalanceLiveData: LiveData<WalletResponse> = userBalanceMutableLiveData
    val addBalanceLiveData: LiveData<WalletResponse> = addBalanceMutableLiveData
    val updateBalanceLiveData: LiveData<WalletResponse> = updateBalanceMutableLiveData
    val allSubsLiveData: LiveData<SubsResponse> = allSubsMutableLiveData
    val getSubsLiveData: LiveData<GetSubsResponse> = getSubsMutableLiveData
    val checkSubsLiveData: LiveData<CheckSubsResponse> = checkSubsMutableLiveData



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

    fun allSubscriptions() {
        CoroutineScope(Dispatchers.IO).launch {

            val response =
                repository.allSubscriptions()

            if (response.isSuccessful) {
                if (response.code() == 200) {
                    allSubsMutableLiveData.postValue(response.body())
                } else {
                    val subsResponse = SubsResponse()
                    subsResponse.status = false
                    subsResponse.message = response.body().toString()
                    allSubsMutableLiveData.postValue(subsResponse)
                }
            } else {
                val subsResponse = SubsResponse()
                subsResponse.status = false
                subsResponse.message = response.errorBody().toString()
                allSubsMutableLiveData.postValue(subsResponse)
            }

        }
    }

    fun getSubscription(
        user_id: Int,
        subs_id: Int,
        pkg_name: String,
        pkg_price: Double,
        orders: String
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val response =
                repository.getSubscription(user_id, subs_id, pkg_name, pkg_price, orders)

            if (response.isSuccessful) {
                if (response.code() == 200) {
                    getSubsMutableLiveData.postValue(response.body())
                } else {
                    val getSubsResponse = GetSubsResponse()
                    getSubsResponse.status = false
                    getSubsResponse.message = response.body().toString()
                    getSubsMutableLiveData.postValue(getSubsResponse)
                }
            } else {
                val getSubsResponse = GetSubsResponse()
                getSubsResponse.status = false
                getSubsResponse.message = response.errorBody().toString()
                getSubsMutableLiveData.postValue(getSubsResponse)
            }

        }

    }

     fun checkSubscription(user_id: Int){

         CoroutineScope(Dispatchers.IO).launch {

             val response =
                 repository.checkSubscription(user_id)

             if (response.isSuccessful) {
                 if (response.code() == 200) {
                     checkSubsMutableLiveData.postValue(response.body())
                 } else {
                     val checkSubsResponse = CheckSubsResponse()
                     checkSubsResponse.status = false
                     checkSubsResponse.message = response.body().toString()
                     checkSubsMutableLiveData.postValue(checkSubsResponse)
                 }
             } else {
                 val checkSubsResponse = CheckSubsResponse()
                 checkSubsResponse.status = false
                 checkSubsResponse.message = response.errorBody().toString()
                 checkSubsMutableLiveData.postValue(checkSubsResponse)
             }

         }

    }
}