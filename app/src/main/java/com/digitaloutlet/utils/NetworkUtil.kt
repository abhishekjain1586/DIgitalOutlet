package com.eros.moviesdb.utils

import android.content.Context
import android.net.ConnectivityManager
import com.digitaloutlet.application.DOApplication

object NetworkUtil {

    fun isNetworkConnected() : Boolean {
        var cm = DOApplication._INSTANCE.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm.activeNetworkInfo != null && cm.activeNetworkInfo.isConnected;
    }

}