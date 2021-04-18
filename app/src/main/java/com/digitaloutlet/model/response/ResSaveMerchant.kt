package com.digitaloutlet.model.response

import com.google.gson.annotations.SerializedName

class ResSaveMerchant {

    @SerializedName("message")
    var message: String? = null

    @SerializedName("merchant_id")
    var merchant_id: String? = null

    @SerializedName("action")
    var action: String? = null

    @SerializedName("status")
    var status: Int? = null

}