package com.digitaloutlet.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.digitaloutlet.R
import com.digitaloutlet.model.bean.PublishedProductsBean
import com.digitaloutlet.service.ServiceHelper
import com.digitaloutlet.utils.Constants
import com.digitaloutlet.viewholder.ActiveProductsChildViewHolder
import com.digitaloutlet.viewholder.ActiveProductsParentViewHolder
import com.digitaloutlet.viewholder.OnItemClickListener
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso

class ActiveProductsParentAdapter(context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val mContext = context
    private val mParentCatLst = ArrayList<PublishedProductsBean>()
    private var mListener: OnNestedItemClickListener<PublishedProductsBean> ? = null
    private val ITEM_VIEW_GROUP: Int = 1
    private val ITEM_VIEW_CHILD: Int = 2

    fun setData(parentCatLst: ArrayList<PublishedProductsBean>) {
        mParentCatLst.clear()
        mParentCatLst.addAll(parentCatLst)
    }

    fun getData(): ArrayList<PublishedProductsBean> {
        return mParentCatLst
    }

    fun setItemClickListener(listener : OnNestedItemClickListener<PublishedProductsBean>) {
        mListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == ITEM_VIEW_GROUP) {
            val view = LayoutInflater.from(mContext).inflate(R.layout.item_dashboard_categories, parent, false)
            return ActiveProductsParentViewHolder(view)
        } else {
            val view = LayoutInflater.from(mContext).inflate(R.layout.item_dashboard_products, parent, false)
            return ActiveProductsChildViewHolder(view)
        }
    }

    override fun getItemCount(): Int {
        return mParentCatLst.size
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads)
        } else {
            val action = payloads.get(0) as String
            /*if (action.equals(ActiveProductsParentViewHolder.ACTION_TYPE_EXPAND_COLLAPSE)) {
                val obj = mParentCatLst.get(holder.adapterPosition)
                if (obj.isExpanded) {
                    obj.isExpanded = false
                    for (removeObj in obj.productsLst) {
                        for (tempCatObj in mParentCatLst) {
                            if (removeObj.productId == tempCatObj.productId) {
                                mParentCatLst.remove(tempCatObj)
                                break
                            }
                        }
                    }
                } else {
                    obj.isExpanded = true
                    mParentCatLst.addAll(holder.adapterPosition + 1, obj.productsLst)
                }
            }
            val productEntityObj = mParentCatLst.get(position)*/

        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val holderPosition: Int = holder.adapterPosition
        val productObj = mParentCatLst.get(holderPosition)

        if (productObj.isGroup) {
            val parentViewHolder = holder as ActiveProductsParentViewHolder
            mListener?.let {
                parentViewHolder.onBindData(holderPosition, productObj, it)
            }
            productObj.parentCatName?.let {
                val productsCount = productObj.productsLst.size ?: 0
                parentViewHolder.mTvParentCatName.text = it + " (" + productsCount + ")"
            }
        } else {
            val childViewHolder = holder as ActiveProductsChildViewHolder
            mListener?.let {
                childViewHolder.onBindData(holderPosition, productObj, it)
            }

            var quantity = Constants.EMPTY
            productObj.quantity?.let {
                quantity = Constants.SPACE + Constants.DASH + Constants.SPACE + it
            }
            var unit = Constants.EMPTY
            productObj.unit?.let {
                unit = Constants.SPACE + it
            }
            childViewHolder.mTvBrandName.text = productObj.brandName
            childViewHolder.mTvProductName.text = productObj.productName + quantity + unit
            productObj.price?.let {
                childViewHolder.mTvProductPrice.text = mContext.resources.getString(R.string.Rs) + it
            } ?: run {
                childViewHolder.mTvProductPrice.text = mContext.resources.getString(R.string.not_available)
            }


            productObj.image?.let {
                Picasso.with(mContext).load(ServiceHelper.PRODUCTS_BASE_URL + it)
                    //.resize(1500, 1500)
                    .resizeDimen(R.dimen.item_image_dimen_75dp, R.dimen.item_image_dimen_75dp)
                    //.centerInside()
                    //.fit()
                    //.placeholder(ContextCompat.getDrawable(mContext, R.drawable.no_img_128))
                    //.error(ContextCompat.getDrawable(mContext, R.drawable.no_img_128))
                    .into(childViewHolder.mIvProductImage, object : Callback {
                        override fun onSuccess() {

                        }
                        override fun onError() {

                        }
                    })
            }

        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (mParentCatLst.get(position).isGroup)
            ITEM_VIEW_GROUP
        else
            ITEM_VIEW_CHILD
    }
}