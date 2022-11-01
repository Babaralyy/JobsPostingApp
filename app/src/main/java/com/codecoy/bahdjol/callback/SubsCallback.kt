package com.codecoy.bahdjol.callback

import com.codecoy.bahdjol.datamodels.SubsData

interface SubsCallback {
    fun onSubsClick(position: Int, subsData: SubsData)
}