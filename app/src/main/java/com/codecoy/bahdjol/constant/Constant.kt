package com.codecoy.bahdjol.constant

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import com.codecoy.bahdjol.R
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Constant {

    const val TAG = "TAG"
    private const val BASE_URL = "https://wh717090.ispot.cc/bahdjol/"
    const val IMG_URL = "https://wh717090.ispot.cc/bahdjol/public/storage/"


    fun getRetrofitInstance(): Retrofit {
        return Retrofit.Builder().baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()).build()

    }

    fun getDialog(context: Context): Dialog {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.dialog_lay)
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return dialog
    }


}