package com.codecoy.bahdjol.callback

import com.codecoy.bahdjol.datamodels.NewReqData

interface StatusCallBack {
    fun onAcceptClick(position: Int, newReqData: NewReqData)
    fun onDeclineClick(position: Int, newReqData: NewReqData)
}