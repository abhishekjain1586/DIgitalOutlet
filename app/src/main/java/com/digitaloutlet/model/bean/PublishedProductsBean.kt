package com.digitaloutlet.model.bean

class PublishedProductsBean {

    var isGroup: Boolean = true

    var isExpanded: Boolean = false

    var isChecked: Boolean = false

    var productId: Int? = null

    var productName: String? = null

    var parentCatId: String? = null

    var parentCatName: String? = null

    var subCatName: String? = null

    var brandName: String? = null

    var quantity: String? = null

    var unit: String? = null

    var image: String? = null

    var merchantId: String? = null

    var price: Double? = null

    var productsLst = ArrayList<PublishedProductsBean>()
}