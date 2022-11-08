package com.codecoy.bahdjol.ui

import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.codecoy.bahdjol.MainActivity
import com.codecoy.bahdjol.R
import com.codecoy.bahdjol.constant.Constant
import com.codecoy.bahdjol.constant.Constant.TAG
import com.codecoy.bahdjol.databinding.FragmentProfileBinding
import com.codecoy.bahdjol.datamodels.UpdateProfileResponse
import com.codecoy.bahdjol.datamodels.UserData
import com.codecoy.bahdjol.network.ApiCall
import com.codecoy.bahdjol.repository.Repository
import com.codecoy.bahdjol.utils.ServiceIds
import com.codecoy.bahdjol.viewmodel.MyViewModel
import com.codecoy.bahdjol.viewmodel.MyViewModelFactory
import gun0912.tedimagepicker.builder.TedImagePicker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream


class ProfileFragment : Fragment() {

    private lateinit var myViewModel: MyViewModel

    private var uri: Uri? = null
    private lateinit var bitmap: Bitmap
    private lateinit var encodeImageString: String

    private lateinit var activity: MainActivity

    private var userData: UserData? = null

    private lateinit var mBinding: FragmentProfileBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentProfileBinding.inflate(inflater)

        inIt()

        return mBinding.root
    }

    private fun inIt() {

        val repository = Repository()
        val myViewModelFactory = MyViewModelFactory(repository)
        myViewModel =
            ViewModelProvider(this, myViewModelFactory)[MyViewModel::class.java]

        getUserData()

        mBinding.btnUpdate.setOnClickListener {

            checkCredentials()

        }

        mBinding.ivCamera.setOnClickListener {
            chooseImage()
        }

    }

    private fun chooseImage() {

        TedImagePicker.with(requireContext())
            .start { uri -> showSingleImage(uri) }
    }

    private fun showSingleImage(uri: Uri) {

        this.uri = uri

        val inputStream: InputStream =
            activity.contentResolver.openInputStream(this.uri!!)!!
        bitmap = BitmapFactory.decodeStream(inputStream)

        mBinding.ivProfile.setImageBitmap(bitmap)

        encodeBitmapImage(bitmap)

    }

    private fun encodeBitmapImage(bitmap: Bitmap?) {

        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val bytesOfImage: ByteArray = byteArrayOutputStream.toByteArray()
        encodeImageString = Base64.encodeToString(bytesOfImage, Base64.DEFAULT)

        Log.i(Constant.TAG, "signUp --> encodeBitmapImage: $encodeImageString")

    }

    private fun getUserData() {

        userData = ServiceIds.fetchUserFromPref(activity, "userInfo")

        if (userData != null) {

            userDataOnViews(userData!!)

        }

    }

    private fun userDataOnViews(userData: UserData) {

        Glide.with(activity).load(Constant.IMG_URL + userData.profileImg)
            .placeholder(R.drawable.ic_downloading)
            .error(R.drawable.ic_error)
            .into(mBinding.ivProfile)

        mBinding.etFirstName.setText(userData.name)
        mBinding.etMaritalStatus.setText(userData.maritalStatusName)
        mBinding.etAddress.setText(userData.address)
        mBinding.etNumber.setText(userData.phone)
        mBinding.etEmail.setText(userData.email)

    }

    private fun checkCredentials() {
        val maritalStatus: String = mBinding.etMaritalStatus.text.toString().trim()
        val firstName: String = mBinding.etFirstName.text.toString().trim()
        val userAddress: String = mBinding.etAddress.text.toString().trim()
        val userNumber: String = mBinding.etNumber.text.toString().trim()

        if (firstName.isEmpty()) {
            mBinding.etFirstName.error = "First name is required!"
            mBinding.etFirstName.requestFocus()
            return
        }
        if (maritalStatus.isEmpty()) {
            mBinding.etMaritalStatus.error = " Marital status is required!"
            mBinding.etMaritalStatus.requestFocus()
            return
        }

        if (userAddress.isEmpty()) {
            mBinding.etAddress.error = "Address is required!"
            mBinding.etAddress.requestFocus()
            return
        }
        if (userNumber.isEmpty()) {
            mBinding.etNumber.error = "Phone Number is required!"
            mBinding.etNumber.requestFocus()

        } else {

            updateProfile(maritalStatus, firstName, userAddress, userNumber)

        }


    }

    private fun updateProfile(
        maritalStatus: String,
        firstName: String,
        userAddress: String,
        userNumber: String
    ) {

        val dialog = Constant.getDialog(activity)
        dialog.show()



        val mStatus = maritalStatus.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val fName = firstName.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val uAddress = userAddress.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val uNumber = userNumber.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val uId =
            ServiceIds.userId.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())

        val updateUserApi = Constant.getRetrofitInstance().create(ApiCall::class.java)



        val updateUserCall: Call<UpdateProfileResponse> = if (this.uri != null) {

            val file = getRealPathFromURI(this.uri!!)?.let { File(it) }!!

            val filePart = MultipartBody.Part.createFormData(
                "profile_img",
                file.name,
                RequestBody.create("image/*".toMediaTypeOrNull(), file)
            )

            Log.i(TAG, "updateProfile:  $filePart, ${ServiceIds.userId}, $maritalStatus, $firstName, $userAddress, $userNumber")

            updateUserApi.updateUser(filePart, uId, mStatus, fName, uAddress, uNumber)



        } else {


            updateUserApi.updateUser(null, uId, mStatus, fName, uAddress, uNumber)
        }



        CoroutineScope(Dispatchers.IO).launch {

            updateUserCall.enqueue(object : Callback<UpdateProfileResponse> {
                override fun onResponse(
                    call: Call<UpdateProfileResponse>,
                    response: Response<UpdateProfileResponse>
                ) {
                    Log.i(TAG, "onResponse: outer ${response.body()}")
                    if (response.isSuccessful) {
                        dialog.dismiss()

                        Log.i(TAG, "onResponse: successful ${response.body()}")
                        
                        if (response.body() != null && response.body()?.status == true) {
                            val updateProfileData = response.body()!!.data

                            if (updateProfileData != null) {

                                userData = UserData(
                                    updateProfileData.id,
                                    updateProfileData.profileImg,
                                    updateProfileData.maritalStatusName,
                                    updateProfileData.name,
                                    updateProfileData.address,
                                    updateProfileData.phone,
                                    updateProfileData.email,
                                    updateProfileData.emailVerifiedAt,
                                    updateProfileData.deviceToken,
                                    updateProfileData.createdAt,
                                    updateProfileData.updatedAt
                                )

                                ServiceIds.saveUserIntoPref(activity, "userInfo",
                                    userData!!
                                )

                                userData = ServiceIds.fetchUserFromPref(activity, "userInfo")

                                if (userData != null) {

                                    userDataOnViews(userData!!)

                                }

                                Toast.makeText(
                                    activity,
                                    response.body()!!.message,
                                    Toast.LENGTH_SHORT
                                ).show()

                            } else {
                                Toast.makeText(
                                    activity,
                                    "Something went wrong",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                        } else {
                            Toast.makeText(
                                activity,
                                response.body()?.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    } else {
                        dialog.dismiss()
                        Toast.makeText(activity, response.errorBody().toString(), Toast.LENGTH_SHORT)
                            .show()
                    }

                }

                override fun onFailure(call: Call<UpdateProfileResponse>, t: Throwable) {
                    dialog.dismiss()
                    Log.i(TAG, "onResponse: fail ${t.message}")
                    Toast.makeText(activity, t.message, Toast.LENGTH_SHORT).show()
                }

            })
        }

    }

    private fun getRealPathFromURI(contentURI: Uri): String? {
        val result: String?
        val cursor: Cursor? = activity?.contentResolver
            ?.query(contentURI, null, null, null, null)
        if (cursor == null) {
            result = contentURI.path
        } else {
            cursor.moveToFirst()
            val idx: Int = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            result = cursor.getString(idx)
            cursor.close()
        }
        return result
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity = context as MainActivity
    }

}