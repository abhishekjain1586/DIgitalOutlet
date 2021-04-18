package com.digitaloutlet.viewholder

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.digitaloutlet.R

class ProductSummaryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var mTvBrandName: TextView = itemView.findViewById(R.id.tv_brand_name)
    var mTvProductName: TextView = itemView.findViewById(R.id.tv_product_name)
    var mTvProductPrice: TextView = itemView.findViewById(R.id.tv_product_price)
}