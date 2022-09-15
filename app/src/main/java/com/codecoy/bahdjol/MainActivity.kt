package com.codecoy.bahdjol


import android.content.Context
import android.location.LocationRequest
import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY
import java.net.URI.create



class MainActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        inIt()

    }


    private fun inIt() {

    }

    private fun onMobileGps(context: Context) {
        // check whether device is GPS supported or not?
        if (Permissions.hasGPSDevice()) {
            // Supported

            // check whether is GPS on or not?
            if (Permissions.isGpsEnabled()) {
                // on

                // check whether  runtime permissions is enable on not?
                if (Permissions.checkNetworkPermissions(context)) {
                    // Enable
                    try {
                        if(onSwitchPosition != 0){
                            val action = HomeFragmentDirections.actionHomeFragmentToMapFragment(onSwitchPosition)
                            findNavController().navigate(action)
                        }else{
                            showToast(requireContext(),"please select type!")
                        }

                    } catch (e: Exception) {
                    }
                } else {
                    // not enable
                    // request permission now
                    Permissions.requestNetworkPermissions(context)
                }
            } else {
                // off
                showToast(context, "Gps not enabled")
                // send request to turn on Location(GPS)
                Permissions.enableGPS(context)
            }
        } else {
            // not Supported
            showToast(context, "Gps not Supported")
            //  finish()
        }
    }




}