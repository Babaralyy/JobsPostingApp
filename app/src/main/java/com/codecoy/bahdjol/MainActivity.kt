package com.codecoy.bahdjol


import android.Manifest
import android.content.Context
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.codecoy.bahdjol.constant.Constant.TAG
import com.codecoy.bahdjol.ui.NotificationFragment
import com.codecoy.bahdjol.ui.ServicesFragment
import com.codecoy.bahdjol.utils.GlobalClass
import com.codecoy.bahdjol.utils.Permissions
import com.codecoy.bahdjol.utils.ServiceIds
import com.google.android.material.navigation.NavigationView
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import gun0912.tedimagepicker.util.ToastUtil.showToast
import okhttp3.internal.notify


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    lateinit var locationManager: LocationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager


        inIt()

    }

    private fun inIt() {

        val extras = intent.extras
        val mNotifi: String


        if (extras != null) {

            mNotifi = ServiceIds.fetchNotifiInfo(this).toString()

            ServiceIds.notId = mNotifi

            Log.i(TAG, "inIt: ServiceIds $mNotifi")

        } else {
            mNotifi = ServiceIds.fetchNotifiInfo(this).toString()

            Log.i(TAG, "inIt: ServiceIds else $mNotifi")

        }

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

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager: FragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLay, fragment)
        fragmentTransaction.commit()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
       return false
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {

        val fManager = supportFragmentManager.findFragmentById(R.id.frameLay)

        if (GlobalClass.drawer != null) {
            if (GlobalClass.drawer!!.isDrawerOpen(Gravity.LEFT)) {
                GlobalClass.drawer!!.close()
            } else if (fManager !is ServicesFragment) {
                GlobalClass.bottomNavigation.selectedItemId = R.id.iHome
            } else {
                finish()
            }
        } else {
            super.onBackPressed()
        }
    }

}