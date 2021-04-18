package com.digitaloutlet.db.dao

import androidx.room.*
import com.digitaloutlet.db.entities.ProductsEntity

@Dao
abstract class ProductsDao : BaseDao<ProductsEntity>() {

    @Query("SELECT * FROM products WHERE merchant_id=:merchantId")
    abstract fun getProductsByMerchant(merchantId: String) : List<ProductsEntity>?

    @Query("select * from products where merchant_id=:merchantId AND parent_cat_id=:parentCatId")
    abstract fun getProductsByParentCategoryId(merchantId: String, parentCatId: Int) : List<ProductsEntity>?

    @Query("select * from products where merchant_id=:merchantId AND parent_cat_id=:parentCatId AND `action` is not 'D'")
    abstract fun getActiveProductsPricesByParentCategoryId(merchantId: String, parentCatId: String) : List<ProductsEntity>?

    /*@Query("SELECT * FROM products where merchant_id=:merchantId AND parent_cat_id=:parentCatId AND `action`!='D'")
    abstract fun getProductsPrices(merchantId: String, parentCatId: Int) : List<ProductsEntity>?*/

    @Query("SELECT * FROM products WHERE merchant_id=:merchantId AND `action` is not NULL ORDER BY parent_cat_id")
    abstract fun getAllProductsToPublish(merchantId: String) : List<ProductsEntity>?

    @Query("SELECT * FROM products WHERE merchant_id=:merchantId AND `action` is not NULL GROUP BY parent_cat_id")
    abstract fun getMerchantCategoriesToPublish(merchantId: String) : List<ProductsEntity>?

    @Query("SELECT * FROM products WHERE merchant_id=:merchantId AND product_state=:productState GROUP BY parent_cat_name")
    abstract fun getCategoriesByState(merchantId: String, productState: String) : List<ProductsEntity>?

    @Query("DELETE from products where merchant_id=:merchantId and product_id in (:productIdList)")
    abstract fun deletePublishedProducts(merchantId: String, productIdList: List<Int>)

    @Query("DELETE from products where merchant_id=:merchantId and product_id=:productId")
    abstract fun deletePublishedProductByID(merchantId: String, productId: Int)

    //@Query("SELECT * from products where merchant_id=:merchantId and product_state=:productState OR product_state='DRAFT' and parent_cat_name=:parentCatName")
    @Query("SELECT * from products where merchant_id=:merchantId and product_state=:productState and parent_cat_name=:parentCatName")
    abstract fun getProductsByStateByParentCatName(merchantId: String, productState: String, parentCatName: String) : List<ProductsEntity>?

    @Query("SELECT * FROM products WHERE merchant_id=:merchantId AND product_id=:productID")
    abstract fun getProductByID(merchantId: String, productID: Int) : ProductsEntity
}