package com.digitaloutlet.repository

import com.digitaloutlet.R
import com.digitaloutlet.application.DOApplication
import com.digitaloutlet.db.DBHelper
import com.digitaloutlet.db.entities.ProductsEntity
import com.digitaloutlet.model.bean.PublishedProductsBean
import com.digitaloutlet.model.request.ReqProductsByParentID
import com.digitaloutlet.model.response.ResProducts
import com.digitaloutlet.service.ServiceHelper
import com.digitaloutlet.utils.Constants
import com.digitaloutlet.utils.DOPrefs
import com.eros.moviesdb.utils.NetworkUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProductsRepository {

    private var mListener: OnProductsListener? = null

    interface OnProductsListener {
        fun onSuccessProducts(response: ResProducts)
        fun onSuccessFailureProducts(errMsg: String)
        fun onFailureProducts(errMsg: String)
    }

    fun setListener(listener: OnProductsListener) {
        mListener = listener
    }

    fun getProductDetails(parentId: Int/*, subCatId: Int*/) {
        if (NetworkUtil.isNetworkConnected()) {
            /*val queryParams = HashMap<String, String>()
            queryParams.put(Constants.TOS, Constants.EMPTY + DOPrefs.getTOS())
            queryParams.put(Constants.parentId, Constants.EMPTY + parentId)
            queryParams.put(Constants.sub_cat, Constants.EMPTY + "Powdered Spices"*//*subCatId*//*)*/
            //queryParams.put(Constants.channel, Constants.channel_apk)

            val reqObj = ReqProductsByParentID()
            reqObj.parent_id = Constants.EMPTY + parentId
            val call = ServiceHelper.getClient().getProductDetails(reqObj)
            call.enqueue(object : Callback<ResProducts> {
                override fun onResponse(
                    call: Call<ResProducts>,
                    response: Response<ResProducts>
                ) {
                    if (response.isSuccessful) {
                        if (response.body() != null) {
                            response.body()?.records?.let { it ->
                                if (it.isNotEmpty()) {
                                    updateProductsWithDB(it, parentId)
                                }
                            }
                            mListener?.onSuccessProducts(response.body() as ResProducts)
                        } else if (response.errorBody() != null) {
                            mListener?.onSuccessFailureProducts(
                                response.errorBody()?.string() ?: Constants.EMPTY
                            )
                        }/* else {
                        mListener?.onSuccessFailure(DOApplication._INSTANCE.getString(R.string.error_internal_server_error))
                    }*/
                    } else {
                        mListener?.onFailureProducts("")
                    }
                }

                override fun onFailure(call: Call<ResProducts>, t: Throwable) {
                    mListener?.onFailureProducts(DOApplication._INSTANCE.getString(R.string.error_oops_something_not_right))
                }
            })
        } else {
            mListener?.onFailureProducts(DOApplication._INSTANCE.getString(R.string.error_no_internet_connection))
        }
    }

    private fun updateProductsWithDB(productsLst: ArrayList<ProductsEntity>, parentCatId: Int) {
        var dbProductsLst = getProductsByParentCatId(parentCatId)
        if (!dbProductsLst.isNullOrEmpty() && !productsLst.isNullOrEmpty()) {
            for (mainObj in productsLst) {
                for (dbObj in dbProductsLst) {
                    if (mainObj.productId == dbObj.productId) {
                        mainObj.merchantId = dbObj.merchantId
                        mainObj.parentCatName = dbObj.parentCatName
                        mainObj.subCatName = dbObj.subCatName
                        mainObj.price = dbObj.price
                        mainObj.productStateForMerchant = dbObj.productStateForMerchant
                        mainObj.action = dbObj.action
                        mainObj.isSelected = true
                        break
                    }
                }
            }
        }
    }

    fun getProductsByParentCatId(parentCatId: Int): ArrayList<ProductsEntity> {
        return DBHelper.getInstance().getProductsDao().getProductsByParentCategoryId(
            DOPrefs.getMerchantId(),
            parentCatId
        ) as ArrayList<ProductsEntity>
    }

    fun getActiveProductsPricesByParentCategoryId(parentCatId: String): ArrayList<ProductsEntity>? {
        return DBHelper.getInstance().getProductsDao().getActiveProductsPricesByParentCategoryId(DOPrefs.getMerchantId(), parentCatId) as ArrayList<ProductsEntity>
    }

    fun getAllProductsToPublish(): ArrayList<ProductsEntity>? {
        return DBHelper.getInstance().getProductsDao().getAllProductsToPublish(DOPrefs.getMerchantId()) as ArrayList<ProductsEntity>
    }

    fun getMerchantCategoriesLstToPublish(): ArrayList<ProductsEntity>? {
        return DBHelper.getInstance().getProductsDao().getMerchantCategoriesToPublish(DOPrefs.getMerchantId()) as ArrayList<ProductsEntity>
    }

    fun getProductByID(productID: Int): ProductsEntity {
        return DBHelper.getInstance().getProductsDao().getProductByID(DOPrefs.getMerchantId(), productID) as ProductsEntity
    }

    fun saveProductsInDB(productsLst: ArrayList<ProductsEntity>, parentCatName: String?): ArrayList<ProductsEntity> {
        val selectedProductLst = ArrayList<ProductsEntity>()

        if (productsLst.isNotEmpty()) {
            for (obj in productsLst) {
                if (obj.isSelected) {
                    if (obj.merchantId.isNullOrEmpty()) {
                        obj.merchantId = DOPrefs.getMerchantId()
                        obj.parentCatName = parentCatName
                        obj.typeOfShop = DOPrefs.getTOS().toString()
                        obj.productStateForMerchant = ProductsEntity.PRODUCT_STATE_DRAFT
                        obj.action = ProductsEntity.ACTION_ADD

                        DBHelper.getInstance().getProductsDao().insertOrUpdate(obj)
                    }
                    selectedProductLst.add(obj)
                } else {
                    if (!obj.merchantId.isNullOrEmpty()) {
                        if (obj.productStateForMerchant!!.equals(ProductsEntity.PRODUCT_STATE_DRAFT)) {
                            DBHelper.getInstance().getProductsDao().delete(obj)
                        } else if (obj.productStateForMerchant!!.equals(ProductsEntity.PRODUCT_STATE_PUBLISHED)) {
                            obj.action = ProductsEntity.ACTION_DELETE
                            DBHelper.getInstance().getProductsDao().insertOrUpdate(obj)
                        }
                    }
                }
            }
        }

        return selectedProductLst
    }

    fun savePriceInDB(productsLst: ArrayList<ProductsEntity>) {
        if (productsLst.isNotEmpty()) {
            for (i in 0 until productsLst.size) {
                val obj = DBHelper.getInstance().getProductsDao().getProductByID(DOPrefs.getMerchantId(), productsLst.get(i).productId!!)

                if (productsLst.get(i).price != obj.price
                    && productsLst.get(i).productStateForMerchant.equals(ProductsEntity.PRODUCT_STATE_PUBLISHED)) {
                    productsLst.get(i).action = ProductsEntity.ACTION_UPDATE
                }
                DBHelper.getInstance().getProductsDao().insertOrUpdate(productsLst.get(i))
            }
        }
    }
}