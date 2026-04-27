package com.supplylens.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class SupplyLensApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Any app-level initialization can go here
    }
}