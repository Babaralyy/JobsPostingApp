package com.codecoy.bahdjol.ui


import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
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
import com.codecoy.bahdjol.databinding.OrderDialogLayBinding
import com.codecoy.bahdjol.databinding.TimePickerLayoutBinding
import com.codecoy.bahdjol.datamodels.*
import com.codecoy.bahdjol.repository.Repository
import com.codecoy.bahdjol.utils.ServiceIds
import com.codecoy.bahdjol.utils.ServiceIds.serviceId
import com.codecoy.bahdjol.utils.ServiceIds.userId
import com.codecoy.bahdjol.viewmodel.MyViewModel
import com.codecoy.bahdjol.viewmodel.MyViewModelFactory
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import gun0912.tedimagepicker.builder.TedImagePicker
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream
import java.text.Format
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


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
    private var bookingImgList = ArrayList<BookingPics>()
    private lateinit var imageAdapter: ImageAdapter
    private lateinit var file: File
    private lateinit var filePart: MultipartBody.Part

    private lateinit var currentDate: String
    private lateinit var currentTime: String

    private var totalBalance: Double = 0.0
    private  var orderBalance : Double = 0.0

    private lateinit var currentBalance: String

    private var mLatitude: Double? = null
    private var mLongitude: Double? = null

    private var serviceTypeId: Int? = null

    private var userData: UserData? = null

    private lateinit var mBinding: FragmentUserFormBinding

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        mBinding = FragmentUserFormBinding.inflate(inflater)

        inIt()

        return mBinding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun inIt() {

        currentBalance = ServiceIds.fetchBalanceFromPref(requireActivity(), "balanceInfo").toString()

        subCategoryList = ArrayList()
        imageList = arrayListOf()
        updatedImageList = arrayListOf()
        bookingImgList = arrayListOf()


        imageAdapter = ImageAdapter(activity, imageList)
        mBinding.rvImages.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        mBinding.rvImages.setHasFixedSize(true)
        mBinding.rvImages.adapter = imageAdapter

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())


        val repository = Repository()
        val myViewModelFactory = MyViewModelFactory(repository)
        myViewModel = ViewModelProvider(this, myViewModelFactory)[MyViewModel::class.java]

        setDateAndTime()


        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
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

        mBinding.btnSend.setOnClickListener {

            checkCredentials()
        }



        getUserData()

    }

    private fun getUserData() {

        userData = ServiceIds.fetchUserFromPref(requireActivity(), "userInfo")

    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun setDateAndTime() {

        currentDate =
            (mBinding.datePicker.month + 1).toString() + "/" + mBinding.datePicker.dayOfMonth.toString() + "/" + mBinding.datePicker.year.toString()

        currentTime = getTime(mBinding.timePicker.hour, mBinding.timePicker.minute).toString()

        mBinding.tvDate.text = currentDate
        mBinding.tvTime.text = currentTime
    }

    private fun chooseImage() {

        Log.i(TAG, "chooseImage: chooseImage")

        TedImagePicker.with(requireContext()).start { uri -> showSingleImage(uri) }
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
            file.name, RequestBody.create("image/*".toMediaTypeOrNull(), file)
        )

        myViewModel.imageUpload(filePart)

        myViewModel.imageUploadLiveData.observe(
            viewLifecycleOwner
        ) {
            dialog.dismiss()
            if (it.status == true && it.data != null) {

                Log.i(TAG, "uploadImage:--> call counter")

                updatedImageList.add(Constant.IMG_URL + it.data.toString())
                val distinct = updatedImageList.distinct().toMutableList()
                imageAdapter.updateAdapterList(distinct as ArrayList<String>)
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
            (dateBinding.datePicker.month + 1).toString() + "/" + dateBinding.datePicker.dayOfMonth.toString() + "/" +dateBinding.datePicker.year.toString()

        Log.i("TAG", "datePickerDialog: $currentDate")

        dateBinding.datePicker.setOnDateChangedListener { _, p1, p2, p3 ->

            val mMonth: Int = p2 + 1

            currentDate = "$mMonth/$p3/$p1"

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

        currentTime = getTime(timeBinding.timePicker.hour, timeBinding.timePicker.minute).toString()

        timeBinding.timePicker.setOnTimeChangedListener { _, i, i2 ->

            currentTime = getTime(i, i2).toString()

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


    override fun onMapReady(googleMap: GoogleMap) {

        mMap = googleMap

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->

                if (location != null) {
                    showCurrentMarker(location)
                    mLatitude = location.latitude
                    mLongitude = location.longitude
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
        googleMap.uiSettings.isRotateGesturesEnabled = true

    }

    private fun showCurrentMarker(location: Location) {

        val currentLocation = LatLng(location.latitude, location.longitude)
        mMap.addMarker(
            MarkerOptions()
                .position(currentLocation).draggable(true)
        )
        mMap.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(
                    location.latitude, location.longitude
                ), 12f
            )
        )

        mMap.setOnMarkerDragListener(object :GoogleMap.OnMarkerDragListener{

            override fun onMarkerDrag(p0: Marker) {

            }

            override fun onMarkerDragEnd(p0: Marker) {

                mMap.animateCamera(CameraUpdateFactory.newLatLng(p0.position))

                Log.i(TAG, "onMarkerDragEnd: latitude ${p0.position.latitude}")
                Log.i(TAG, "onMarkerDragEnd: longitude ${p0.position.longitude}")
            }

            override fun onMarkerDragStart(p0: Marker) {

            }

        })

    }

    private fun checkCredentials() {

        val serviceDes: String = mBinding.etDes.text.toString().trim()
        val serviceDate: String = mBinding.tvDate.text.toString().trim()
        val serviceTime: String = mBinding.tvTime.text.toString().trim()


        if (mLatitude == null || mLongitude == null) {
            Toast.makeText(
                requireActivity(), "Please add your location!", Toast.LENGTH_SHORT
            ).show()
            return
        }
        if (serviceDes.isEmpty()) {
            Toast.makeText(
                requireActivity(), "Please enter service description!", Toast.LENGTH_SHORT
            ).show()
            return
        }
        if (serviceDate.isEmpty()) {
            Toast.makeText(
                requireActivity(), "Please set date!", Toast.LENGTH_SHORT
            ).show()
            return
        }
        if (serviceTime.isEmpty()) {
            Toast.makeText(
                requireActivity(), "Please set time!", Toast.LENGTH_SHORT
            ).show()
            return
        }
        if (updatedImageList.isEmpty()) {
            Toast.makeText(
                requireActivity(), "Please add service image!", Toast.LENGTH_SHORT
            ).show()

        } else {

            showDialog(serviceDes, serviceDate, serviceTime)


        }
    }

    private fun showDialog(serviceDes: String, serviceDate: String, serviceTime: String) {

        val orderDialogBinding: OrderDialogLayBinding =
            OrderDialogLayBinding.inflate(LayoutInflater.from(requireContext()))

        val dialog = Dialog(requireActivity())
        dialog.setContentView(orderDialogBinding.root)
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        orderDialogBinding.tvPrice.text = ServiceIds.subServicePrice + " $"

        orderDialogBinding.btnContinue.setOnClickListener {

             totalBalance = currentBalance.toDouble()
             orderBalance = ServiceIds.subServicePrice!!.toDouble()

            if (orderBalance > totalBalance){
                Toast.makeText(requireActivity(), "You don't have enough balance. Please recharge your account!", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            } else {
                addBookingOrder(serviceDes, serviceDate, serviceTime)
                dialog.dismiss()
            }


        }

        orderDialogBinding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()

    }

    private fun addBookingOrder(
        serviceDes: String, serviceDate: String, serviceTime: String
    ) {

        val dialog = Constant.getDialog(requireActivity())
        dialog.show()

        bookingImgList.clear()

        for (i in updatedImageList.indices) {
            bookingImgList.add(BookingPics(updatedImageList[i]))
            Log.i(TAG, "addBookingOrder: for loop")
        }

        serviceTypeId = ServiceIds.subServiceId

        Log.i(TAG, "addBookingOrder:   $userId $serviceId $serviceTypeId $serviceDes $mLatitude $mLongitude $serviceDate $serviceTime ${bookingImgList.size}")

        val bookingDetails = BookingDetails(
            userId,
            serviceId,
            serviceTypeId,
            serviceDes,
            mLatitude,
            mLongitude,
            serviceDate,
            serviceTime,
            bookingImgList
        )

        myViewModel.sendBookingDetails(bookingDetails)
        myViewModel.bookingLiveData.observe(
            viewLifecycleOwner
        ) {
            dialog.dismiss()
            Log.i(TAG, "addBookingOrder: Outer ${it.nearestAgentList.size}")
            if (it.status == true) {

                Log.i(TAG, "addBookingOrder: ${it.nearestAgentList.size}")

                totalBalance -= orderBalance

                updateBalance(totalBalance)

                Toast.makeText(requireActivity(), it.message, Toast.LENGTH_SHORT).show()


            } else {
                Log.i(TAG, "addBookingOrder: failed ${it.message}")
                Toast.makeText(requireActivity(), it.message, Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun updateBalance(totalBalance: Double) {

        myViewModel.updateBalance(userData?.id!!, totalBalance.toString())

        myViewModel.updateBalanceLiveData.observe(viewLifecycleOwner
        ) {

            if (it.status == true && it.data != null) {

                Log.i(TAG, "updateBalance: success ${it.message}")

                val walletData = it.data

                ServiceIds.saveBalanceIntoPref(requireActivity(), "balanceInfo",
                    walletData!!.balance!!
                )

            } else {
                Log.i(TAG, "updateBalance: failure ${it.message}")
                Toast.makeText(requireActivity(), it.message, Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun getRealPathFromURI(contentURI: Uri): String? {
        val result: String?
        val cursor: Cursor? = activity.contentResolver?.query(contentURI, null, null, null, null)
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

    private fun getTime(hr: Int, min: Int): String? {
        val cal = Calendar.getInstance()
        cal[Calendar.HOUR_OF_DAY] = hr
        cal[Calendar.MINUTE] = min
        val formatter: Format
        formatter = SimpleDateFormat("h:mm a")
        return formatter.format(cal.time)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity = context as MainActivity
    }

}