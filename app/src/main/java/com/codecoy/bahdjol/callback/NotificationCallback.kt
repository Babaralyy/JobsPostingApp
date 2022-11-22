package com.codecoy.bahdjol.callback

import com.codecoy.bahdjol.datamodels.BookingHistoryData

interface NotificationCallback {
    fun onNotificationClick(position: Int)
}