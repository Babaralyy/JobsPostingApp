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
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.codecoy.bahdjol.MainActivity
import com.codecoy.bahdjol.constant.Constant
import com.codecoy.bahdjol.constant.Constant.TAG
import com.codecoy.bahdjol.databinding.FragmentSignUpBinding
import com.codecoy.bahdjol.datamodels.UserResponse
import com.codecoy.bahdjol.network.ApiCall
import com.codecoy.bahdjol.utils.ServiceIds
import com.codecoy.bahdjol.utils.isNetworkConnected
import gun0912.tedimagepicker.builder.TedImagePicker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream


class SignUpFragment : Fragment() {

    private var uri: Uri? = null
    private lateinit var bitmap: Bitmap
    private lateinit var encodeImageString: String
    private var filePart: MultipartBody.Part? = null

    private lateinit var activity: MainActivity

    private lateinit var mBinding: FragmentSignUpBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentSignUpBinding.inflate(inflater)

        inIt()

        return mBinding.root
    }

    private fun inIt() {

        mBinding.btnSignUp.setOnClickListener {


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

        Log.i(TAG, "signUp --> encodeBitmapImage: $encodeImageString")

    }

    private fun checkCredentials() {


        val firstName: String = mBinding.etFirstName.text.toString().trim()
        val lastName: String = mBinding.etLastName.text.toString().trim()
        val userAddress: String = mBinding.etAddress.text.toString().trim()
        val userNumber: String = mBinding.etNumber.text.toString().trim()
        val userEmail: String = mBinding.etEmail.text.toString().trim()
        val userPassword: String = mBinding.etPassword.text.toString().trim()

        if (firstName.isEmpty()) {
            mBinding.etFirstName.error = "First name is required!"
            mBinding.etFirstName.requestFocus()
            return
        }
        if (lastName.isEmpty()) {
            mBinding.etLastName.error = "Last name is required!"
            mBinding.etLastName.requestFocus()
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
            return
        }
        if (userEmail.isEmpty()) {
            mBinding.etEmail.error = "Email is required!"
            mBinding.etEmail.requestFocus()
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
            mBinding.etEmail.error = "Please provide valid email!"
            mBinding.etEmail.requestFocus()
            return
        }
        if (userPassword.isEmpty()) {
            mBinding.etPassword.error = "Password can't be empty!"
            mBinding.etPassword.requestFocus()
            return
        }
        if (userPassword.length < 8) {
            mBinding.etPassword.error = "Password should be greater than 8 characters!"
            mBinding.etPassword.requestFocus()
        } else {


            if (activity.isNetworkConnected()) {
                signUp(firstName, lastName, userAddress, userNumber, userEmail, userPassword)
            } else {
                Toast.makeText(
                    activity,
                    "Connect to the Internet and try again!",
                    Toast.LENGTH_SHORT
                ).show()
            }

        }


    }

    private fun signUp(
        firstName: String,
        lastName: String,
        userAddress: String,
        userNumber: String,
        userEmail: String,
        userPassword: String
    ) {
        val dialog = Constant.getDialog(activity)
        dialog.show()

        if(this.uri != null){
            
            val file = getRealPathFromURI(this.uri!!)?.let { File(it) }
             filePart = MultipartBody.Part.createFormData(
                "profile_img",
                file?.name,
                RequestBody.create("image/*".toMediaTypeOrNull(), file!!)
            )
            
        }
        
        val fName = firstName.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val lName = lastName.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val uAddress = userAddress.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val uNumber = userNumber.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val uEmail = userEmail.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val uPassword = userPassword.toRequestBody("multipart/form-data".toMediaTypeOrNull())


        val signUpApi = Constant.getRetrofitInstance().create(ApiCall::class.java)
        CoroutineScope(Dispatchers.IO).launch {
            val signUpCall =
                signUpApi.createUser(filePart, fName, lName, uAddress, uNumber, uEmail, uPassword)
            signUpCall.enqueue(object : retrofit2.Callback<UserResponse> {
                override fun onResponse(
                    call: Call<UserResponse>,
                    response: Response<UserResponse>
                ) {

                    Log.i(TAG, "onResponse: signup $response")

                    if (response.isSuccessful) {
                        dialog.dismiss()

                        if (response.body() != null && response.body()?.status == true) {
                            val userData = response.body()!!.data[0]

                            ServiceIds.saveUserIntoPref(activity, "userInfo", userData)

                            Toast.makeText(
                                activity,
                                response.body()!!.message,
                                Toast.LENGTH_SHORT
                            ).show()

                            val action =
                                SignUpFragmentDirections.actionSignUpFragmentToMainFragment()
                            findNavController().navigate(action)

                        } else {
                            Toast.makeText(
                                activity,
                                response.body()?.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    } else {
                        dialog.dismiss()
                        Toast.makeText(activity, response.message(), Toast.LENGTH_SHORT)
                            .show()
                    }
                }

                override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                    dialog.dismiss()
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