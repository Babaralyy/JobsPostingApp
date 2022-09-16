package com.codecoy.bahdjol.utils


import android.content.Context
import android.content.IntentSender
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import com.codecoy.bahdjol.MainActivity
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.PendingResult
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.*


class Permissions {
    companion object {

        var googleApiClient: GoogleApiClient? = null
        private const val REQUEST_GPS = 23


        fun isGpsEnabled(locationManager: LocationManager): Boolean {
            return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        }


        fun hasGPSDevice(locationManager: LocationManager): Boolean {
            val providers = locationManager.allProviders
            return providers.contains(LocationManager.GPS_PROVIDER)
        }

        fun enableGPS(context: Context) {
            if (googleApiClient == null) {
                googleApiClient = GoogleApiClient.Builder(context).addApi(LocationServices.API)
                    .addConnectionCallbacks(object : GoogleApiClient.ConnectionCallbacks {
                        override fun onConnected(bundle: Bundle?) {
                            Log.i("connected", "connected")
                        }

                        override fun onConnectionSuspended(i: Int) {
                            Log.e("suspended", "suspended")
                            googleApiClient!!.connect()
                        }
                    })
                    .addOnConnectionFailedListener { connectionResult ->
                        Log.e("Location error",
                            "Location error " + connectionResult.errorCode)
                    }.build()
                googleApiClient!!.connect()
                val locationRequest: LocationRequest = LocationRequest.create()
                locationRequest.apply {
                    priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                    interval = 7 * 1000 //30 * 1000
                    fastestInterval = 5 * 1000 //5 * 1000
                }
                val builder: LocationSettingsRequest.Builder = LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest)
                builder.setAlwaysShow(true)
                val result: PendingResult<LocationSettingsResult> =
                    LocationServices.SettingsApi.checkLocationSettings(googleApiClient!!, builder.build())
                result.setResultCallback { result1 ->
                    Log.e("result", "result")
                    val status: Status = result1.status
                    if (status.statusCode === LocationSettingsStatusCodes.RESOLUTION_REQUIRED) {
                        Log.e("RESOLUTION_REQUIRED", "")
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(context as MainActivity, REQUEST_GPS)

                            // getActivity().finish();
                        } catch (e: IntentSender.SendIntentException) {
                            // Ignore the error.
                            Log.e("error", "error")
                        }
                    }
                }
            }
        }

    }
}