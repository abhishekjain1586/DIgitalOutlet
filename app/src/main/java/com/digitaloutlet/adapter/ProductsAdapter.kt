package com.digitaloutlet.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.digitaloutlet.R
import com.digitaloutlet.application.DOApplication
import com.digitaloutlet.db.entities.ProductsEntity
import com.digitaloutlet.service.ServiceHelper
import com.digitaloutlet.utils.Constants
import com.digitaloutlet.viewholder.OnItemClickListener
import com.digitaloutlet.viewholder.ProductSummaryHeaderViewHolder
import com.digitaloutlet.viewholder.ProductsViewHolder
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso

private const val HEADER_VIEW = 1
private const val PRODUCT_VIEW = 2

class ProductsAdapter(context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val mContext = context
    private val mOriginalDataLst = ArrayList<ProductsEntity>()
    private var mProductsLst = ArrayList<ProductsEntity>()
    private var mListener: OnItemClickListener<ProductsEntity> ? = null
    private val mFilter = ItemFilter()

    fun setData(tosLst: ArrayList<ProductsEntity>) {
        mOriginalDataLst.clear()
        mOriginalDataLst.addAll(tosLst)
        mProductsLst.clear()
        mProductsLst.addAll(tosLst)
    }

    fun setItemClickListener(listener : OnItemClickListener<ProductsEntity>) {
        mListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var resource: Int
        var view: View
        return if (viewType == HEADER_VIEW) {
            resource = R.layout.item_summary_header
            view = LayoutInflater.from(mContext).inflate(resource, parent, false)
            ProductSummaryHeaderViewHolder(view)
        } else {
            resource = R.layout.item_selected_product
            view = LayoutInflater.from(mContext).inflate(resource, parent, false)
            ProductsViewHolder(view)
        }
    }

    override fun getItemCount(): Int {
        return mProductsLst.size
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads)
        } else {
            val payloadObj = payloads.get(0) as ProductsEntity
            val productEntityObj = mProductsLst.get(position)

            /*productEntityObj.merchantId = payloadObj.merchantId
            productEntityObj.subCatId = payloadObj.subCatId
            productEntityObj.productStateForMerchant = payloadObj.productStateForMerchant
            productEntityObj.action = payloadObj.action*/
            productEntityObj.isSelected = payloadObj.isSelected

            if (productEntityObj.isSelected) {
                (holder as ProductsViewHolder).mTvSeletion.background = ContextCompat.getDrawable(mContext, R.drawable.bg_radio_selected) //mContext.resources.getDrawable(R.drawable.bg_radio_selected, null)
            } else {
                (holder as ProductsViewHolder).mTvSeletion.background = ContextCompat.getDrawable(mContext, R.drawable.bg_radio_unselected) //mContext.resources.getDrawable(R.drawable.bg_radio_unselected, null)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val productObj = mProductsLst.get(position)

        if (getItemViewType(position) == HEADER_VIEW) {
            val headerHolder = holder as ProductSummaryHeaderViewHolder
            headerHolder.mTvRatesTitle.visibility = View.GONE
            headerHolder.mTvParentCatName.text = productObj.subCatName
        } else {
            val productHolder = holder as ProductsViewHolder
            mListener?.let {
                productHolder.bind(position, productObj, it)
            }

            productObj.brandName?.let {
                productHolder.mTvBrandName.text = it.trim().toUpperCase()
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
                productHolder.mTvProductName.text = it.trim() + quantity + unit
            }

            productHolder.mTvSeletion.isEnabled = true
            if (productObj.productStateForMerchant == ProductsEntity.PRODUCT_STATE_PUBLISHED) {
                productHolder.mTvSeletion.isEnabled = false
            }

            if (productObj.productStateForMerchant == ProductsEntity.PRODUCT_STATE_PUBLISHED) {
                productHolder.mTvSeletion.setBackgroundResource(R.drawable.bg_radio_selected_disabled)
            } else if (productObj.isSelected) {
                productHolder.mTvSeletion.setBackgroundResource(R.drawable.bg_radio_selected)
            } else {
                productHolder.mTvSeletion.setBackgroundResource(R.drawable.bg_radio_unselected)
            }

            productObj.image?.let {
                //val targetSize = Constants.IMAGE_GRID_SIZE_IN_PIXELS_300
                Picasso.with(mContext).load(ServiceHelper.PRODUCTS_BASE_URL + it)
                    //.resize(1500, 1500)
                    .resizeDimen(R.dimen.item_image_dimen_75dp, R.dimen.item_image_dimen_75dp)
                    //.centerInside()
                    //.placeholder(mContext.resources.getDrawable(R.drawable.no_img_128, null))
                    //.error(ContextCompat.getDrawable(mContext, R.drawable.no_img_128))
                    .into(productHolder.mIvProductImage, object : Callback {
                        override fun onSuccess() {

                        }
                        override fun onError() {

                        }
                    })
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (mProductsLst.get(position).productId == null) {
            return HEADER_VIEW
        }
        return PRODUCT_VIEW
    }

    fun getFilter(): ItemFilter {
        return mFilter
    }

    inner class ItemFilter : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val filterString = constraint.toString().toLowerCase()
            var results = FilterResults()
            val filterLst = ArrayList<ProductsEntity>()
            if (filterString.trim().isNullOrEmpty()) {
                filterLst.clear()
                filterLst.addAll(mOriginalDataLst)
            } else {
                for (tempObj in mOriginalDataLst) {
                    tempObj.productName?.let {
                        if (it.contains(filterString, true)) {
                            filterLst.add(tempObj)
                        }
                    }
                }
            }

            results.values = filterLst
            results.count = filterLst.size
            return results
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            results?.let {
                mProductsLst = it.values as ArrayList<ProductsEntity>
                notifyDataSetChanged()
            }
        }

    }
}