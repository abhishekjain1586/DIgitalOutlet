package com.digitaloutlet.repository

import com.digitaloutlet.db.DBHelper
import com.digitaloutlet.db.entities.ProductsEntity
import com.digitaloutlet.model.bean.PublishedProductsBean
import com.digitaloutlet.model.response.ParentCategory
import com.digitaloutlet.utils.DOPrefs

class DashboardRepository {

    fun getProductsToBePublished(): ArrayList<ProductsEntity>? {
        return DBHelper.getInstance().getProductsDao().getAllProductsToPublish(DOPrefs.getMerchantId()) as ArrayList<ProductsEntity>
    }

    fun getDraftProducts(): ArrayList<ParentCategory>? {
        val categoriesLst = ArrayList<ParentCategory>()
        val lst = DBHelper.getInstance().getProductsDao().getCategoriesByState(DOPrefs.getMerchantId(), ProductsEntity.PRODUCT_STATE_DRAFT) as ArrayList<ProductsEntity>
        for (productObj in lst) {
            val catObj = ParentCategory()
            catObj.id = productObj.parentCatId?.toInt()
            catObj.parent_cat = productObj.parentCatName
            categoriesLst.add(catObj)
        }
        return categoriesLst
    }

    fun getPublishedCategories(): ArrayList<ProductsEntity>? {
        return DBHelper.getInstance().getProductsDao().getCategoriesByState(DOPrefs.getMerchantId(), ProductsEntity.PRODUCT_STATE_PUBLISHED) as ArrayList<ProductsEntity>
    }

    fun getPublishedProductsByParentCat(parentCatName: String): ArrayList<ProductsEntity> {
        return DBHelper.getInstance().getProductsDao()
            .getProductsByStateByParentCatName(
                DOPrefs.getMerchantId(),
                ProductsEntity.PRODUCT_STATE_PUBLISHED,
                parentCatName) as ArrayList<ProductsEntity>
    }

    fun getPublishedProductsBean(): ArrayList<PublishedProductsBean> {
        var beanLst = ArrayList<PublishedProductsBean>()
        var publishedCatLst = getPublishedCategories()
        if (!publishedCatLst.isNullOrEmpty()) {
            for (publishedCatObj in publishedCatLst) {
                var beanObj = PublishedProductsBean()
                beanObj.parentCatId = publishedCatObj.parentCatId
                beanObj.parentCatName = publishedCatObj.parentCatName

                var childLst = ArrayList<PublishedProductsBean>()
                var entityLst = getPublishedProductsByParentCat(publishedCatObj.parentCatName!!)
                for (entityObj in entityLst) {
                    var childObj = PublishedProductsBean()
                    childObj.isGroup = false
                    childObj.parentCatName = entityObj.parentCatName
                    childObj.parentCatId = entityObj.parentCatId
                    childObj.brandName = entityObj.brandName
                    childObj.image = entityObj.image
                    childObj.merchantId = entityObj.merchantId
                    childObj.price = entityObj.price
                    childObj.productId = entityObj.productId
                    childObj.productName = entityObj.productName
                    childObj.quantity = entityObj.quantity
                    childObj.subCatName = entityObj.subCatName
                    childObj.unit = entityObj.unit
                    childLst.add(childObj)
                }
                beanObj.productsLst.addAll(childLst)
                beanLst.add(beanObj)
            }
        }

        return beanLst
    }

    fun deleteProduct(childObj: PublishedProductsBean) {

    }
}