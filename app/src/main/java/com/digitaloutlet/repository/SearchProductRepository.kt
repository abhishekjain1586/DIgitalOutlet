package com.digitaloutlet.repository

import com.digitaloutlet.R
import com.digitaloutlet.application.DOApplication
import com.digitaloutlet.model.response.ResProducts
import com.digitaloutlet.service.ServiceHelper
import com.digitaloutlet.utils.Constants
import com.eros.moviesdb.utils.NetworkUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchProductRepository {

    private var mListener: OnSearchProductListener? = null

    interface OnSearchProductListener {
        fun onSuccessSearchProduct(response: ResProducts)
        fun onFailureSearchProduct(errMsg: String)
    }

    fun setListener(listener: OnSearchProductListener) {
        mListener = listener
    }

    fun searchProductQuery(searchQuery: String) {
        if (NetworkUtil.isNetworkConnected()) {
            val queryParams = HashMap<String, String>()
            queryParams.put(Constants.query, searchQuery)

            val call = ServiceHelper.getClient().searchProductQuery(queryParams)
            call.enqueue(object : Callback<ResProducts> {
                override fun onResponse(
                    call: Call<ResProducts>,
                    response: Response<ResProducts>
                ) {
                    if (response.isSuccessful) {
                        if (response.body() != null) {
                            mListener?.onSuccessSearchProduct(response.body() as ResProducts)
                        } else if (response.errorBody() != null) {
                            mListener?.onFailureSearchProduct(response.errorBody()?.string() ?: Constants.EMPTY)
                        } else {
                            mListener?.onFailureSearchProduct(DOApplication._INSTANCE.getString(R.string.error_internal_server_error))
                        }
                    } else {
                        mListener?.onFailureSearchProduct(DOApplication._INSTANCE.getString(R.string.error_internal_server_error))
                    }
                }

                override fun onFailure(call: Call<ResProducts>, t: Throwable) {
                    mListener?.onFailureSearchProduct(DOApplication._INSTANCE.getString(R.string.error_oops_something_not_right))
                }
            })
        } else {
            mListener?.onFailureSearchProduct(DOApplication._INSTANCE.getString(R.string.error_no_internet_connection))
        }
    }
}