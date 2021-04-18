package com.digitaloutlet.model.request

import com.google.gson.annotations.SerializedName

class ReqGenerateOTP {

    @SerializedName("msisdn")
    var msisdn: String? = null

}