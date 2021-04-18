package com.digitaloutlet.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.digitaloutlet.R
import com.digitaloutlet.db.entities.ProductsEntity
import com.digitaloutlet.utils.Constants
import com.digitaloutlet.viewholder.ProductSummaryHeaderViewHolder
import com.digitaloutlet.viewholder.ProductSummaryViewHolder

private const val HEADER_VIEW = 1
private const val SUMMARY_VIEW = 2

class SummaryAdapter(context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val mContext = context
    private val mProductsLst = ArrayList<ProductsEntity>()


    fun setData(tosLst: ArrayList<ProductsEntity>) {
        mProductsLst.clear()
        mProductsLst.addAll(tosLst)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == HEADER_VIEW) {
            val itemView : View = LayoutInflater.from(mContext).inflate(R.layout.item_summary_header, parent, false)
            ProductSummaryHeaderViewHolder(itemView)
        } else {
            val itemView : View = LayoutInflater.from(mContext).inflate(R.layout.item_summary_product, parent, false)
            ProductSummaryViewHolder(itemView)
        }
    }

    override fun getItemCount(): Int {
        return mProductsLst.size
    }

    override fun getItemViewType(position: Int): Int {
        if (mProductsLst.get(position).productId == null) {
            return HEADER_VIEW
        }

        return SUMMARY_VIEW
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val productObj = mProductsLst.get(position)

        if (getItemViewType(position) == HEADER_VIEW) {
            productObj.parentCatName?.let {
                (holder as ProductSummaryHeaderViewHolder).mTvParentCatName.text = it
            }
        } else {
            productObj.brandName?.let {
                (holder as ProductSummaryViewHolder).mTvBrandName.text = it.trim().toUpperCase()
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
                (holder as ProductSummaryViewHolder).mTvProductName.text = it.trim() + quantity + unit
            }

            productObj.price?.let {
                (holder as ProductSummaryViewHolder).mTvProductPrice.text = mContext.resources.getString(R.string.Rs) + it
            } ?: run {
                (holder as ProductSummaryViewHolder).mTvProductPrice.text = mContext.resources.getString(R.string.not_available)
            }
        }
    }
}