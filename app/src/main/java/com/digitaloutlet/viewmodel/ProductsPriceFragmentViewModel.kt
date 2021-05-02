package com.digitaloutlet.viewmodel

import androidx.lifecycle.ViewModel
import com.digitaloutlet.db.entities.ProductsEntity
import com.digitaloutlet.model.response.ParentCategory
import com.digitaloutlet.repository.ProductsRepository
import com.digitaloutlet.view.enums.ProductSelectionState

class ProductsPriceFragmentViewModel : ViewModel() {

    private var loader: SingleLiveEvent<Boolean>? = null
    private var errorDialog: SingleLiveEvent<String>? = null
    private var mLvdProducts: SingleLiveEvent<ArrayList<ProductsEntity>>? = null
    private var mLvdMoveToNextCategory: SingleLiveEvent<Boolean>? = null
    private var mLvdSaveAsDraft: SingleLiveEvent<Boolean>? = null

    private val mProductsRepository = ProductsRepository()

    private lateinit var mSelectionState: ProductSelectionState
    private var mCurrentCategory: ParentCategory? = null
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

    fun obsvGetProducts(): SingleLiveEvent<java.util.ArrayList<ProductsEntity>> {
        if (mLvdProducts == null) {
            mLvdProducts = SingleLiveEvent()
        }
        return mLvdProducts!!
    }

    fun obsvMoveToNextCategory(): SingleLiveEvent<Boolean> {
        if (mLvdMoveToNextCategory == null) {
            mLvdMoveToNextCategory = SingleLiveEvent()
        }
        return mLvdMoveToNextCategory!!
    }

    fun obsvSaveAsDraft(): SingleLiveEvent<Boolean> {
        if (mLvdSaveAsDraft == null) {
            mLvdSaveAsDraft = SingleLiveEvent()
        }
        return mLvdSaveAsDraft!!
    }
    // End of Live Data Initialization

    fun setCurrentCategory(parentCat: ParentCategory?) {
        mCurrentCategory = parentCat
    }

    fun setProductSelectionState(selectionState: ProductSelectionState) {
        mSelectionState = selectionState
    }

    fun getCurrentCategory(): ParentCategory? {
        return mCurrentCategory
    }

    fun getProducts(productID: Int?) {
        if (mSelectionState == ProductSelectionState.EDIT_PRODUCT) {
            getProductByID(productID!!)
            return
        }
        mMerchantActiveProductsLst.clear()
        var lst: ArrayList<ProductsEntity>?
        var state = ProductsEntity.PRODUCT_STATE_DRAFT
        if (mSelectionState == ProductSelectionState.EDIT_CATEGORIES) {
            state = ProductsEntity.PRODUCT_STATE_PUBLISHED
        }
        lst = mProductsRepository.getProductsByState("" + mCurrentCategory?.id, state)
        lst?.let {
            mMerchantActiveProductsLst.addAll(it)
            mLvdProducts?.value = mMerchantActiveProductsLst
        }
    }

    fun getProductByID(productID: Int) {
        mMerchantActiveProductsLst.clear()
        mMerchantActiveProductsLst.add(mProductsRepository.getProductByID(productID))

        mLvdProducts?.value = mMerchantActiveProductsLst
    }

    fun savePriceInDB(isSaveAsDraft: Boolean) {
        mProductsRepository.savePriceInDB(mMerchantActiveProductsLst)

        if (isSaveAsDraft) {
            mLvdSaveAsDraft?.value = true
        } else {
            mLvdMoveToNextCategory?.value = true
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