package com.digitaloutlet.viewmodel

import androidx.lifecycle.ViewModel
import com.digitaloutlet.R
import com.digitaloutlet.application.DOApplication
import com.digitaloutlet.model.response.ParentCategory
import com.digitaloutlet.model.response.ResParentCategory
import com.digitaloutlet.repository.ParentCategoryRepository

class CreateCatalogActivityViewModel : ViewModel(),
    ParentCategoryRepository.OnParentCategoryListener {

    private var loader = SingleLiveEvent<Boolean>()
    private var errorDialog = SingleLiveEvent<String>()
    private var mLiveDataParentCategory = SingleLiveEvent<ArrayList<ParentCategory>>()

    private var mParentCatRepository = ParentCategoryRepository()

    private var mCategoriesLst = ArrayList<ParentCategory>()

    fun showLoader() : SingleLiveEvent<Boolean> {
        return loader
    }

    fun showErrorDialog() : SingleLiveEvent<String> {
        return errorDialog
    }

    fun getParentCatLst(): ArrayList<ParentCategory> {
        return mCategoriesLst
    }

    fun fetchParentCategoriesObserver(): SingleLiveEvent<ArrayList<ParentCategory>> {
        if (mCategoriesLst.isNullOrEmpty()) {
            loader.value = true
            mParentCatRepository.setListener(this)
            mParentCatRepository.getParentCategories()
        } else {
            mLiveDataParentCategory.value = mCategoriesLst
        }
        return mLiveDataParentCategory
    }

    override fun onSuccessParentCategory(response: ResParentCategory) {
        loader.value = false
        if (response.status == 1) {
            if (!response.records.isNullOrEmpty()) {
                mCategoriesLst.clear()
                mCategoriesLst.addAll(response.records!!)
                mLiveDataParentCategory.value = mCategoriesLst
            } else {
                errorDialog.value = DOApplication._INSTANCE.resources.getString(R.string.error_no_record_found)
            }
        } else {
            errorDialog.value = response.message
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
        }
    }

    fun getSelectedCategories(): ArrayList<ParentCategory>? {
        if (mCategoriesLst.isNullOrEmpty()) {
            return null
        }

        var selectedCatLst: ArrayList<ParentCategory>? = null
        for (obj in mCategoriesLst) {
            if (obj.isSelected) {
                if (selectedCatLst == null) {
                    selectedCatLst = ArrayList()
                }
                selectedCatLst.add(obj)
            }
        }
        return selectedCatLst
    }

    override fun onSuccessFailureParentCategory(errMsg: String) {
        loader.value = false
        errorDialog.value = errMsg
    }

    override fun onFailureParentCategory(errMsg: String) {
        loader.value = false
        errorDialog.value = errMsg
    }

}