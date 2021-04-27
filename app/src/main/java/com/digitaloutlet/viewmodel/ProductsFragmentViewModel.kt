package com.digitaloutlet.viewmodel

import androidx.lifecycle.ViewModel
import com.digitaloutlet.db.entities.ProductsEntity
import com.digitaloutlet.model.response.ParentCategory
import com.digitaloutlet.model.response.ResProducts
import com.digitaloutlet.repository.ProductsRepository

class ProductsFragmentViewModel : ViewModel(), ProductsRepository.OnProductsListener {

    private var loader: SingleLiveEvent<Boolean>? = null
    private var errorDialog: SingleLiveEvent<String>? = null
    private var mLvdProducts: SingleLiveEvent<ArrayList<ProductsEntity>>? = null
    private var mLvdProductChangeState: SingleLiveEvent<HashMap<Int, ProductsEntity>>? = null
    private var mLiveDataProceedNextToCapturePrice: SingleLiveEvent<ArrayList<ProductsEntity>>? = null
    private var mLiveDataUserConsentToProceed: SingleLiveEvent<Boolean>? = null
    private var mLvdSaveAsDraft: SingleLiveEvent<Boolean>? = null

    private var mProductsRepository = ProductsRepository()

    private var mProductsLst = ArrayList<ProductsEntity>()
    private var mCurrentCategory: ParentCategory? = null
    //var mCurrentParentCat: ParentCategory? = null


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

    fun observerGetProducts(catId: Int): SingleLiveEvent<java.util.ArrayList<ProductsEntity>> {
        if (mLvdProducts == null) {
            mLvdProducts = SingleLiveEvent()
        }

        if (!mProductsLst.isNullOrEmpty()) {
            mLvdProducts?.value = mProductsLst
        } else {
            loader?.value = true
            mProductsRepository.setListener(this)
            mProductsRepository.getProductDetails(catId)
        }

        return mLvdProducts!!
    }

    fun observerChangeProductState(): SingleLiveEvent<HashMap<Int, ProductsEntity>> {
        if (mLvdProductChangeState == null) {
            mLvdProductChangeState = SingleLiveEvent()
        }
        return mLvdProductChangeState!!
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

    fun observerSaveAsDraft(): SingleLiveEvent<Boolean> {
        if (mLvdSaveAsDraft == null) {
            mLvdSaveAsDraft = SingleLiveEvent()
        }
        return mLvdSaveAsDraft!!
    }

    fun setCurrentCategory(parentCategory: ParentCategory?) {
        mCurrentCategory = parentCategory
    }
    // End of Live Data Initialization


    fun onProductChangeState(position: Int, productsEntity: ProductsEntity) {
        val obj = mProductsLst.get(position)
        obj.isSelected = !productsEntity.isSelected

        val tempMap = HashMap<Int, ProductsEntity>()
        tempMap.put(position, obj)
        mLvdProductChangeState?.value = tempMap
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

        val selectedLst = mProductsRepository.getProductsByParentCatId(mCurrentCategory?.id!!)
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

    fun saveProductsInDB(parentCatName: String?, isSaveAsDraft: Boolean = true) {
        val selectedProductLst = mProductsRepository.saveProductsInDB(getProductsToSave(), parentCatName)
        if (isSaveAsDraft) {
            mLvdSaveAsDraft?.value = true
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
                mLvdProducts?.value = mProductsLst
            } else {
                mLvdProducts?.value = null
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