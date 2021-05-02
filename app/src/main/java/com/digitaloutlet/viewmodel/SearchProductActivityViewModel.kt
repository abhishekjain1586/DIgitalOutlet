package com.digitaloutlet.viewmodel

import androidx.lifecycle.ViewModel
import com.digitaloutlet.db.entities.ProductsEntity
import com.digitaloutlet.model.response.ResProducts
import com.digitaloutlet.repository.SearchProductRepository

class SearchProductActivityViewModel : ViewModel(),
    SearchProductRepository.OnSearchProductListener {

    private var loader: SingleLiveEvent<Boolean>? = null
    private var errorDialog: SingleLiveEvent<String>? = null
    private var mLvdProducts: SingleLiveEvent<ArrayList<ProductsEntity>>? = null

    private val searchProductRepository = SearchProductRepository()

    private var mProductsLst = ArrayList<ProductsEntity>()

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

    fun obsvGetSearchProducts(): SingleLiveEvent<ArrayList<ProductsEntity>> {
        if (mLvdProducts == null) {
            mLvdProducts = SingleLiveEvent()
        }
        return mLvdProducts!!
    }

    fun searchProduct(strSearch: String) {
        searchProductRepository.searchProductQuery(strSearch)
    }

    override fun onSuccessSearchProduct(response: ResProducts) {
        loader?.value = false
        mLvdProducts?.value = response.records
    }

    override fun onFailureSearchProduct(errMsg: String) {
        loader?.value = false
        errorDialog?.value = errMsg
    }
}