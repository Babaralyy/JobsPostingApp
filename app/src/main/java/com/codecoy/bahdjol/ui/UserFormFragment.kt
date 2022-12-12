package com.codecoy.bahdjol.ui


import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.Intent
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
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.codecoy.bahdjol.MainActivity
import com.codecoy.bahdjol.R
import com.codecoy.bahdjol.adapter.ImageAdapter
import com.codecoy.bahdjol.callback.CancelCallback
import com.codecoy.bahdjol.constant.Constant
import com.codecoy.bahdjol.constant.Constant.TAG
import com.codecoy.bahdjol.databinding.*
import com.codecoy.bahdjol.datamodels.*
import com.codecoy.bahdjol.network.ApiCall
import com.codecoy.bahdjol.repository.Repository
import com.codecoy.bahdjol.utils.ServiceIds
import com.codecoy.bahdjol.utils.ServiceIds.serviceId
import com.codecoy.bahdjol.utils.ServiceIds.userId
import com.codecoy.bahdjol.utils.isNetworkConnected
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
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream
import java.text.Format
import java.text.SimpleDateFormat
import java.util.*


class UserFormFragment : Fragment(), OnMapReadyCallback, CancelCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var myViewModel: MyViewModel

    private lateinit var subCategoryList: ArrayList<String>
    private lateinit var activity: MainActivity

    private var uri: Uri? = null
    private lateinit var bitmap: Bitmap
    private lateinit var encodeImageString: String

    private lateinit var imageList: MutableList<String>

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
    private var checkSubsData: CheckSubsData? = null


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

        currentBalance = ServiceIds.fetchBalanceFromPref(activity, "balanceInfo").toString()

        subCategoryList = ArrayList()
        imageList = arrayListOf()
        bookingImgList = arrayListOf()


        mBinding.rvImages.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        mBinding.rvImages.setHasFixedSize(true)


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)


        val repository = Repository()
        val myViewModelFactory = MyViewModelFactory(repository)
        myViewModel = ViewModelProvider(this, myViewModelFactory)[MyViewModel::class.java]

        setDateAndTime()


        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        mBinding.toolBar.setNavigationOnClickListener {
            replaceFragment(SubServicesFragment())
        }

        mBinding.ivAddImage.setOnClickListener {

            if (activity.isNetworkConnected()) {
                chooseImage()
            } else {
                Toast.makeText(
                    activity,
                    "Connect to the internet and try again",
                    Toast.LENGTH_SHORT
                ).show()
            }

        }

        mBinding.dateLay.setOnClickListener {
            datePickerDialog()
        }
        mBinding.timeLay.setOnClickListener {
            timePickerDialog()
        }

        mBinding.btnSend.setOnClickListener {
            if (activity.isNetworkConnected()) {
                checkCredentials()
            } else {
                showCallDialog()
            }

        }



        getUserData()

    }

    private fun getUserData() {

        userData = ServiceIds.fetchUserFromPref(activity, "userInfo")

        checkSubsData = ServiceIds.fetchSubsFromPref(activity, "subsInfo")

    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun setDateAndTime() {

        currentDate =
            (mBinding.datePicker.month + 1).toString() + "/" + mBinding.datePicker.dayOfMonth.toString() + "/" + mBinding.datePicker.year.toString()

        currentTime = getTime(mBinding.timePicker.hour, mBinding.timePicker.minute).toString()

        currentDate = customDateFormat(currentDate)

        mBinding.tvDate.text = currentDate
        mBinding.tvTime.text = currentTime
    }

    private fun chooseImage() {

        Log.i(TAG, "chooseImage: chooseImage")

        TedImagePicker.with(requireContext()).start { uri -> showSingleImage(uri) }
    }

    private fun showSingleImage(mUri: Uri) {
        Log.i(TAG, "chooseImage: showSingleImage")
        val dialog = Constant.getDialog(activity)
        dialog.show()

        this.uri = mUri

        val inputStream: InputStream =
            activity.contentResolver.openInputStream(this.uri!!)!!
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

        val uploadImgApi = Constant.getRetrofitInstance().create(ApiCall::class.java)
        lifecycleScope.launch {
            val uploadImgCall = withContext(Dispatchers.IO) { uploadImgApi.uploadImage(filePart) }

            Log.i(TAG, "uploadImage: $uploadImgCall")

            if (uploadImgCall.isSuccessful) {
                dialog.dismiss()

                Log.i(TAG, "uploadImage: isSuccessful ")

                val uploadResponse = uploadImgCall.body()

                if (uploadResponse != null) {

                    Log.i(TAG, "uploadImage: isSuccessful ${uploadResponse.data}")

                    imageList.add(uploadResponse.data!!)

                    setRecyclerView()

                } else {
                    Toast.makeText(activity, "Something went wrong!", Toast.LENGTH_SHORT).show()
                }

            } else {
                dialog.dismiss()
                Toast.makeText(activity, "Something went wrong!", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun setRecyclerView() {
        imageAdapter = ImageAdapter(activity, imageList, this)
        mBinding.rvImages.adapter = imageAdapter
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

        currentDate = customDateFormat(currentDate)

        Log.i("TAG", "datePickerDialog: $currentDate")

        dateBinding.datePicker.setOnDateChangedListener { _, p1, p2, p3 ->

            val mMonth: Int = p2 + 1

            currentDate = "$mMonth/$p3/$p1"

            currentDate = customDateFormat(currentDate)

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
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                activity,
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
                activity, "Please add your location!", Toast.LENGTH_SHORT
            ).show()
            return
        }
        if (serviceDes.isEmpty()) {
            Toast.makeText(
                activity, "Please enter service description!", Toast.LENGTH_SHORT
            ).show()
            return
        }
        if (serviceDate.isEmpty()) {
            Toast.makeText(
                activity, "Please set date!", Toast.LENGTH_SHORT
            ).show()
            return
        }
        if (serviceTime.isEmpty()) {
            Toast.makeText(
                activity, "Please set time!", Toast.LENGTH_SHORT
            ).show()
            return
        }
        if (imageList.isEmpty()) {

            Toast.makeText(
                activity, "Please add service image!", Toast.LENGTH_SHORT
            ).show()

            return
        } else {

            if (checkSubsData != null){

                showSubsDialog(checkSubsData!!, serviceDes, serviceDate, serviceTime)

            } else {
                Log.i(TAG, "checkCredentials: isNetworkConnected")

                showDialog(serviceDes, serviceDate, serviceTime)
            }

        }
    }

    private fun showSubsDialog(checkSubsData: CheckSubsData, serviceDes: String, serviceDate: String, serviceTime: String) {

        Log.i(TAG, "checkCredentials: showDialog")

        val subsInfoDialogBinding: SubsInfoDialogLayBinding =
            SubsInfoDialogLayBinding.inflate(LayoutInflater.from(requireContext()))

        val dialog = Dialog(activity)
        dialog.setContentView(subsInfoDialogBinding.root)
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)


        subsInfoDialogBinding.tvMember.text = "You have ${checkSubsData.pkgName} Subscription"
        subsInfoDialogBinding.tvPrice.text = "Price: ${checkSubsData.pkgPrice}"
        subsInfoDialogBinding.tvStart.text = "Start date: ${checkSubsData.startDate}"
        subsInfoDialogBinding.tvEnd.text = "End date: ${checkSubsData.endDate}"
        subsInfoDialogBinding.tvOrders.text = "Remaining orders: ${checkSubsData.orders}"

        subsInfoDialogBinding.btnContinue.setOnClickListener {

            addBookingOrder(serviceDes, serviceDate, serviceTime)
            dialog.dismiss()
        }

        subsInfoDialogBinding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()

    }


    private fun showDialog(serviceDes: String, serviceDate: String, serviceTime: String) {


        Log.i(TAG, "checkCredentials: showDialog")

        val orderDialogBinding: OrderDialogLayBinding =
            OrderDialogLayBinding.inflate(LayoutInflater.from(requireContext()))

        val dialog = Dialog(activity)
        dialog.setContentView(orderDialogBinding.root)
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        orderDialogBinding.tvPrice.text = ServiceIds.subServicePrice + " $"

        orderDialogBinding.btnContinue.setOnClickListener {

             totalBalance = currentBalance.toDouble()
             orderBalance = ServiceIds.subServicePrice!!.toDouble()

            if (orderBalance > totalBalance){
                Toast.makeText(activity, "You don't have enough balance. Please recharge your account!", Toast.LENGTH_SHORT).show()
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

    private fun showCallDialog() {

        val callDialogBinding: CallDialogLayBinding =
            CallDialogLayBinding.inflate(LayoutInflater.from(requireContext()))

        val dialog = Dialog(activity)
        dialog.setContentView(callDialogBinding.root)
        dialog.setCancelable(true)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        callDialogBinding.btnContinue.setOnClickListener {
            val intent = Intent(
                Intent.ACTION_DIAL,
                Uri.parse("tel:" + callDialogBinding.tvNumber.text.toString())
            )
            startActivity(intent)
            dialog.dismiss()
        }

        dialog.show()

    }

    private fun addBookingOrder(
        serviceDes: String, serviceDate: String, serviceTime: String
    ) {

        val dialog = Constant.getDialog(activity)
        dialog.show()

        bookingImgList.clear()

        for (i in imageList.indices) {
            bookingImgList.add(BookingPics(imageList[i]))
            Log.i(TAG, "addBookingOrder: for loop")
        }

        serviceTypeId = ServiceIds.subServiceId

        Log.i(
            TAG,
            "addBookingOrder:   $userId $serviceId $serviceTypeId $serviceDes $mLatitude $mLongitude $serviceDate $serviceTime ${bookingImgList.size}"
        )

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
            activity
        ) {
            dialog.dismiss()
            Log.i(TAG, "addBookingOrder: Outer ${it.nearestAgentList.size}")
            if (it.status == true) {

                Log.i(TAG, "addBookingOrder: ${it.nearestAgentList.size}")

                Toast.makeText(activity, it.message, Toast.LENGTH_SHORT).show()

                replaceFragment(ServicesFragment())


            } else {
                Log.i(TAG, "addBookingOrder: failed ${it.message}")
                Toast.makeText(activity, it.message, Toast.LENGTH_SHORT).show()
            }
        }

    }

//    private fun updateBalance(totalBalance: Double) {
//
//        myViewModel.updateBalance(userData?.id!!, totalBalance.toString())
//
//        myViewModel.updateBalanceLiveData.observe(activity
//        ) {
//
//            if (it.status == true && it.data != null) {
//
//                Log.i(TAG, "updateBalance: success ${it.message}")
//
//                val walletData = it.data
//
//                ServiceIds.saveBalanceIntoPref(activity, "balanceInfo",
//                    walletData!!.balance!!
//                )
//
//            } else {
//                Log.i(TAG, "updateBalance: failure ${it.message}")
//                Toast.makeText(activity, it.message, Toast.LENGTH_SHORT).show()
//            }
//        }
//
//    }

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

    private fun customDateFormat(dateSchedule: String): String {

        val originalFormat = SimpleDateFormat("MM/dd/yyyy")
        val targetFormat = SimpleDateFormat("MM/dd/yyyy")

        val date: Date = originalFormat.parse(dateSchedule) as Date

        Log.i("TAG", "datePickerDialog: originalFormat ${originalFormat.format(date)}")
        Log.i("TAG", "datePickerDialog: targetFormat ${targetFormat.format(date)}")

        return targetFormat.format(date)

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity = context as MainActivity
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager: FragmentManager = activity.supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLay, fragment)
        fragmentTransaction.commit()
    }

    override fun onImageCancelClick(position: Int) {
        imageList.removeAt(position)
        imageAdapter.notifyDataSetChanged()
    }

}