package com.codecoy.bahdjol.ui


import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.codecoy.bahdjol.MainActivity
import com.codecoy.bahdjol.R
import com.codecoy.bahdjol.adapter.ImageAdapter
import com.codecoy.bahdjol.constant.Constant
import com.codecoy.bahdjol.constant.Constant.TAG
import com.codecoy.bahdjol.databinding.DatePickerLayoutBinding
import com.codecoy.bahdjol.databinding.FragmentUserFormBinding
import com.codecoy.bahdjol.databinding.TimePickerLayoutBinding
import com.codecoy.bahdjol.datamodels.SubServicesData
import com.codecoy.bahdjol.repository.Repository
import com.codecoy.bahdjol.utils.ServiceIds.serviceId
import com.codecoy.bahdjol.viewmodel.MyViewModel
import com.codecoy.bahdjol.viewmodel.MyViewModelFactory
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import gun0912.tedimagepicker.builder.TedImagePicker
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream


class UserFormFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var myViewModel: MyViewModel

    private lateinit var subServicesDataList: MutableList<SubServicesData>
    private lateinit var subCategoryList: ArrayList<String>
    private lateinit var activity: MainActivity

    private var uri: Uri? = null
    private lateinit var bitmap: Bitmap
    private lateinit var encodeImageString: String

    private lateinit var imageList: MutableList<String>
    private var updatedImageList = ArrayList<String>()
    private lateinit var imageAdapter: ImageAdapter
    private lateinit var file: File
    private lateinit var filePart: MultipartBody.Part

    private lateinit var currentDate: String

    private lateinit var currentTime: String

    private lateinit var mBinding: FragmentUserFormBinding
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        mBinding = FragmentUserFormBinding.inflate(inflater)

        inIt()

        return mBinding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun inIt() {

        subCategoryList = ArrayList()
        imageList = arrayListOf()


        imageAdapter = ImageAdapter(activity, imageList)
        mBinding.rvImages.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        mBinding.rvImages.setHasFixedSize(true)
        mBinding.rvImages.adapter = imageAdapter

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())


        val repository = Repository()
        val myViewModelFactory = MyViewModelFactory(repository)
        myViewModel =
            ViewModelProvider(this, myViewModelFactory)[MyViewModel::class.java]

        subServices()


        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        mBinding.ivAddImage.setOnClickListener {

            chooseImage()

        }


        mBinding.dateLay.setOnClickListener {
            datePickerDialog()
        }
        mBinding.timeLay.setOnClickListener {
            timePickerDialog()
        }

    }

    private fun chooseImage() {

        Log.i(TAG, "chooseImage: chooseImage")

        TedImagePicker.with(requireContext())
            .start { uri -> showSingleImage(uri) }
    }

    private fun showSingleImage(mUri: Uri) {
        Log.i(TAG, "chooseImage: showSingleImage")
        val dialog = Constant.getDialog(requireActivity())
        dialog.show()

        this.uri = mUri

        val inputStream: InputStream =
            requireActivity().contentResolver.openInputStream(this.uri!!)!!
        bitmap = BitmapFactory.decodeStream(inputStream)

        encodeBitmapImage(bitmap, dialog)

    }

    private fun encodeBitmapImage(bitmap: Bitmap?, dialog: Dialog) {

        Log.i(TAG, "chooseImage: encodeBitmapImage")

        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val bytesOfImage: ByteArray = byteArrayOutputStream.toByteArray()
        encodeImageString = Base64.encodeToString(bytesOfImage, Base64.DEFAULT)

        uploadImage(dialog)

    }

    private fun uploadImage(dialog: Dialog) {

        Log.i(TAG, "chooseImage: uploadImage")

        file = getRealPathFromURI(this.uri!!)?.let { File(it) }!!

        filePart = MultipartBody.Part.createFormData(
            "img",
            file.name,
            RequestBody.create("image/*".toMediaTypeOrNull(), file!!)
        )

        myViewModel.imageUpload(filePart)

        myViewModel.imageUploadLiveData.observe(
            viewLifecycleOwner
        ) {
            dialog.dismiss()
            if (it.status == true && it.data != null) {

                Log.i(TAG, "uploadImage:--> call counter")

                updatedImageList.add(Constant.IMG_URL + it.data.toString())
                imageAdapter.updateAdapterList(updatedImageList)
            } else {
                Toast.makeText(requireActivity(), it.message, Toast.LENGTH_SHORT).show()
            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun datePickerDialog() {

        val dateBinding: DatePickerLayoutBinding =
            DatePickerLayoutBinding.inflate(LayoutInflater.from(requireContext()))

        val dialog = Dialog(requireContext())
        dialog.setContentView(dateBinding.root)
        dialog.setCancelable(false)


        currentDate =
            dateBinding.datePicker.year.toString() + "-" + (dateBinding.datePicker.month + 1).toString() + "-" + dateBinding.datePicker.dayOfMonth.toString()

        Log.i("TAG", "datePickerDialog: $currentDate")

        dateBinding.datePicker.setOnDateChangedListener { _, p1, p2, p3 ->

            val mMonth: Int = p2 + 1

            currentDate = "$p1-$mMonth-$p3"

            Log.i("TAG", "changeDate: $currentDate")
        }

        dateBinding.btnDone.setOnClickListener {

            mBinding.tvDate.text = currentDate

            dialog.dismiss()

        }

        dateBinding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()


    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun timePickerDialog() {

        val timeBinding: TimePickerLayoutBinding =
            TimePickerLayoutBinding.inflate(LayoutInflater.from(requireContext()))

        val dialog = Dialog(requireContext())
        dialog.setContentView(timeBinding.root)
        dialog.setCancelable(false)

        currentTime =
            timeBinding.timePicker.hour.toString() + ":" + timeBinding.timePicker.minute.toString()

        timeBinding.timePicker.setOnTimeChangedListener { _, i, i2 ->

            currentTime = "$i:$i2"

            Log.i(TAG, "timePickerDialog: $i $i2 ")

        }


        timeBinding.btnDone.setOnClickListener {

            mBinding.tvTime.text = currentTime

            dialog.dismiss()

        }

        timeBinding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()

    }

    private fun subServices() {
        val dialog = Constant.getDialog(requireActivity())
        dialog.show()

        myViewModel.subServices(serviceId!!)

        myViewModel.subServicesLiveData.observe(
            viewLifecycleOwner
        ) {
            dialog.dismiss()

            Log.i(TAG, "response: outer ${it.data.size}")

            if (it.status == true && it.data.isNotEmpty()) {

                Log.i(Constant.TAG, "response: success ${it.data.size}")

                Toast.makeText(activity, it.data.size.toString(), Toast.LENGTH_SHORT).show()

                subServicesDataList = it.data

                setAutoComplete(subServicesDataList as ArrayList<SubServicesData>)


            } else {
                Toast.makeText(requireActivity(), it.message, Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun setAutoComplete(subServicesList: ArrayList<SubServicesData>) {

        for (i in subServicesList) {
            subCategoryList.add(i.subcategoryName.toString())
        }

        val arrayAdapter = ArrayAdapter(requireActivity(), R.layout.dropdown_item, subCategoryList)

        mBinding.autoCompleteTextView.setAdapter(arrayAdapter)

    }

    override fun onMapReady(googleMap: GoogleMap) {

        mMap = googleMap

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->

                if (location != null) {
                    showCurrentMarker(location)
                }

            }

        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            activity.checkLocationPermissions()
        } else {
            googleMap.isMyLocationEnabled = true
        }

        googleMap.uiSettings.isMyLocationButtonEnabled = true
        googleMap.uiSettings.isZoomGesturesEnabled = true
        googleMap.uiSettings.isZoomControlsEnabled = true
        googleMap.uiSettings.isScrollGesturesEnabled = true


    }

    private fun showCurrentMarker(location: Location) {

        val currentLocation = LatLng(location.latitude, location.longitude)
        mMap.addMarker(
            MarkerOptions()
                .position(currentLocation)
        )
        mMap.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(
                    location.latitude,
                    location.longitude
                ), 12f
            )
        )

    }

    private fun getRealPathFromURI(contentURI: Uri): String? {
        val result: String?
        val cursor: Cursor? = activity.contentResolver
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