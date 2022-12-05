package com.codecoy.bahdjol.callback

import com.codecoy.bahdjol.datamodels.AgentNotificationData
import com.codecoy.bahdjol.datamodels.NotificationData

interface NotificationCallback {
    fun onNotificationClick(position: Int, notificationData: NotificationData)
    fun onAgentNotificationClick(position: Int, notificationData: AgentNotificationData)
}