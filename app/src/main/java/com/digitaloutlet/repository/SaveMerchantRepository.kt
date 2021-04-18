package com.digitaloutlet.repository

import com.digitaloutlet.R
import com.digitaloutlet.application.DOApplication
import com.digitaloutlet.model.bean.SaveMerchantBean
import com.digitaloutlet.model.response.ResSaveMerchant
import com.digitaloutlet.service.ServiceHelper
import com.digitaloutlet.utils.Constants
import com.eros.moviesdb.utils.NetworkUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SaveMerchantRepository {

    private var mListener: OnSaveMerchantListener? = null

    interface OnSaveMerchantListener {
        fun onSuccessSaveMerchant(response: ResSaveMerchant)
        fun onFailureSaveMerchant(errMsg: String)
    }

    fun setListener(listener: OnSaveMerchantListener) {
        mListener = listener
    }

    fun saveMerchant(saveMerchantBean: SaveMerchantBean) {
        if (NetworkUtil.isNetworkConnected()) {
            val queryParams = HashMap<String, String>()
            queryParams.put(Constants.MSISDN, saveMerchantBean.msisdn ?: Constants.EMPTY)
            queryParams.put(Constants.channel, Constants.channel_apk)
            //queryParams.put(Constants.locationTag, saveMerchantBean.locationTag ?: Constants.EMPTY)
            queryParams.put(Constants.nameOfShop, saveMerchantBean.nameOfShop ?: Constants.EMPTY)
            queryParams.put(Constants.TOS, Constants.EMPTY + saveMerchantBean.tos)
            queryParams.put(Constants.nameOfOwner, saveMerchantBean.nameOfOwner ?: Constants.EMPTY)
            queryParams.put(Constants.shopNo, saveMerchantBean.shopNo ?: Constants.EMPTY)
            queryParams.put(Constants.shopAddress, saveMerchantBean.shopAddress ?: Constants.EMPTY)
            queryParams.put(Constants.pin_code, saveMerchantBean.pinCode ?: Constants.EMPTY)
            queryParams.put(Constants.city, saveMerchantBean.city ?: Constants.EMPTY)
            queryParams.put(Constants.state, saveMerchantBean.state ?: Constants.EMPTY)
            queryParams.put(Constants.locationTag, saveMerchantBean.latitude + Constants.DASH + saveMerchantBean.longitude ?: Constants.EMPTY)
            //queryParams.put(Constants.longitude, saveMerchantBean.longitude ?: Constants.EMPTY)

            val call = ServiceHelper.getClient().saveMerchant(queryParams)
            call.enqueue(object : Callback<ResSaveMerchant> {
                override fun onResponse(
                    call: Call<ResSaveMerchant>,
                    response: Response<ResSaveMerchant>
                ) {
                    if (response.isSuccessful) {
                        if (response.body() != null) {
                            mListener?.onSuccessSaveMerchant(response.body() as ResSaveMerchant)
                        } else if (response.errorBody() != null) {
                            mListener?.onFailureSaveMerchant(
                                response.errorBody()?.string() ?: Constants.EMPTY
                            )
                        } else {
                            mListener?.onFailureSaveMerchant(DOApplication._INSTANCE.getString(R.string.error_internal_server_error))
                        }
                    } else {
                        mListener?.onFailureSaveMerchant(DOApplication._INSTANCE.getString(R.string.error_internal_server_error))
                    }
                }

                override fun onFailure(call: Call<ResSaveMerchant>, t: Throwable) {
                    mListener?.onFailureSaveMerchant(DOApplication._INSTANCE.getString(R.string.error_oops_something_not_right))
                }
            })
        } else {
            mListener?.onFailureSaveMerchant(DOApplication._INSTANCE.getString(R.string.error_no_internet_connection))
        }
    }
}