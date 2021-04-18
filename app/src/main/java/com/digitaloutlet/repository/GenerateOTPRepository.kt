package com.digitaloutlet.repository

import com.digitaloutlet.R
import com.digitaloutlet.application.DOApplication
import com.digitaloutlet.model.response.ResGenerateOTP
import com.digitaloutlet.service.ServiceHelper
import com.digitaloutlet.utils.Constants
import com.eros.moviesdb.utils.NetworkUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GenerateOTPRepository {

    private var mListener: OnGenerateOTPListener? = null

    interface OnGenerateOTPListener {
        fun onSuccessGenerateOTP(response: ResGenerateOTP)
        fun onFailureGenerateOTP(errMsg: String)
    }

    fun setListener(listener: OnGenerateOTPListener) {
        mListener = listener
    }

    fun generateOTP(msisdn: String) {
        if (NetworkUtil.isNetworkConnected()) {
            val queryParams = HashMap<String, String>()
            queryParams.put(Constants.MSISDN, msisdn)
            queryParams.put(Constants.channel, Constants.hash_code)

            val call = ServiceHelper.getClient().generateOTP(queryParams)
            call.enqueue(object : Callback<ResGenerateOTP> {
                override fun onResponse(
                    call: Call<ResGenerateOTP>,
                    response: Response<ResGenerateOTP>
                ) {
                    if (response.isSuccessful) {
                        if (response.body() != null) {
                            mListener?.onSuccessGenerateOTP(response.body() as ResGenerateOTP)
                        } else if (response.errorBody() != null) {
                            mListener?.onFailureGenerateOTP(
                                response.errorBody()?.string() ?: Constants.EMPTY
                            )
                        } else {
                            mListener?.onFailureGenerateOTP(DOApplication._INSTANCE.getString(R.string.error_internal_server_error))
                        }
                    } else {
                        mListener?.onFailureGenerateOTP(DOApplication._INSTANCE.getString(R.string.error_internal_server_error))
                    }
                }

                override fun onFailure(call: Call<ResGenerateOTP>, t: Throwable) {
                    mListener?.onFailureGenerateOTP(DOApplication._INSTANCE.getString(R.string.error_oops_something_not_right))
                }
            })
        } else {
            mListener?.onFailureGenerateOTP(DOApplication._INSTANCE.getString(R.string.error_no_internet_connection))
        }
    }
}