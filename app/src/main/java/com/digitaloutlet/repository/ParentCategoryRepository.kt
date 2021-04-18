package com.digitaloutlet.repository

import com.digitaloutlet.R
import com.digitaloutlet.application.DOApplication
import com.digitaloutlet.model.response.ResParentCategory
import com.digitaloutlet.service.ServiceHelper
import com.digitaloutlet.utils.Constants
import com.digitaloutlet.utils.DOPrefs
import com.eros.moviesdb.utils.NetworkUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ParentCategoryRepository {

    private var mListener: OnParentCategoryListener? = null

    interface OnParentCategoryListener {
        fun onSuccessParentCategory(response: ResParentCategory)
        fun onSuccessFailureParentCategory(errMsg: String)
        fun onFailureParentCategory(errMsg: String)
    }

    fun setListener(listener: OnParentCategoryListener) {
        mListener = listener
    }

    fun getParentCategories() {
        if (NetworkUtil.isNetworkConnected()) {
            val queryParams = HashMap<String, String>()
            queryParams.put(Constants.TOS, Constants.EMPTY + DOPrefs.getTOS())
            //queryParams.put(Constants.channel, Constants.channel_apk)

            val call = ServiceHelper.getClient().getParentCategories(queryParams)
            call.enqueue(object : Callback<ResParentCategory> {
                override fun onResponse(
                    call: Call<ResParentCategory>,
                    response: Response<ResParentCategory>
                ) {
                    if (response.isSuccessful) {
                        if (response.body() != null) {
                            mListener?.onSuccessParentCategory(response.body() as ResParentCategory)
                        } else if (response.errorBody() != null) {
                            mListener?.onSuccessFailureParentCategory(
                                response.errorBody()?.string() ?: Constants.EMPTY
                            )
                        }/* else {
                        mListener?.onSuccessFailure(DOApplication._INSTANCE.getString(R.string.error_internal_server_error))
                    }*/
                    } else {
                        mListener?.onFailureParentCategory("")
                    }
                }

                override fun onFailure(call: Call<ResParentCategory>, t: Throwable) {
                    mListener?.onFailureParentCategory(DOApplication._INSTANCE.getString(R.string.error_oops_something_not_right))
                }
            })
        } else {
            mListener?.onFailureParentCategory(DOApplication._INSTANCE.getString(R.string.error_no_internet_connection))
        }
    }
}