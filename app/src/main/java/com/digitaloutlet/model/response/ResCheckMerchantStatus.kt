package com.digitaloutlet.model.response

import com.google.gson.annotations.SerializedName

class ResCheckMerchantStatus {

    @SerializedName("message")
    var message: String? = null

    @SerializedName("merchant_id")
    var merchant_id: String? = null

    @SerializedName("status")
    var status: Int? = null

}