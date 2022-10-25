package com.codecoy.bahdjol.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.codecoy.bahdjol.datamodels.UserData
import com.google.gson.Gson

object ServiceIds {

    var serviceId: Int? = null
    var subServiceId: Int? = null
    private var userData: UserData? = null
    var userId: Int? = null

    private lateinit var sharedPreferences: SharedPreferences

    fun saveUserIntoPref(context: Context, userInfo: String, userData: UserData) {

        sharedPreferences = context.getSharedPreferences(userInfo, Context.MODE_PRIVATE)

        val editor: SharedPreferences.Editor = sharedPreferences.edit()

        userId = (userData.id)

        val gson = Gson()
        val json = gson.toJson(userData)

        editor.putString("userData", json)

        Log.i("TAG", "saveInfoIntoPref: $json")

        editor.apply()
    }

    fun fetchUserFromPref(context: Context, userInfo: String): UserData? {

        sharedPreferences = context.getSharedPreferences(userInfo, Context.MODE_PRIVATE)

        val gson = Gson()
        val json = sharedPreferences.getString("userData", "")

        userData =
            gson.fromJson(json, UserData::class.java)
        return if (userData != null) {
            userId = (userData!!.id)
            Log.i(
                "TAG",
                "saveInfoIntoPref: login_id" + userData!!.id
            )
            userData
        } else {
            null
        }

    }

}