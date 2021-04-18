package com.digitaloutlet.adapter

import android.content.Context
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.digitaloutlet.R
import com.digitaloutlet.db.entities.ProductsEntity
import com.digitaloutlet.utils.Constants
import com.digitaloutlet.viewholder.OnItemClickListener
import com.digitaloutlet.viewholder.ProductsPriceViewHolder

class ProductsPriceAdapter(context: Context) : RecyclerView.Adapter<ProductsPriceViewHolder>() {

    private val mContext = context
    private val mProductsLst = ArrayList<ProductsEntity>()
    private var mListener: OnItemClickListener<String>? = null

    fun setListener(listener: OnItemClickListener<String>) {
        mListener = listener
    }

    fun setData(tosLst: ArrayList<ProductsEntity>) {
        mProductsLst.clear()
        mProductsLst.addAll(tosLst)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductsPriceViewHolder {
        var resource = R.layout.item_product_price
        val view : View = LayoutInflater.from(mContext).inflate(resource, parent, false)
        return ProductsPriceViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mProductsLst.size
    }

    override fun onBindViewHolder(holder: ProductsPriceViewHolder, position: Int) {
        val productObj = mProductsLst.get(position)

        mListener?.let {
            holder.onBindData(position, productObj, it)
        }

        productObj.brandName?.let {
            holder.mTvBrandName.text = it.trim().toUpperCase()
        }

        var quantity = Constants.EMPTY
        productObj.quantity?.let {
            quantity = Constants.SPACE + Constants.DASH + Constants.SPACE + it
        }
        var unit = Constants.EMPTY
        productObj.unit?.let {
            unit = Constants.SPACE + it
        }

        productObj.productName?.let {
            holder.mTvProductName.text = it.trim() + quantity + unit
        }

        //holder.mEdtProductPrice.filters = arrayOf<InputFilter>(Decimal)
        productObj.price?.let {
            holder.mEdtProductPrice.setText(Constants.EMPTY + it)
        }
    }
}