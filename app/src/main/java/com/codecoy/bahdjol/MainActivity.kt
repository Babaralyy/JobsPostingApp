package com.codecoy.bahdjol


import android.Manifest
import android.content.Context
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.codecoy.bahdjol.constant.Constant.TAG
import com.codecoy.bahdjol.utils.Permissions
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import gun0912.tedimagepicker.util.ToastUtil.showToast


class MainActivity : AppCompatActivity() {

    lateinit var locationManager: LocationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager


        inIt()

    }

    private fun inIt() {

        checkLocationPermissions()


    }

    fun checkLocationPermissions() {

        Dexter.withContext(this)
            .withPermissions(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ).withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    Log.i(TAG, "onPermissionsChecked: checked ")
                    report.let {
                        if (report.areAllPermissionsGranted()) {
                            Log.i(TAG, "onPermissionsChecked:  areAllPermissionsGranted")
                            onMobileGps(this@MainActivity)
                        }
                    }


                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest?>?,
                    token: PermissionToken?
                ) {
                    Log.i(TAG, "onPermissionsChecked: onPermissionRationaleShouldBeShown ")
                    token?.continuePermissionRequest()
                }
            }).check()

    }

    private fun onMobileGps(context: Context) {
        // check whether device is GPS supported or not?
        if (Permissions.hasGPSDevice(locationManager)) {
            // check whether is GPS on or not?
            if (Permissions.isGpsEnabled(locationManager)) {
                // on

            } else {
                // off
                showToast("Gps not enabled")
                // send request to turn on Location(GPS)
                Permissions.enableGPS(context)
            }
        } else {
            // not Supported
            showToast("Gps not Supported")
            //  finish()
        }
    }

}