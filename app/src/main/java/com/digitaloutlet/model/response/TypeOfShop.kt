package com.digitaloutlet.model.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class TypeOfShop {

    @SerializedName("id")
    var id: Int? = null

    @SerializedName("type")
    var type: String? = null

    @SerializedName("value")
    var value: String? = null

    @SerializedName("createdAt")
    var createdAt: String? = null

    @SerializedName("updatedAt")
    var updatedAt: String? = null

    @Expose(serialize = false, deserialize = false)
    var isSelected: Boolean = false
}