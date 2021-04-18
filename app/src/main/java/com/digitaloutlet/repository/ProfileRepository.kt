package com.digitaloutlet.repository

import com.digitaloutlet.R
import com.digitaloutlet.application.DOApplication
import com.digitaloutlet.model.bean.ProfileBean
import com.digitaloutlet.model.response.ResGenerateOTP
import com.digitaloutlet.model.response.ResLogout
import com.digitaloutlet.service.ServiceHelper
import com.digitaloutlet.utils.Constants
import com.digitaloutlet.utils.DOPrefs
import com.eros.moviesdb.utils.NetworkUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileRepository {
    private var mListener: OnLogoutListener? = null

    interface OnLogoutListener {
        fun onSuccessLogout(response: ResLogout)
        fun onSuccessFailureLogout(errMsg: String)
        fun onFailureLogout(errMsg: String)
    }

    fun setListener(listener: OnLogoutListener) {
        mListener = listener
    }

    fun logout(): Boolean {
        return true
        /*if (NetworkUtil.isNetworkConnected()) {
            val queryParams = HashMap<String, String>()
            queryParams.put(Constants.channel, Constants.hash_code)

            val call = ServiceHelper.getClient().generateOTP(queryParams)
            call.enqueue(object : Callback<ResGenerateOTP> {
                override fun onResponse(
                    call: Call<ResGenerateOTP>,
                    response: Response<ResGenerateOTP>
                ) {
                    if (response.isSuccessful) {
                        if (response.body() != null) {
                            DOPrefs.clearPref()
                            mListener?.onSuccessLogout(response.body() as ResLogout)
                        } else if (response.errorBody() != null) {
                            mListener?.onSuccessFailureLogout(
                                response.errorBody()?.string() ?: Constants.EMPTY
                            )
                        }
                    } else {
                        mListener?.onFailureLogout(DOApplication._INSTANCE.getString(R.string.error_internal_server_error))
                    }
                }

                override fun onFailure(call: Call<ResGenerateOTP>, t: Throwable) {
                    mListener?.onFailureLogout(DOApplication._INSTANCE.getString(R.string.error_oops_something_not_right))
                }
            })
        } else {
            mListener?.onFailureLogout(DOApplication._INSTANCE.getString(R.string.error_no_internet_connection))
        }*/
    }

    fun getProfileMenu(): ArrayList<ProfileBean> {
        val lst = ArrayList<ProfileBean>()
        val editProfile = ProfileBean(DOApplication._INSTANCE.getString(R.string.edit_profile), ProfileBean.EDIT_PROFILE, R.drawable.ic_edit_profile)
        val raiseReq = ProfileBean(DOApplication._INSTANCE.getString(R.string.raise_request), ProfileBean.RAISE_REQUEST, R.drawable.ic_raise_request)
        val myReq = ProfileBean(DOApplication._INSTANCE.getString(R.string.my_request), ProfileBean.MY_REQUEST, R.drawable.ic_my_request)
        val logout = ProfileBean(DOApplication._INSTANCE.getString(R.string.logout), ProfileBean.LOGOUT, R.drawable.ic_logout)
        val productHistory = ProfileBean(DOApplication._INSTANCE.getString(R.string.product_history), ProfileBean.PRODUCT_HISTORY, R.drawable.ic_product_history)
        val deleteAcc = ProfileBean(DOApplication._INSTANCE.getString(R.string.delete_my_account), ProfileBean.DELETE_ACCOUNT, R.drawable.ic_delete_account)

        lst.add(editProfile)
        lst.add(raiseReq)
        lst.add(myReq)
        lst.add(logout)
        lst.add(productHistory)
        lst.add(deleteAcc)
        return lst
    }
}