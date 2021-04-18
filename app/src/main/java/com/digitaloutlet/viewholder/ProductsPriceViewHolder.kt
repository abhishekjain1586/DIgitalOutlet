package com.digitaloutlet.viewholder

import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.digitaloutlet.R
import com.digitaloutlet.application.DOApplication
import com.digitaloutlet.db.entities.ProductsEntity
import com.digitaloutlet.utils.Constants
import com.digitaloutlet.utils.inputfilter.DecimalInputFilter

class ProductsPriceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private var mListener: OnItemClickListener<String>? = null
    private var mPosition = -1

    var mTvBrandName: TextView = itemView.findViewById(R.id.tv_brand_name)
    var mTvProductName: TextView = itemView.findViewById(R.id.tv_product_name)
    var mEdtProductPrice: EditText = itemView.findViewById(R.id.edt_product_price)

    init {
        mEdtProductPrice.filters = arrayOf(DecimalInputFilter(5, 2),
            InputFilter.LengthFilter(DOApplication._INSTANCE.resources.getInteger(R.integer.price_length)))
    }

    fun onBindData(position: Int, productEntity: ProductsEntity, listener: OnItemClickListener<String>) {
        mPosition = position
        mListener = listener
        mEdtProductPrice.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (mEdtProductPrice.text.toString().trim() != Constants.DOT) {
                    mListener?.onItemClick(mPosition, mEdtProductPrice.text.toString(), 0)
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //productEntity.price = mEdtProductPrice.text.toString().toDouble()
            }
        })
    }
}