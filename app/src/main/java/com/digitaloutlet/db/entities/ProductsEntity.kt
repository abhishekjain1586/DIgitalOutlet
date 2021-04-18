package com.digitaloutlet.db.entities

import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Entity(tableName = "products", primaryKeys = arrayOf("product_id","merchant_id"))
class ProductsEntity() : Parcelable {

    @ColumnInfo(name = "parent_cat_id")
    @SerializedName("parent_cat_id")
    var parentCatId: String? = null

    @ColumnInfo(name = "parent_cat_name")
    @SerializedName("parent_cat_name")
    var parentCatName: String? = null

    @ColumnInfo(name = "sub_cat_name")
    @SerializedName("sub_cat")
    var subCatName: String? = null

    @NonNull
    @ColumnInfo(name = "product_id")
    @SerializedName("product_id")
    var productId: Int? = null

    @ColumnInfo(name = "product_name")
    @SerializedName("product_name")
    var productName: String? = null

    @ColumnInfo(name = "brand_name")
    @SerializedName("brand_name")
    var brandName: String? = null

    @ColumnInfo(name = "quantity")
    @SerializedName("quantity")
    var quantity: String? = null

    @ColumnInfo(name = "unit")
    @SerializedName("unit")
    var unit: String? = null

    @ColumnInfo(name = "image")
    @SerializedName("image")
    var image: String? = null

    @ColumnInfo(name = "status")
    @SerializedName("status")
    var status: String? = null

    /*@ColumnInfo(name = "createdAt")
    @SerializedName("createdAt")
    var createdAt: String? = null*/

    @ColumnInfo(name = "type_of_shop")
    @Expose(serialize = false, deserialize = false)
    var typeOfShop: String? = null

    @NonNull
    @ColumnInfo(name = "merchant_id")
    @Expose(serialize = false, deserialize = false)
    var merchantId: String? = null

    @ColumnInfo(name = "price")
    @Expose(serialize = false, deserialize = false)
    var price: Double? = null

    @ColumnInfo(name = "product_state")
    @Expose(serialize = false, deserialize = false)
    var productStateForMerchant: String? = null

    @ColumnInfo(name = "action")
    @Expose(serialize = false, deserialize = false)
    var action: String? = null

    @Ignore
    @Expose(serialize = false, deserialize = false)
    var isSelected: Boolean = false


    constructor(parcel: Parcel) : this() {
        parentCatId = parcel.readString()
        parentCatName = parcel.readString()
        subCatName = parcel.readString()
        productId = parcel.readValue(Int::class.java.classLoader) as? Int
        productName = parcel.readString()
        brandName = parcel.readString()
        quantity = parcel.readString()
        unit = parcel.readString()
        image = parcel.readString()
        status = parcel.readString()
        /*createdAt = parcel.readString()*/
        typeOfShop = parcel.readString()
        merchantId = parcel.readString()
        price = parcel.readValue(Double::class.java.classLoader) as? Double
        productStateForMerchant = parcel.readString()
        action = parcel.readString()
        isSelected = parcel.readByte() != 0.toByte()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(parentCatId)
        parcel.writeString(parentCatName)
        parcel.writeString(subCatName)
        parcel.writeValue(productId)
        parcel.writeString(productName)
        parcel.writeString(brandName)
        parcel.writeString(quantity)
        parcel.writeString(unit)
        parcel.writeString(image)
        parcel.writeString(status)
        /*parcel.writeString(createdAt)*/
        parcel.writeString(typeOfShop)
        parcel.writeString(merchantId)
        parcel.writeValue(price)
        parcel.writeString(productStateForMerchant)
        parcel.writeString(action)
        parcel.writeByte(if (isSelected) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object {
        val CREATOR = object : Parcelable.Creator<ProductsEntity> {
            override fun createFromParcel(parcel: Parcel): ProductsEntity {
                return ProductsEntity(parcel)
            }

            override fun newArray(size: Int): Array<ProductsEntity?> {
                return arrayOfNulls(size)
            }
        }


        const val PRODUCT_STATE_DRAFT = "DRAFT"
        const val PRODUCT_STATE_PUBLISHED = "PUBLISHED"
        const val ACTION_ADD = "A"
        const val ACTION_DELETE = "D"
        const val ACTION_UPDATE = "U"
        const val ACTION_STATE_ACTIVE = "S"
    }

    override fun hashCode(): Int {
        return productId.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (other is ProductsEntity) {
            return other.productId == productId
        }
        return false
    }

    /*companion object CREATOR : Parcelable.Creator<ProductsEntity> {
        override fun createFromParcel(parcel: Parcel): ProductsEntity {
            return ProductsEntity(parcel)
        }

        override fun newArray(size: Int): Array<ProductsEntity?> {
            return arrayOfNulls(size)
        }


        const val PRODUCT_STATE_DRAFT = "DRAFT"
        const val PRODUCT_STATE_PUBLISHED = "PUBLISHED"
        const val ACTION_ADD = "A"
        const val ACTION_DELETE = "D"
        const val ACTION_UPDATE = "U"
    }*/


}