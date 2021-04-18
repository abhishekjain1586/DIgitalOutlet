package com.digitaloutlet.repository

import com.digitaloutlet.R
import com.digitaloutlet.application.DOApplication
import com.digitaloutlet.db.DBHelper
import com.digitaloutlet.db.entities.ProductsEntity
import com.digitaloutlet.model.bean.PublishedProductsBean
import com.digitaloutlet.model.request.PublishProducts
import com.digitaloutlet.model.request.ReqPublishProducts
import com.digitaloutlet.model.response.ResPublishProduct
import com.digitaloutlet.service.ServiceHelper
import com.digitaloutlet.utils.Constants
import com.digitaloutlet.utils.DOPrefs
import com.eros.moviesdb.utils.NetworkUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PublishProductRepository {

    var mListener: OnPublishProductsListener? = null

    interface OnPublishProductsListener {
        fun onSuccessPublishProducts(response: ResPublishProduct)
        fun onSuccessFailurePublishProducts(errMsg: String)
        fun onFailurePublishProducts(errMsg: String)
    }

    fun setListener(listener: OnPublishProductsListener) {
        mListener = listener
    }

    fun publishProducts() {
        if (NetworkUtil.isNetworkConnected()) {
            val productsLst = DBHelper.getInstance().getProductsDao().getAllProductsToPublish(DOPrefs.getMerchantId()) as ArrayList<ProductsEntity>
            if (!productsLst.isNullOrEmpty()) {
                val call = ServiceHelper.getClient().publishProducts(getRequest(productsLst))
                call.enqueue(object : Callback<ResPublishProduct> {
                    override fun onResponse(call: Call<ResPublishProduct>, response: Response<ResPublishProduct>) {
                        if (response.isSuccessful) {
                            if (response.body() != null) {
                                updateProducts(productsLst)
                                mListener?.onSuccessPublishProducts(response.body() as ResPublishProduct)
                            } else if (response.errorBody() != null) {
                                mListener?.onSuccessFailurePublishProducts(response.errorBody()?.string() ?: Constants.EMPTY)
                            }/* else {
                        mListener?.onSuccessFailure(DOApplication._INSTANCE.getString(R.string.error_internal_server_error))
                    }*/
                        } else {
                            mListener?.onFailurePublishProducts("")
                        }
                    }

                    override fun onFailure(call: Call<ResPublishProduct>, t: Throwable) {
                        mListener?.onFailurePublishProducts(DOApplication._INSTANCE.getString(R.string.error_oops_something_not_right))
                    }
                })
            }
        } else {
            mListener?.onFailurePublishProducts(DOApplication._INSTANCE.getString(R.string.error_no_internet_connection))
        }
    }

    private fun getRequest(productsLst: ArrayList<ProductsEntity>): ReqPublishProducts {
        val reqPublishProducts = ReqPublishProducts()
        val reqLst = ArrayList<PublishProducts>()
        for (obj in productsLst) {
            val reqObj = PublishProducts()
            obj.action?.let {
                if (it.equals(ProductsEntity.ACTION_ADD)) {
                    reqObj.parent_cat_id = obj.parentCatId
                    reqObj.parent_cat_name = obj.parentCatName
                    reqObj.sub_cat_name = obj.subCatName
                    reqObj.product_name = obj.productName
                    reqObj.brand_name = obj.brandName
                    reqObj.quantity = obj.quantity
                    reqObj.unit = obj.unit
                    reqObj.image_link = obj.image
                    reqObj.actual_price = Constants.EMPTY + obj.price
                } else if (it.equals(ProductsEntity.ACTION_UPDATE)) {
                    reqObj.actual_price = Constants.EMPTY + obj.price
                }
                reqObj.merchant_id = obj.merchantId
                reqObj.product_id = Constants.EMPTY + obj.productId
                reqObj.action = it
            }
            reqLst.add(reqObj)
        }
        reqPublishProducts.details = reqLst
        return reqPublishProducts
    }

    private fun updateProducts(productsLst: ArrayList<ProductsEntity>) {
        val deleteProductIdLst = ArrayList<Int>()
        val addOrUpdateProductsLst = ArrayList<ProductsEntity>()
        for (obj in productsLst) {
            obj.action?.let {
                when(it) {
                    ProductsEntity.ACTION_ADD -> {
                        obj.productStateForMerchant = ProductsEntity.PRODUCT_STATE_PUBLISHED
                        obj.action = null
                        addOrUpdateProductsLst.add(obj)
                    }
                    ProductsEntity.ACTION_UPDATE -> {
                        obj.action = null
                        addOrUpdateProductsLst.add(obj)
                    }
                    ProductsEntity.ACTION_DELETE -> {
                        obj.productId?.let {
                            it1 -> deleteProductIdLst.add(it1)
                        }
                    }
                    else -> {}
                }
            }
        }

        if (addOrUpdateProductsLst.isNotEmpty()) {
            DBHelper.getInstance().getProductsDao().updateAll(addOrUpdateProductsLst)
        }
        if (deleteProductIdLst.isNotEmpty()) {
            DBHelper.getInstance().getProductsDao().deletePublishedProducts(DOPrefs.getMerchantId(), deleteProductIdLst)
        }
    }

    fun deleteProduct(childObj: PublishedProductsBean) {
        if (NetworkUtil.isNetworkConnected()) {
            val call = ServiceHelper.getClient().publishProducts(getDeleteProductRequest(childObj))
            call.enqueue(object : Callback<ResPublishProduct> {
                override fun onResponse(call: Call<ResPublishProduct>, response: Response<ResPublishProduct>) {
                    if (response.isSuccessful) {
                        if (response.body() != null) {
                            deleteProductFromDB(childObj)
                            mListener?.onSuccessPublishProducts(response.body() as ResPublishProduct)
                        } else if (response.errorBody() != null) {
                            mListener?.onSuccessFailurePublishProducts(response.errorBody()?.string() ?: Constants.EMPTY)
                        }/* else {
                    mListener?.onSuccessFailure(DOApplication._INSTANCE.getString(R.string.error_internal_server_error))
                }*/
                    } else {
                        mListener?.onFailurePublishProducts("")
                    }
                }

                override fun onFailure(call: Call<ResPublishProduct>, t: Throwable) {
                    mListener?.onFailurePublishProducts(DOApplication._INSTANCE.getString(R.string.error_oops_something_not_right))
                }
            })
        } else {
            mListener?.onFailurePublishProducts(DOApplication._INSTANCE.getString(R.string.error_no_internet_connection))
        }
    }

    private fun deleteProductFromDB(childObj: PublishedProductsBean) {
        DBHelper.getInstance().getProductsDao().deletePublishedProductByID(DOPrefs.getMerchantId(), childObj.productId!!)
    }

    private fun getDeleteProductRequest(childObjLst: ArrayList<PublishedProductsBean>): ReqPublishProducts {
        val reqPublishProducts = ReqPublishProducts()
        val reqLst = ArrayList<PublishProducts>()

        for (obj in childObjLst) {
            val reqObj = PublishProducts()
            reqObj.merchant_id = obj.merchantId
            reqObj.product_id = Constants.EMPTY + obj.productId
            reqObj.action = ProductsEntity.ACTION_DELETE
            reqLst.add(reqObj)
        }

        reqPublishProducts.details = reqLst
        return reqPublishProducts
    }

    private fun getDeleteProductRequest(childObj: PublishedProductsBean): ReqPublishProducts {
        val childObjLst = ArrayList<PublishedProductsBean>()
        childObjLst.add(childObj)
        return getDeleteProductRequest(childObjLst)
    }
}