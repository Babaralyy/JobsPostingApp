package com.codecoy.bahdjol.ui

import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.codecoy.bahdjol.constant.Constant
import com.codecoy.bahdjol.constant.Constant.TAG
import com.codecoy.bahdjol.databinding.FragmentSignUpBinding
import com.codecoy.bahdjol.datamodels.UserResponse
import com.codecoy.bahdjol.repository.Repository
import com.codecoy.bahdjol.utils.ServiceIds
import com.codecoy.bahdjol.viewmodel.MyViewModel
import com.codecoy.bahdjol.viewmodel.MyViewModelFactory
import gun0912.tedimagepicker.builder.TedImagePicker
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream


class SignUpFragment : Fragment() {

    private lateinit var myViewModel: MyViewModel

    private var uri: Uri? = null
    private lateinit var bitmap: Bitmap
    private lateinit var encodeImageString: String

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


        val repository = Repository()
        val myViewModelFactory = MyViewModelFactory(repository)


        myViewModel =
            ViewModelProvider(this, myViewModelFactory)[MyViewModel::class.java]


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
            requireActivity().contentResolver.openInputStream(this.uri!!)!!
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

        val maritalStatus: String = mBinding.etMaritalStatus.text.toString().trim()
        val firstName: String = mBinding.etFirstName.text.toString().trim()
        val userAddress: String = mBinding.etAddress.text.toString().trim()
        val userNumber: String = mBinding.etNumber.text.toString().trim()
        val userEmail: String = mBinding.etEmail.text.toString().trim()
        val userPassword: String = mBinding.etPassword.text.toString().trim()

        if (this.uri == null) {
            Toast.makeText(requireContext(), "Please select profile image!", Toast.LENGTH_SHORT)
                .show()
            return
        }
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
            signUp(maritalStatus, firstName, userAddress,userNumber, userEmail, userPassword)
        }


    }

    private fun signUp(
        maritalStatus: String,
        firstName: String,
        userAddress: String,
        userNumber: String,
        userEmail: String,
        userPassword: String
    ) {

        val dialog = Constant.getDialog(requireActivity())
        dialog.show()

        val file = getRealPathFromURI(this.uri!!)?.let { File(it) }

        val filePart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "profile_img",
            file?.name,
            RequestBody.create("image/*".toMediaTypeOrNull(), file!!)
        )


        val mStatus = maritalStatus.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val fName = firstName.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val uAddress = userAddress.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val uNumber = userNumber.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val uEmail = userEmail.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val uPassword = userPassword.toRequestBody("multipart/form-data".toMediaTypeOrNull())

        myViewModel.signUpUser(filePart, mStatus, fName, uAddress,uNumber, uEmail, uPassword)

        myViewModel.signUpLiveData.observe(viewLifecycleOwner
        ) {
            dialog.dismiss()
            if (it.status == true && it.data != null) {
                Log.i(TAG, "response: ${it.data}")

                val userData = it.data

                ServiceIds.saveUserIntoPref(requireActivity(), "userInfo", userData!!)
                val action = SignUpFragmentDirections.actionSignUpFragmentToMainFragment()
                findNavController().navigate(action)

            } else {
                Toast.makeText(requireActivity(), it.message, Toast.LENGTH_SHORT).show()
            }
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


}