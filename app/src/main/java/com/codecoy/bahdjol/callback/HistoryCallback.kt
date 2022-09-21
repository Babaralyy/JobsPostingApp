package com.codecoy.bahdjol.callback

import com.codecoy.bahdjol.datamodels.BookingHistoryData

interface HistoryCallback {
    fun onHistoryClick(position: Int, bookingHistoryData: BookingHistoryData)
}