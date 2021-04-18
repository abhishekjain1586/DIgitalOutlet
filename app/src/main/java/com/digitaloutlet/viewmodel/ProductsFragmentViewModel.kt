package com.digitaloutlet.viewmodel

import androidx.lifecycle.ViewModel
import com.digitaloutlet.R
import com.digitaloutlet.application.DOApplication
import com.digitaloutlet.db.entities.ProductsEntity
import com.digitaloutlet.model.response.ParentCategory
import com.digitaloutlet.model.response.ResProducts
import com.digitaloutlet.repository.ProductsRepository

class ProductsFragmentViewModel : ViewModel(), ProductsRepository.OnProductsListener {

    private var loader: SingleLiveEvent<Boolean>? = null
    private var errorDialog: SingleLiveEvent<String>? = null
    private var mLiveDataProducts: SingleLiveEvent<ArrayList<ProductsEntity>>? = null
    private var mLiveDataProductChangeState: SingleLiveEvent<HashMap<Int, ProductsEntity>>? = null
    private var mLiveDataProceedNextToCapturePrice: SingleLiveEvent<ArrayList<ProductsEntity>>? = null
    private var mLiveDataUserConsentToProceed: SingleLiveEvent<Boolean>? = null
    private var mLiveDataSaveAsDraft: SingleLiveEvent<Boolean>? = null

    private var mProductsRepository: ProductsRepository? = null

    private var mProductsLst = ArrayList<ProductsEntity>()
    var mCurrentParentCat: ParentCategory? = null


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

    fun getProductsObserver(subCatId: Int): SingleLiveEvent<java.util.ArrayList<ProductsEntity>> {
        if (mLiveDataProducts == null) {
            mLiveDataProducts = SingleLiveEvent()
        }

        if (mProductsRepository == null) {
            mProductsRepository = ProductsRepository()
            mProductsRepository?.setListener(this)
        }

        if (!mProductsLst.isNullOrEmpty()) {
            mLiveDataProducts?.value = mProductsLst
        } else {
            mCurrentParentCat?.id?.let {
                loader?.value = true
                mProductsRepository?.getProductDetails(it)
            }
        }

        return mLiveDataProducts!!
    }

    fun changeProductStateObserver(): SingleLiveEvent<HashMap<Int, ProductsEntity>> {
        if (mLiveDataProductChangeState == null) {
            mLiveDataProductChangeState = SingleLiveEvent()
        }
        return mLiveDataProductChangeState!!
    }

    fun proceedNextToCapturePriceObserver(): SingleLiveEvent<ArrayList<ProductsEntity>> {
        if (mLiveDataProceedNextToCapturePrice == null) {
            mLiveDataProceedNextToCapturePrice = SingleLiveEvent()
        }
        return mLiveDataProceedNextToCapturePrice!!
    }
    // End of Live Data Initialization


    // Live Data Initialization For Price Screen

    fun userConsentToProceedObserver(): SingleLiveEvent<Boolean> {
        if (mLiveDataUserConsentToProceed == null) {
            mLiveDataUserConsentToProceed = SingleLiveEvent()
        }
        return mLiveDataUserConsentToProceed!!
    }

    fun saveAsDraftObserver(): SingleLiveEvent<Boolean> {
        if (mLiveDataSaveAsDraft == null) {
            mLiveDataSaveAsDraft = SingleLiveEvent()
        }
        return mLiveDataSaveAsDraft!!
    }
    // End of Live Data Initialization


    fun setCurrentParentCategory(parentCat: ParentCategory) {
        mCurrentParentCat = parentCat
    }

    fun onProductChangeState(position: Int, productsEntity: ProductsEntity) {
        val obj = mProductsLst.get(position)
        obj.isSelected = !productsEntity.isSelected

        val tempMap = HashMap<Int, ProductsEntity>()
        tempMap.put(position, obj)
        mLiveDataProductChangeState?.value = tempMap
    }

    private fun getProductsToSave(): ArrayList<ProductsEntity> {
        val tempLst = ArrayList<ProductsEntity>()
        for (obj in mProductsLst) {
            if (obj.productId != null) {
                tempLst.add(obj)
            }
        }
        return tempLst
    }

    private fun getSubcategoryWiseLst(productsLst: ArrayList<ProductsEntity>): ArrayList<ProductsEntity> {
        val subcatWiseLst = ArrayList<ProductsEntity>()
        val tempLst = ArrayList<ProductsEntity>()
        val mapSubCat = HashMap<Int, ProductsEntity>()

        val selectedLst = mProductsRepository?.getProductsByParentCatId(mCurrentParentCat?.id!!)
        if (!selectedLst.isNullOrEmpty()) {
            val mapSavedLst = HashMap<Int, ProductsEntity>()
            for (i in 0 until selectedLst.size) {
                selectedLst.get(i).isSelected = true
                subcatWiseLst.add(selectedLst.get(i))
                mapSavedLst.put(selectedLst.get(i).productId!!, selectedLst.get(i))
            }

            for (i in 0 until productsLst.size) {
                if (mapSavedLst.get(productsLst.get(i).productId) == null) {
                    tempLst.add(productsLst.get(i))
                }
            }
            /*val iterator = mapRemovePos.entries.iterator()
            while (iterator.hasNext()) {
                val mapElement = iterator.next() as Map.Entry<Int, Int>
                productsLst.removeAt(mapElement.value)
            }*/
        }
        //tempLst.addAll(productsLst)
        tempLst.sortWith(Comparator { o1, o2 -> o1.subCatName!!.compareTo(o2.subCatName!!)})
        for (i in 0 until tempLst.size) {
            if (i == 0 || (tempLst.size > 1 && !tempLst.get(i).subCatName.equals(tempLst.get(i-1).subCatName))) {
                val obj = ProductsEntity()
                obj.subCatName = tempLst.get(i).subCatName
                var pos = 0
                if (i > 0) {
                    pos = i + 1
                }
                mapSubCat.put(pos, obj)
            }
        }
        val iterator = mapSubCat.entries.iterator()
        while (iterator.hasNext()) {
            val mapElement = iterator.next() as Map.Entry<Int, ProductsEntity>
            tempLst.add(mapElement.key, mapElement.value)
        }
        subcatWiseLst.addAll(tempLst)
        return subcatWiseLst
    }

    fun saveProductsInDB(isSaveAsDraft: Boolean) {
        val selectedProductLst = mProductsRepository?.saveProductsInDB(getProductsToSave(), mCurrentParentCat?.parent_cat)
        if (isSaveAsDraft) {
            mLiveDataSaveAsDraft?.value = true
        } else {
            if (!selectedProductLst.isNullOrEmpty()) {
                mLiveDataProceedNextToCapturePrice?.value = selectedProductLst
            } else {
                mLiveDataUserConsentToProceed?.value = true
            }
        }
    }

    override fun onSuccessProducts(response: ResProducts) {
        loader?.value = false
        if (response.status == 1) {
            if (!response.records.isNullOrEmpty()) {
                mProductsLst.clear()
                mProductsLst.addAll(getSubcategoryWiseLst(response.records!!))
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