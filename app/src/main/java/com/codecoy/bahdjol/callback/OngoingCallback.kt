package com.codecoy.bahdjol.callback

import com.codecoy.bahdjol.datamodels.OngoingReqData

interface OngoingCallback {
    fun onCompleteClick(position: Int, ongoingReqData: OngoingReqData)
}