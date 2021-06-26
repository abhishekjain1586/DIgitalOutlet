package com.digitaloutlet.application

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.res.Resources
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class DOApplication : Application() {

    private lateinit var executorService: ExecutorService
    private lateinit var handler: Handler

    override fun onCreate() {
        super.onCreate()
        _INSTANCE = this

        executorService = Executors.newSingleThreadExecutor()
        handler = Handler(Looper.getMainLooper())
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

    fun getExecutorService(): ExecutorService {
        return executorService
    }

    fun getMainHandler(): Handler {
        return handler
    }
}