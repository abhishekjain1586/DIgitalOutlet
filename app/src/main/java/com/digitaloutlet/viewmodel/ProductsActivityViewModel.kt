package com.digitaloutlet.viewmodel

import androidx.lifecycle.ViewModel
import com.digitaloutlet.R
import com.digitaloutlet.application.DOApplication
import com.digitaloutlet.db.entities.ProductsEntity
import com.digitaloutlet.model.response.ParentCategory
import com.digitaloutlet.model.response.ResProducts
import com.digitaloutlet.repository.ProductsRepository
import com.digitaloutlet.utils.Constants

class ProductsActivityViewModel : ViewModel(), ProductsRepository.OnProductsListener {

    private var loader: SingleLiveEvent<Boolean>? = null
    private var errorDialog: SingleLiveEvent<String>? = null
    private var mLiveDataParentCategoryName: SingleLiveEvent<String>? = null
    private var mLiveDataProducts: SingleLiveEvent<ArrayList<ProductsEntity>>? = null

    private var mProductsLst = ArrayList<ProductsEntity>()
    private var mSelectedParentCatLst = ArrayList<ParentCategory>()
    private var currentParentCatPosition = 0


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

    fun setSelectedParentCatLst(selectedParentCatLst: ArrayList<ParentCategory>) {
        mSelectedParentCatLst.clear()
        mSelectedParentCatLst.addAll(selectedParentCatLst)

        mLiveDataParentCategoryName?.value = getCurrentParentCat()?.parent_cat ?: Constants.EMPTY
    }

    fun getCurrentParentCat(): ParentCategory? {
        if (mSelectedParentCatLst.isNotEmpty()) {
            return mSelectedParentCatLst.get(currentParentCatPosition)
        }
        return null
    }

    override fun onSuccessProducts(response: ResProducts) {
        loader?.value = false
        if (response.status == 1) {
            if (!response.records.isNullOrEmpty()) {
                mProductsLst.clear()
                mProductsLst.addAll(response.records!!)

                mLiveDataProducts?.value = mProductsLst
            } else {
                errorDialog?.value = DOApplication._INSTANCE.resources.getString(R.string.error_no_record_found)
            }
        } else {
            errorDialog?.value = response.message
        }
    }

    override fun onSuccessFailureProducts(errMsg: String) {
        loader?.value = false
        errorDialog?.value = errMsg
    }

    override fun onFailureProducts(errMsg: String) {
        loader?.value = false
        errorDialog?.value = errMsg
    }
}