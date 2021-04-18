package com.digitaloutlet.model.response

import com.digitaloutlet.db.entities.ProductsEntity
import com.google.gson.annotations.SerializedName

class ResProducts {

    @SerializedName("message")
    var message: String? = null

    @SerializedName("status")
    var status: Int? = null

    @SerializedName("records")
    var records: ArrayList<ProductsEntity>? = null
}