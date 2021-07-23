package com.plasdor.app.utils

import android.app.Application
import com.androidnetworking.AndroidNetworking

class PlasdorApplication:Application() {
    override fun onCreate() {
        super.onCreate()
        AndroidNetworking.initialize(getApplicationContext());
    }
}