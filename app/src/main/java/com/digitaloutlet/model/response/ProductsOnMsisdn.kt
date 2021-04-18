package com.digitaloutlet.model.response

import com.google.gson.annotations.SerializedName

class ProductsOnMsisdn {

    @SerializedName("id")
    var id: Int? = null

    @SerializedName("merchant_id")
    var merchant_id: String? = null

    @SerializedName("parent_cat_id")
    var parent_cat_id: String? = null

    @SerializedName("parent_cat_name")
    var parent_cat_name: String? = null

    @SerializedName("sub_cat_name")
    var sub_cat_name: String? = null

    @SerializedName("product_id")
    var product_id: String? = null

    @SerializedName("product_name")
    var product_name: String? = null

    @SerializedName("quantity")
    var quantity: String? = null

    @SerializedName("unit")
    var unit: String? = null

    @SerializedName("image_link")
    var image_link: String? = null

    @SerializedName("actual_price")
    var actual_price: String? = null

    @SerializedName("product_state")
    var product_state: String? = null

    @SerializedName("createdAt")
    var createdAt: String? = null

}