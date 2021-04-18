package com.digitaloutlet.repository

import com.digitaloutlet.R
import com.digitaloutlet.application.DOApplication
import com.digitaloutlet.model.response.ResValidateOTP
import com.digitaloutlet.service.ServiceHelper
import com.digitaloutlet.utils.Constants
import com.eros.moviesdb.utils.NetworkUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ValidateOTPRepository {

    private var mListener: OnVerifyOTPListener? = null

    interface OnVerifyOTPListener {
        fun onSuccessVerifyOTP(response: ResValidateOTP)
        fun onFailureVerifyOTP(errMsg: String)
    }

    fun setListener(listener: OnVerifyOTPListener) {
        mListener = listener
    }

    fun verifyOTP(msisdn: String, otp: String) {
        if (NetworkUtil.isNetworkConnected()) {
            val queryParams = HashMap<String, String>()
            queryParams.put(Constants.MSISDN, msisdn)
            queryParams.put(Constants.channel, Constants.channel_apk)
            queryParams.put(Constants.OTP, otp)

            val call = ServiceHelper.getClient().validateOTP(queryParams)
            call.enqueue(object : Callback<ResValidateOTP> {
                override fun onResponse(
                    call: Call<ResValidateOTP>,
                    response: Response<ResValidateOTP>
                ) {
                    if (response.isSuccessful) {
                        if (response.body() != null) {
                            mListener?.onSuccessVerifyOTP(response.body() as ResValidateOTP)
                        } else if (response.errorBody() != null) {
                            mListener?.onFailureVerifyOTP(
                                response.errorBody()?.string() ?: Constants.EMPTY
                            )
                        } else {
                            mListener?.onFailureVerifyOTP(DOApplication._INSTANCE.getString(R.string.error_internal_server_error))
                        }
                    } else {
                        mListener?.onFailureVerifyOTP(DOApplication._INSTANCE.getString(R.string.error_internal_server_error))
                    }
                }

                override fun onFailure(call: Call<ResValidateOTP>, t: Throwable) {
                    mListener?.onFailureVerifyOTP(DOApplication._INSTANCE.getString(R.string.error_oops_something_not_right))
                }
            })
        } else {
            mListener?.onFailureVerifyOTP(DOApplication._INSTANCE.getString(R.string.error_no_internet_connection))
        }
    }
}