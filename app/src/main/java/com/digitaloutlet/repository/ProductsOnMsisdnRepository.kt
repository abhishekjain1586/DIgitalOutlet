package com.digitaloutlet.repository

import android.util.Log
import com.digitaloutlet.R
import com.digitaloutlet.application.DOApplication
import com.digitaloutlet.db.DBHelper
import com.digitaloutlet.db.entities.ProductsEntity
import com.digitaloutlet.model.response.ProductsOnMsisdn
import com.digitaloutlet.model.response.ResProductsOnMsisdn
import com.digitaloutlet.service.ServiceHelper
import com.digitaloutlet.utils.Constants
import com.eros.moviesdb.utils.NetworkUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProductsOnMsisdnRepository {

    private var mListener: OnFetchProductsByMsisdnListener? = null

    interface OnFetchProductsByMsisdnListener {
        fun onSuccessFetchProductsByMsisdn(productsLst: ArrayList<ProductsEntity>)
        fun onSuccessFailureFetchProductsByMsisdn(errMsg: String)
        fun onFailureFetchProductsByMsisdn(errMsg: String)
    }

    fun setListener(listener: OnFetchProductsByMsisdnListener) {
        mListener = listener
    }

    fun getProductDetailsOnMSISDN(msisdn: String) {
        if (NetworkUtil.isNetworkConnected()) {
            val queryParams = HashMap<String, String>()
            queryParams.put(Constants.MSISDN, msisdn)
            //queryParams.put(Constants.channel, Constants.hash_code)

            val call = ServiceHelper.getClient().getProductDetailsOnMSISDN(queryParams)
            call.enqueue(object : Callback<ResProductsOnMsisdn> {
                override fun onResponse(
                    call: Call<ResProductsOnMsisdn>,
                    response: Response<ResProductsOnMsisdn>
                ) {
                    if (response.isSuccessful) {
                        if (response.body() != null) {
                            (response.body() as ResProductsOnMsisdn).records?.let {
                                if (it.isNotEmpty()) {
                                    mListener?.onSuccessFetchProductsByMsisdn(saveProductsToDB(it))
                                } else {
                                    mListener?.onSuccessFailureFetchProductsByMsisdn(
                                        DOApplication._INSTANCE.getString(R.string.error_no_record_found))
                                }
                            }
                        } else if (response.errorBody() != null) {
                            mListener?.onSuccessFailureFetchProductsByMsisdn(
                                response.errorBody()?.string() ?: Constants.EMPTY
                            )
                        }/* else {
                        mListener?.onSuccessFailure(DOApplication._INSTANCE.getString(R.string.error_internal_server_error))
                    }*/
                    } else {
                        mListener?.onFailureFetchProductsByMsisdn("")
                    }
                }

                override fun onFailure(call: Call<ResProductsOnMsisdn>, t: Throwable) {
                    mListener?.onFailureFetchProductsByMsisdn(DOApplication._INSTANCE.getString(R.string.error_oops_something_not_right))
                }
            })
        } else {
            mListener?.onFailureFetchProductsByMsisdn(DOApplication._INSTANCE.getString(R.string.error_no_internet_connection))
        }
    }

    private fun saveProductsToDB(prodLst: ArrayList<ProductsOnMsisdn>): ArrayList<ProductsEntity> {
        val entityLst = ArrayList<ProductsEntity>()
        for (prodObj in prodLst) {
            val entityObj = ProductsEntity()
            entityObj.parentCatId = prodObj.parent_cat_id
            entityObj.parentCatName = prodObj.parent_cat_name
            entityObj.subCatName = prodObj.sub_cat_name
            entityObj.productId = prodObj.product_id?.toInt()
            entityObj.productName = prodObj.product_name
            entityObj.quantity = prodObj.quantity
            entityObj.unit = prodObj.unit
            entityObj.status = prodObj.product_state
            entityObj.merchantId = prodObj.merchant_id
            entityObj.price = prodObj.actual_price?.toDouble()
            entityObj.productStateForMerchant = ProductsEntity.PRODUCT_STATE_PUBLISHED
            entityLst.add(entityObj)
        }
        DBHelper.getInstance().getProductsDao().insertAll(entityLst)

        return entityLst
    }
}