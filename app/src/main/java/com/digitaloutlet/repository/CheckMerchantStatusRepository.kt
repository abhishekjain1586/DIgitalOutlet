package com.digitaloutlet.repository

import com.digitaloutlet.R
import com.digitaloutlet.application.DOApplication
import com.digitaloutlet.model.response.ResCheckMerchantStatus
import com.digitaloutlet.service.ServiceHelper
import com.digitaloutlet.utils.Constants
import com.eros.moviesdb.utils.NetworkUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CheckMerchantStatusRepository {

    var mListener: OnCheckMerchantStatusListener? = null

    interface OnCheckMerchantStatusListener {
        fun onSuccessCheckMerchantStatus(response: ResCheckMerchantStatus)
        fun onFailureCheckMerchantStatus(errMsg: String)
    }

    fun setListener(listener: OnCheckMerchantStatusListener) {
        mListener = listener
    }

    fun checkMerchantRegistration(msisdn: String) {
        if (NetworkUtil.isNetworkConnected()) {
            val queryParams = HashMap<String, String>()
            queryParams.put(Constants.MSISDN, msisdn)
            queryParams.put(Constants.channel, Constants.channel_apk)

            val call = ServiceHelper.getClient().checkMerchantRegistration(queryParams)
            call.enqueue(object : Callback<ResCheckMerchantStatus> {
                override fun onResponse(call: Call<ResCheckMerchantStatus>, response: Response<ResCheckMerchantStatus>) {
                    if (response.isSuccessful) {
                        if (response.body() != null) {
                            mListener?.onSuccessCheckMerchantStatus(response.body() as ResCheckMerchantStatus)
                        } else if (response.errorBody() != null) {
                            mListener?.onFailureCheckMerchantStatus(response.errorBody()?.string() ?: Constants.EMPTY)
                        } else {
                            mListener?.onFailureCheckMerchantStatus(DOApplication._INSTANCE.getString(R.string.error_internal_server_error))
                        }
                    } else {
                        mListener?.onFailureCheckMerchantStatus(DOApplication._INSTANCE.getString(R.string.error_internal_server_error))
                    }
                }

                override fun onFailure(call: Call<ResCheckMerchantStatus>, t: Throwable) {
                    mListener?.onFailureCheckMerchantStatus(DOApplication._INSTANCE.getString(R.string.error_oops_something_not_right))
                }
            })
        } else {
            mListener?.onFailureCheckMerchantStatus(DOApplication._INSTANCE.getString(R.string.error_no_internet_connection))
        }
    }
}