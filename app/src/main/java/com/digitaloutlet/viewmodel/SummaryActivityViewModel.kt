package com.digitaloutlet.viewmodel

import androidx.lifecycle.ViewModel
import com.digitaloutlet.R
import com.digitaloutlet.application.DOApplication
import com.digitaloutlet.db.entities.ProductsEntity
import com.digitaloutlet.model.response.ResPublishProduct
import com.digitaloutlet.repository.ProductsRepository
import com.digitaloutlet.repository.PublishProductRepository

class SummaryActivityViewModel : ViewModel(), PublishProductRepository.OnPublishProductsListener {

    private var loader = SingleLiveEvent<Boolean>()
    private var errorDialog = SingleLiveEvent<String>()
    private var mLiveDataProducts: SingleLiveEvent<ArrayList<ProductsEntity>>? = null
    private var mLiveDataPublishProducts: SingleLiveEvent<String>? = null

    private var mProductsRepository = ProductsRepository()
    private var mPublishProductsRepository: PublishProductRepository? = null

    private var mProductsLst = ArrayList<ProductsEntity>()

    // Live Data Initialization
    fun showLoader() : SingleLiveEvent<Boolean> {
        return loader
    }

    fun showErrorDialog() : SingleLiveEvent<String> {
        return errorDialog
    }

    fun getProductsObserver(): SingleLiveEvent<java.util.ArrayList<ProductsEntity>> {
        if (mLiveDataProducts == null) {
            mLiveDataProducts = SingleLiveEvent()
        }
        return mLiveDataProducts!!

    }

    fun publishProductsObserver(): SingleLiveEvent<String> {
        if (mLiveDataPublishProducts == null) {
            mLiveDataPublishProducts = SingleLiveEvent()
        }
        return mLiveDataPublishProducts!!

    }

    fun getAllProductsToPublish() {
        val tempLst = mProductsRepository.getAllProductsToPublish()
        val parentCatLst = mProductsRepository.getMerchantCategoriesLstToPublish()

        if (!tempLst.isNullOrEmpty()) {
            for (parentCatObj in parentCatLst!!) {
                for (i: Int in 0..tempLst.size - 1) {
                    if (tempLst[i].parentCatId.equals(parentCatObj.parentCatId)) {
                        val tempObj = ProductsEntity()
                        tempObj.parentCatName = parentCatObj.parentCatName
                        tempLst.add(i, tempObj)
                        break
                    }
                }
            }
            mProductsLst.clear()
            mProductsLst.addAll(tempLst)
            mLiveDataProducts?.value = mProductsLst
        } else {
            errorDialog.value = DOApplication._INSTANCE.getString(R.string.error_no_products_to_publish)
        }
    }

    fun publishProducts() {
        if (mPublishProductsRepository == null) {
            mPublishProductsRepository = PublishProductRepository()
            mPublishProductsRepository?.setListener(this)
        }
        loader.value = true
        mPublishProductsRepository?.publishProducts()
    }

    override fun onSuccessPublishProducts(response: ResPublishProduct) {
        loader.value = false
        if (response.status == 1) {
            mLiveDataPublishProducts?.value = response.message
        } else {
            errorDialog.value = response.message?.let { it } ?: DOApplication._INSTANCE.resources.getString(R.string.error_internal_server_error)
        }
    }

    override fun onSuccessFailurePublishProducts(errMsg: String) {
        loader.value = false
        errorDialog.value = errMsg
    }

    override fun onFailurePublishProducts(errMsg: String) {
        loader.value = false
        errorDialog.value = errMsg
    }

    /*fun proceedNextToCapturePriceObserver(): SingleLiveEvent<ArrayList<ProductsEntity>> {
        if (mLiveDataProceedNextToCapturePrice == null) {
            mLiveDataProceedNextToCapturePrice = SingleLiveEvent()
        }
        return mLiveDataProceedNextToCapturePrice!!
    }*/
    // End of Live Data Initialization


}