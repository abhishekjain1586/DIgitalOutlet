package com.digitaloutlet.application

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.res.Resources
import android.util.DisplayMetrics

class DOApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        _INSTANCE = this
    }

    companion object {
        lateinit var _INSTANCE: DOApplication
    }

    fun getAppContext(): Context {
        return _INSTANCE.applicationContext
    }

    fun getWindowHeight(): Int {
        val displayMetrics = DisplayMetrics()
        return Resources.getSystem().displayMetrics.heightPixels
    }

    fun getWindowWidth(): Int {
        val displayMetrics = DisplayMetrics()
        return Resources.getSystem().displayMetrics.widthPixels
    }
}