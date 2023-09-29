package com.example.sampleshoppingcart

import android.app.Application
import com.example.sampleshoppingcart.util.SharedPreferencesManager

class MainApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        SharedPreferencesManager.init(applicationContext)
    }
}
