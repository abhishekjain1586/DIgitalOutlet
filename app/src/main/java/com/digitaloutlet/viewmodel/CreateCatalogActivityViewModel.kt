package com.digitaloutlet.viewmodel

import androidx.lifecycle.ViewModel
import com.digitaloutlet.R
import com.digitaloutlet.application.DOApplication
import com.digitaloutlet.model.response.ParentCategory
import com.digitaloutlet.model.response.ResParentCategory
import com.digitaloutlet.repository.ParentCategoryRepository

class CreateCatalogActivityViewModel : ViewModel(),
    ParentCategoryRepository.OnParentCategoryListener {

    private var loader: SingleLiveEvent<Boolean>? = null
    private var mLiveDataParentCategory: SingleLiveEvent<ArrayList<ParentCategory>>? = null
    private var mLiveDataParentCategoryUpdateStatus: SingleLiveEvent<ParentCategory>? = null
    private var errorDialog: SingleLiveEvent<String>? = null
    private var mLiveDataSelectedCategories: SingleLiveEvent<ArrayList<ParentCategory>>? = null

    private var mParentCatRepository: ParentCategoryRepository? = null

    private var mCategoriesLst = ArrayList<ParentCategory>()

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

    fun getParentCatLst(): ArrayList<ParentCategory> {
        return mCategoriesLst
    }

    fun fetchParentCategoriesObserver(): SingleLiveEvent<ArrayList<ParentCategory>> {
        if (mLiveDataParentCategory == null) {
            mLiveDataParentCategory = SingleLiveEvent()
        }

        if (mParentCatRepository == null) {
            mParentCatRepository = ParentCategoryRepository()
            mParentCatRepository?.setListener(this)
        }

        loader?.value = true
        mParentCatRepository?.getParentCategories()

        return mLiveDataParentCategory!!
    }

    override fun onSuccessParentCategory(response: ResParentCategory) {
        loader?.value = false
        if (response.status == 1) {
            if (!response.records.isNullOrEmpty()) {
                mCategoriesLst.clear()
                mCategoriesLst.addAll(response.records!!)
                mLiveDataParentCategory?.value = mCategoriesLst
            } else {
                errorDialog?.value = DOApplication._INSTANCE.resources.getString(R.string.error_no_record_found)
            }
        } else {
            errorDialog?.value = response.message
        }
    }

    fun updateParentCategoryStatus(parentCategory: ParentCategory) {
        if (mCategoriesLst.isNotEmpty()) {
            for (obj in mCategoriesLst) {
                if (obj.id == parentCategory.id) {
                    obj.isSelected = !parentCategory.isSelected
                    break
                }
            }
            mLiveDataParentCategoryUpdateStatus?.value = parentCategory
        }
    }

    fun observerProceedNextToSelectProducts(): SingleLiveEvent<ArrayList<ParentCategory>> {
        if (mLiveDataSelectedCategories == null) {
            mLiveDataSelectedCategories = SingleLiveEvent()
        }
        return mLiveDataSelectedCategories!!
    }

    fun proceedNextToSelectProducts() {
        var selectedCatLst: ArrayList<ParentCategory>? = null

        if (mCategoriesLst.isNullOrEmpty()) {
            return
        }

        for (obj in mCategoriesLst) {
            if (obj.isSelected) {
                if (selectedCatLst == null) {
                    selectedCatLst = ArrayList()
                }
                selectedCatLst.add(obj)
            }
        }

        if (selectedCatLst.isNullOrEmpty()) {
            errorDialog?.value = DOApplication._INSTANCE.getString(R.string.select_product_categories)
            return
        }

        mLiveDataSelectedCategories?.value = selectedCatLst
    }

    override fun onSuccessFailureParentCategory(errMsg: String) {
        loader?.value = false
        errorDialog?.value = errMsg
    }

    override fun onFailureParentCategory(errMsg: String) {
        loader?.value = false
        errorDialog?.value = errMsg
    }

}