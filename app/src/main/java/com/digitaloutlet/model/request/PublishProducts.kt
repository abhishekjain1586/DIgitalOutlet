package com.digitaloutlet.model.request

import com.google.gson.annotations.SerializedName

class PublishProducts {

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

    @SerializedName("brand_name")
    var brand_name: String? = null

    @SerializedName("quantity")
    var quantity: String? = null

    @SerializedName("unit")
    var unit: String? = null

    @SerializedName("image_link")
    var image_link: String? = null

    @SerializedName("actual_price")
    var actual_price: String? = null

    @SerializedName("action")
    var action: String? = null
}