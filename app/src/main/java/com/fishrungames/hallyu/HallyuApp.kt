package com.fishrungames.hallyu

import android.app.Application
import com.vk.sdk.VKSdk

class HallyuApp: Application() {

    override fun onCreate() {
        super.onCreate()
        VKSdk.initialize(this)
    }

}