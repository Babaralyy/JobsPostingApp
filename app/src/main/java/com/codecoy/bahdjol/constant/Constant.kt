package com.codecoy.bahdjol.constant

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import com.codecoy.bahdjol.R
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object Constant {

    const val TAG = "TAG"
    private const val BASE_URL = "https://wh717090.ispot.cc/bahdjol/"
    const val IMG_URL = "https://wh717090.ispot.cc/bahdjol/public/storage/"

    var httpClient = OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS) // connect timeout
        .writeTimeout(10, TimeUnit.SECONDS) // write timeout
        .readTimeout(10, TimeUnit.SECONDS) // read timeout

    val gson: Gson = GsonBuilder()
        .setLenient()
        .create()

    fun getRetrofitInstance(): Retrofit {
        return Retrofit.Builder().baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(httpClient.build()).build()

    }

    fun getDialog(context: Context): Dialog {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.dialog_lay)
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return dialog
    }


}