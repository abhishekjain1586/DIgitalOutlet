package com.digitaloutlet.model.response

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ParentCategory() : Parcelable {

    @SerializedName("id")
    var id: Int? = null

    @SerializedName("parent_cat")
    var parent_cat: String? = null

    @SerializedName("image")
    var image: String? = null

    @Expose(serialize = false, deserialize = false)
    var isSelected: Boolean = false

    constructor(parcel: Parcel) : this() {
        //id = parcel.readInt()
        id = parcel.readValue(Int::class.java.classLoader) as? Int
        parent_cat = parcel.readString()
        image = parcel.readString()
        isSelected = parcel.readByte() != 0.toByte()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        //parcel.writeInt(id?:0)
        parcel.writeValue(id)
        parcel.writeString(parent_cat)
        parcel.writeString(image)
        parcel.writeByte(if (isSelected) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ParentCategory> {
        override fun createFromParcel(parcel: Parcel): ParentCategory {
            return ParentCategory(parcel)
        }

        override fun newArray(size: Int): Array<ParentCategory?> {
            return arrayOfNulls(size)
        }
    }

}