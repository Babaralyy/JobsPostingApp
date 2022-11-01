package com.codecoy.bahdjol.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.codecoy.bahdjol.datamodels.CheckSubsData
import com.codecoy.bahdjol.datamodels.GetSubsData
import com.codecoy.bahdjol.datamodels.UserData
import com.google.gson.Gson

object ServiceIds {

    var serviceId: Int? = null
    var subServiceId: Int? = null
    var subServicePrice: String? = null
    private var userData: UserData? = null
    private var checkSubsData: CheckSubsData? = null
    var userId: Int? = null

    private lateinit var sharedPreferences: SharedPreferences

    fun saveUserIntoPref(context: Context, userInfo: String, userData: UserData) {

        sharedPreferences = context.getSharedPreferences(userInfo, Context.MODE_PRIVATE)

        val editor: SharedPreferences.Editor = sharedPreferences.edit()

        userId = (userData.id)

        val gson = Gson()
        val json = gson.toJson(userData)

        editor.putString("userData", json)

        Log.i("TAG", "saveInfoIntoPref: user $json")

        editor.apply()
    }

    fun saveBalanceIntoPref(context: Context, balanceInfo: String, currentBalance: String){

        sharedPreferences = context.getSharedPreferences(balanceInfo, Context.MODE_PRIVATE)

        val editor: SharedPreferences.Editor = sharedPreferences.edit()

        editor.putString("balance", currentBalance)

        Log.i("TAG", "saveInfoIntoPref: balance $currentBalance")

        editor.apply()

    }

    fun saveSubsIntoPref(context: Context, subsInfo: String, checkSubsData: CheckSubsData) {

        sharedPreferences = context.getSharedPreferences(subsInfo, Context.MODE_PRIVATE)

        val editor: SharedPreferences.Editor = sharedPreferences.edit()

        val gson = Gson()
        val json = gson.toJson(checkSubsData)

        editor.putString("checkSubsData", json)

        Log.i("TAG", "saveInfoIntoPref: subs $json")

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

    fun fetchBalanceFromPref(context: Context, balanceInfo: String): String? {
        sharedPreferences = context.getSharedPreferences(balanceInfo, Context.MODE_PRIVATE)

        Log.i(
            "TAG",
            "saveInfoIntoPref: get balance ${sharedPreferences.getString("balance", null)}"
        )

        return sharedPreferences.getString("balance", null)
    }

    fun fetchSubsFromPref(context: Context, subsInfo: String): CheckSubsData? {

        sharedPreferences = context.getSharedPreferences(subsInfo, Context.MODE_PRIVATE)

        val gson = Gson()
        val json = sharedPreferences.getString("checkSubsData", null)

        checkSubsData =
            gson.fromJson(json, CheckSubsData::class.java)
        return if (checkSubsData != null) {

            Log.i(
                "TAG",
                "saveInfoIntoPref: checkSubsData $checkSubsData"
            )

            checkSubsData


        } else {
            null
        }

    }

}