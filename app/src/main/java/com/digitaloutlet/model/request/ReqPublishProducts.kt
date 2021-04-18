package com.digitaloutlet.model.request

import com.google.gson.annotations.SerializedName

class ReqPublishProducts {

    @SerializedName("details")
    var details: ArrayList<PublishProducts>? = null
}