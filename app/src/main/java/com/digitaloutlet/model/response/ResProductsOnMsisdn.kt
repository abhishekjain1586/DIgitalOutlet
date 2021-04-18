package com.digitaloutlet.model.response

import com.google.gson.annotations.SerializedName

class ResProductsOnMsisdn {

    @SerializedName("message")
    var message: String? = null

    @SerializedName("status")
    var status: Int? = null

    @SerializedName("records")
    var records: ArrayList<ProductsOnMsisdn>? = null

}