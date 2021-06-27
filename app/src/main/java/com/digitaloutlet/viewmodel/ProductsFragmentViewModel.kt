package com.digitaloutlet.viewmodel

import androidx.lifecycle.ViewModel
import com.digitaloutlet.db.entities.ProductsEntity
import com.digitaloutlet.model.response.ParentCategory
import com.digitaloutlet.model.response.ResProducts
import com.digitaloutlet.repository.ProductsRepository
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class ProductsFragmentViewModel : ViewModel(), ProductsRepository.OnProductsListener {

    private var loader: SingleLiveEvent<Boolean>? = null
    private var errorDialog: SingleLiveEvent<String>? = null
    private var mLvdProducts: SingleLiveEvent<ArrayList<ProductsEntity>>? = null
    private var mLvdProductChangeState: SingleLiveEvent<HashMap<Int, ProductsEntity>>? = null
    private var mLvdSaveAndProceed: SingleLiveEvent<ArrayList<ProductsEntity>>? = null
    private var mLvdSaveAsDraft: SingleLiveEvent<Boolean>? = null

    private var mProductsRepository = ProductsRepository()

    private var mProductsLst = ArrayList<ProductsEntity>()
    private var mCurrentCategory: ParentCategory? = null


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

    fun obsvSaveAndProceed(): SingleLiveEvent<ArrayList<ProductsEntity>> {
        if (mLvdSaveAndProceed == null) {
            mLvdSaveAndProceed = SingleLiveEvent()
        }
        return mLvdSaveAndProceed!!
    }
    // End of Live Data Initialization


    // Live Data Initialization For Price Screen
    fun obsvSaveAsDraft(): SingleLiveEvent<Boolean> {
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

    private fun getSubcategoryWiseProducts(productsLst: ArrayList<ProductsEntity>): ArrayList<ProductsEntity> {
        val subcatWiseProductsLst = ArrayList<ProductsEntity>()
        var unsavedProductsLst = ArrayList<ProductsEntity>()

        val savedProductsLst = mProductsRepository.getProductsByParentCatId(mCurrentCategory?.id!!)
        if (!savedProductsLst.isNullOrEmpty()) {
            subcatWiseProductsLst.addAll(savedProductsLst)
            unsavedProductsLst.addAll(getUnsavedProducts(productsLst, getSavedProductsMap(savedProductsLst)))
        } else {
            unsavedProductsLst.addAll(productsLst)
        }

        unsavedProductsLst.sortWith(Comparator { o1, o2 -> o1.subCatName!!.compareTo(o2.subCatName!!)})

        subcatWiseProductsLst.addAll(addSubcatElement(unsavedProductsLst))
        return subcatWiseProductsLst
    }

    private fun getSavedProductsMap(selectedLst: ArrayList<ProductsEntity>): HashMap<Int, ProductsEntity> {
        val mapSavedLst = HashMap<Int, ProductsEntity>()
        for (i in 0 until selectedLst.size) {
            selectedLst.get(i).isSelected = true
            mapSavedLst.put(selectedLst.get(i).productId!!, selectedLst.get(i))
        }
        return mapSavedLst
    }

    private fun getUnsavedProducts(productsLst: ArrayList<ProductsEntity>, savedProductsMap: HashMap<Int, ProductsEntity>): ArrayList<ProductsEntity> {
        val tempLst = ArrayList<ProductsEntity>()
        for (i in 0 until productsLst.size) {
            if (savedProductsMap.get(productsLst.get(i).productId) == null) {
                tempLst.add(productsLst.get(i))
            }
        }
        return tempLst
    }

    private fun addSubcatElement(lst: ArrayList<ProductsEntity>): ArrayList<ProductsEntity> {
        val mapSubCat = TreeMap<Int, ProductsEntity>()
        for (i in 0 until lst.size) {
            if (i == 0 || (lst.size > 1 && !lst.get(i).subCatName.equals(lst.get(i-1).subCatName))) {
                val obj = ProductsEntity()
                obj.subCatName = lst.get(i).subCatName
                var pos = 0
                if (i > 0) {
                    pos = i + 1
                }
                mapSubCat.put(pos, obj)
            }
        }
        val keys = ArrayList<Int>(mapSubCat.keys)
        for (i in keys.size - 1 downTo 0) {
            System.out.println(mapSubCat.get(keys[i]))
            lst.add(keys.get(i), mapSubCat.get(keys[i]) as ProductsEntity)
        }
        /*val iterator = mapSubCat.entries.iterator()
        while (iterator.hasNext()) {
            val mapElement = iterator.next() as Map.Entry<Int, ProductsEntity>
            lst.add(mapElement.key, mapElement.value)
        }*/
        return lst
    }

    fun saveProductsInDB(parentCatName: String?, isSaveAsDraft: Boolean = true) {
        val selectedProductLst = mProductsRepository.saveProductsInDB(getProductsToSave(), parentCatName)
        if (isSaveAsDraft) {
            mLvdSaveAsDraft?.value = true
        } else {
            mLvdSaveAndProceed?.value = selectedProductLst
        }
    }

    override fun onSuccessProducts(response: ResProducts) {
        loader?.value = false
        if (response.status == 1) {
            if (!response.records.isNullOrEmpty()) {
                mProductsLst.clear()
                mProductsLst.addAll(getSubcategoryWiseProducts(response.records!!))
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