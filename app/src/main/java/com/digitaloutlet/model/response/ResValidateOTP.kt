package com.digitaloutlet.model.response

import com.google.gson.annotations.SerializedName

class ResValidateOTP {

    @SerializedName("message")
    var message: String? = null

    @SerializedName("status")
    var status: Int? = null

}