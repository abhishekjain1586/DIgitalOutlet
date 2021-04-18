package com.digitaloutlet.utils

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import com.digitaloutlet.R
import com.digitaloutlet.application.DOApplication
import com.eros.moviesdb.utils.NetworkUtil
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.api.net.*
import java.util.*


class GeocoderUtil {

    private var mContext: Context
    private var mListener: OnGeocoderAddressListener? =null

    interface OnGeocoderAddressListener {
        fun onAddressReceived(addressLst: List<Address>)
        fun onError()
    }

    private constructor(context: Context) {
        mContext = context
    }

    companion object {
        var instance: GeocoderUtil? = null

        fun getInstance(context: Context): GeocoderUtil {
            if (instance == null) {
                instance = GeocoderUtil(context)
            }
            return instance!!
        }
    }

    private val mHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            val bundle = msg.data
            val addressLst = bundle.getParcelableArrayList<Address>("address_data")
            if (!addressLst.isNullOrEmpty()) {
                mListener?.onAddressReceived(addressLst)
            } else {
                mListener?.onError()
            }
        }
    }

    fun getAddress(strAddress: String, listener: OnGeocoderAddressListener?) {
        mListener = listener

        val geocoder = Geocoder(mContext, Locale.getDefault())
        if (NetworkUtil.isNetworkConnected()) {
            Thread(object : Runnable {
                override fun run() {
                    var address: ArrayList<Address>? = null
                    val msg = Message()
                    try { // try/catch is used when Network is on/off constantly. getFromLocationName causes Crash.
                        address = geocoder.getFromLocationName(strAddress, 5) as ArrayList<Address>
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                    val bundle = Bundle()
                    msg.data = bundle
                    if (!address.isNullOrEmpty()) {
                        bundle.putParcelableArrayList("address_data", address)
                    }
                    mHandler.sendMessage(msg)
                }
            }).start()
        } else {
            mListener?.onError()
        }
    }

    fun getAddressFromPlacesAPI(searchQuery: String) {
        if (!Places.isInitialized()) {
            Places.initialize(DOApplication._INSTANCE.applicationContext, DOApplication._INSTANCE.resources.getString(
                R.string.maps_api_key));
        }
        val placesClient = Places.createClient(mContext)

        val token = AutocompleteSessionToken.newInstance()

        val request = FindAutocompletePredictionsRequest.builder()
            .setTypeFilter(TypeFilter.ADDRESS)
            .setQuery(searchQuery)
            .setSessionToken(token)
            .build()

        val task: Task<FindAutocompletePredictionsResponse> = placesClient.findAutocompletePredictions(request)
        task.addOnSuccessListener(object : OnSuccessListener<FindAutocompletePredictionsResponse> {
            override fun onSuccess(response: FindAutocompletePredictionsResponse?) {
                response?.let {
                    for (prediction in it.autocompletePredictions) {
                        Log.d("Predictions", ""+prediction.getFullText(null));
                    }
                }
            }
        })

        task.addOnFailureListener(object : OnFailureListener {
            override fun onFailure(e: java.lang.Exception) {
                if (e is ApiException) {
                    Log.d("Predictions", e.message);
                }
            }

        })
    }
}