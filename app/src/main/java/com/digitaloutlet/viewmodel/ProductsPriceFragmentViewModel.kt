package com.digitaloutlet.viewmodel

import androidx.lifecycle.ViewModel
import com.digitaloutlet.db.entities.ProductsEntity
import com.digitaloutlet.model.response.ParentCategory
import com.digitaloutlet.repository.ProductsRepository
import com.digitaloutlet.utils.Constants

class ProductsPriceFragmentViewModel : ViewModel() {

    private var loader: SingleLiveEvent<Boolean>? = null
    private var errorDialog: SingleLiveEvent<String>? = null
    private var mLiveDataMerchantActiveProducts: SingleLiveEvent<ArrayList<ProductsEntity>>? = null
    private var mLiveDataMoveToNextCategoryObserver: SingleLiveEvent<Boolean>? = null
    private var mLiveDataSaveAsDraft: SingleLiveEvent<Boolean>? = null

    private val mProductsRepository = ProductsRepository()

    private var mCurrentParentCat: ParentCategory? = null
    private var mMerchantActiveProductsLst = ArrayList<ProductsEntity>()


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

    fun getAllMerchantActiveProductsObserver(): SingleLiveEvent<java.util.ArrayList<ProductsEntity>> {
        if (mLiveDataMerchantActiveProducts == null) {
            mLiveDataMerchantActiveProducts = SingleLiveEvent()
        }
        return mLiveDataMerchantActiveProducts!!
    }

    fun moveToNextCategoryObserver(): SingleLiveEvent<Boolean> {
        if (mLiveDataMoveToNextCategoryObserver == null) {
            mLiveDataMoveToNextCategoryObserver = SingleLiveEvent()
        }
        return mLiveDataMoveToNextCategoryObserver!!
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

    /*fun setMerchantActiveProducts(lst: ArrayList<ProductsEntity>) {
        mMerchantActiveProductsLst.clear()
        mMerchantActiveProductsLst.addAll(lst)

        mLiveDataMerchantActiveProducts?.value = mMerchantActiveProductsLst
    }*/

    fun getProducts(parentCatID: String) {
        mMerchantActiveProductsLst.clear()
        val lst = mProductsRepository.getActiveProductsPricesByParentCategoryId(parentCatID)
        lst?.let {
            mMerchantActiveProductsLst.addAll(it)
            mLiveDataMerchantActiveProducts?.value = mMerchantActiveProductsLst
        }
    }

    fun setMerchantActiveProducts(productID: Int) {
        mMerchantActiveProductsLst.clear()
        mMerchantActiveProductsLst.add(mProductsRepository.getProductByID(productID))

        mLiveDataMerchantActiveProducts?.value = mMerchantActiveProductsLst
    }

    fun savePriceInDB(isSaveAsDraft: Boolean) {
        mProductsRepository.savePriceInDB(mMerchantActiveProductsLst)

        if (isSaveAsDraft) {
            mLiveDataSaveAsDraft?.value = true
        } else {
            mLiveDataMoveToNextCategoryObserver?.value = true
        }

    }

    fun updateProductPrice(position: Int, price: String) {
        if (price.isNotEmpty()) {
            mMerchantActiveProductsLst.get(position).price = price.toDouble()
        } else {
            mMerchantActiveProductsLst.get(position).price = null
        }
    }

}