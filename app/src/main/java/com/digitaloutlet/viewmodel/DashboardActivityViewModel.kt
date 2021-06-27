package com.digitaloutlet.viewmodel

import androidx.lifecycle.ViewModel
import com.digitaloutlet.db.entities.ProductsEntity
import com.digitaloutlet.model.bean.PublishedProductsBean
import com.digitaloutlet.model.response.ParentCategory
import com.digitaloutlet.model.response.ResPublishProduct
import com.digitaloutlet.repository.DashboardRepository
import com.digitaloutlet.repository.ProductsOnMsisdnRepository
import com.digitaloutlet.repository.ProductsRepository
import com.digitaloutlet.repository.PublishProductRepository

class DashboardActivityViewModel : ViewModel(),
    PublishProductRepository.OnPublishProductsListener,
    ProductsOnMsisdnRepository.OnFetchProductsByMsisdnListener {

    private var loader: SingleLiveEvent<Boolean>? = null
    private var errorDialog: SingleLiveEvent<String>? = null

    private var mLiveDataPublishedCategories: SingleLiveEvent<ArrayList<PublishedProductsBean>>? = null
    private var mLiveDataUnpublished: SingleLiveEvent<ArrayList<ProductsEntity>>? = null
    private var mDeleteProductObserver: SingleLiveEvent<Boolean>? = null
    private var mEditProductObserver: SingleLiveEvent<ArrayList<ParentCategory>>? = null
    private var mLiveDataDraftCategories: SingleLiveEvent<ArrayList<ParentCategory>>? = null
    private var mProductsOnMsisdnObserver: SingleLiveEvent<Boolean>? = null

    private val mDashboardRepository = DashboardRepository()
    private var mPublishProductsRepository = PublishProductRepository()
    private var mProductsOnMsisdnRepository = ProductsOnMsisdnRepository()

    private var mPublishedCategoriesLst = ArrayList<PublishedProductsBean>()

    // Live Data Initialization
    fun showLoader() : SingleLiveEvent<Boolean> {
        if (loader == null) {
            loader = SingleLiveEvent()
        }
        return loader!!
    }

    fun showErrorDialog() : SingleLiveEvent<String> {
        if (errorDialog == null) {
            errorDialog = SingleLiveEvent()
        }
        return errorDialog!!
    }

    fun getProductsOnMsisdnObserver(): SingleLiveEvent<Boolean> {
        if (mProductsOnMsisdnObserver == null) {
            mProductsOnMsisdnObserver = SingleLiveEvent()
        }
        return mProductsOnMsisdnObserver!!
    }

    fun fetchProductsOnMsisdn(msisdn: String) {
        mProductsOnMsisdnRepository.setListener(this)
        mProductsOnMsisdnRepository.getProductDetailsOnMSISDN(msisdn)
    }

    fun getPublishedCategoriesObserver(): SingleLiveEvent<java.util.ArrayList<PublishedProductsBean>> {
        if (mLiveDataPublishedCategories == null) {
            mLiveDataPublishedCategories = SingleLiveEvent()
        }

        if (!mDashboardRepository.getPublishedProductsBean().isNullOrEmpty()) {
            mPublishedCategoriesLst = mDashboardRepository.getPublishedProductsBean()
            mLiveDataPublishedCategories?.value = mPublishedCategoriesLst
        }

        return mLiveDataPublishedCategories!!
    }

    fun hasPublishedProducts(): Boolean {
        return mPublishedCategoriesLst.size > 0
    }

    fun getUnpublishedProducts(): SingleLiveEvent<java.util.ArrayList<ProductsEntity>> {
        if (mLiveDataUnpublished == null) {
            mLiveDataUnpublished = SingleLiveEvent()
        }

        mLiveDataUnpublished?.value = mDashboardRepository.getProductsToBePublished()

        return mLiveDataUnpublished!!
    }

    fun getDraftCategories(): SingleLiveEvent<ArrayList<ParentCategory>> {
        if (mLiveDataDraftCategories == null) {
            mLiveDataDraftCategories = SingleLiveEvent()
        }
        return mLiveDataDraftCategories!!
    }

    fun fetchDraftCategories() {
        mLiveDataDraftCategories?.value = mDashboardRepository.getDraftProducts()
    }

    fun deleteProductObserver(): SingleLiveEvent<Boolean> {
        if (mDeleteProductObserver == null)  {
            mDeleteProductObserver = SingleLiveEvent()
        }

        return mDeleteProductObserver!!
    }

    fun editProductObserver(): SingleLiveEvent<ArrayList<ParentCategory>> {
        if (mEditProductObserver == null)  {
            mEditProductObserver = SingleLiveEvent()
        }

        return mEditProductObserver!!
    }
    // End of Live Data Initialization

    fun deleteProduct(childObj: PublishedProductsBean) {
        loader?.value = true
        mPublishProductsRepository.setListener(this)
        mPublishProductsRepository.deleteProduct(childObj)
    }

    override fun onSuccessPublishProducts(response: ResPublishProduct) {
        loader?.value = false
        mDeleteProductObserver?.value = true
    }

    override fun onSuccessFailurePublishProducts(errMsg: String) {
        loader?.value = false
        errorDialog?.value = errMsg
    }

    override fun onFailurePublishProducts(errMsg: String) {
        loader?.value = false
        errorDialog?.value = errMsg
    }

    fun editProduct(productBeanObj: PublishedProductsBean) {
        val lst = ArrayList<ParentCategory>()
        val obj = ParentCategory()
        obj.id = productBeanObj.parentCatId?.toInt()
        obj.parent_cat = productBeanObj.parentCatName
        lst.add(obj)
        mEditProductObserver?.value = lst
    }

    override fun onSuccessFetchProductsByMsisdn(productsLst: ArrayList<ProductsEntity>) {
        mProductsOnMsisdnObserver?.value = true
        getPublishedCategoriesObserver()
    }

    override fun onSuccessFailureFetchProductsByMsisdn(errMsg: String) {
        mProductsOnMsisdnObserver?.value = true
    }

    override fun onFailureFetchProductsByMsisdn(errMsg: String) {
        mProductsOnMsisdnObserver?.value = false
    }
}