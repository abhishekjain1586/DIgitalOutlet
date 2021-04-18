package com.digitaloutlet.viewholder

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.digitaloutlet.R

class ProductSummaryHeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var mTvParentCatName: TextView = itemView.findViewById(R.id.tv_parent_cat_name)
    var mTvRatesTitle: TextView = itemView.findViewById(R.id.tv_rates_title)
}