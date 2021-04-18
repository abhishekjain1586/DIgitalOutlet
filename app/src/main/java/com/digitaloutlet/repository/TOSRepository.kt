package com.digitaloutlet.repository

import com.digitaloutlet.R
import com.digitaloutlet.application.DOApplication
import com.digitaloutlet.model.response.ResTOS
import com.digitaloutlet.service.ServiceHelper
import com.digitaloutlet.utils.Constants
import com.eros.moviesdb.utils.NetworkUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TOSRepository {

    private var mListener: OnTOSListener? = null

    interface OnTOSListener {
        fun onSuccessTOS(response: ResTOS)
        fun onFailureTOS(errMsg: String)
    }

    fun setListener(listener: OnTOSListener) {
        mListener = listener
    }

    fun getTOS() {
        if (NetworkUtil.isNetworkConnected()) {
            val queryParams = HashMap<String, String>()
            queryParams.put(Constants.type, Constants.TOS)

            val call = ServiceHelper.getClient().getTOS(queryParams)
            call.enqueue(object : Callback<ResTOS> {
                override fun onResponse(
                    call: Call<ResTOS>,
                    response: Response<ResTOS>
                ) {
                    if (response.isSuccessful) {
                        if (response.body() != null) {
                            mListener?.onSuccessTOS(response.body() as ResTOS)
                        } else if (response.errorBody() != null) {
                            mListener?.onFailureTOS(
                                response.errorBody()?.string() ?: Constants.EMPTY
                            )
                        } else {
                            mListener?.onFailureTOS(DOApplication._INSTANCE.getString(R.string.error_internal_server_error))
                        }
                    } else {
                        mListener?.onFailureTOS(DOApplication._INSTANCE.getString(R.string.error_internal_server_error))
                    }
                }

                override fun onFailure(call: Call<ResTOS>, t: Throwable) {
                    mListener?.onFailureTOS(DOApplication._INSTANCE.getString(R.string.error_oops_something_not_right))
                }
            })
        } else {
            mListener?.onFailureTOS(DOApplication._INSTANCE.getString(R.string.error_no_internet_connection))
        }
    }

}