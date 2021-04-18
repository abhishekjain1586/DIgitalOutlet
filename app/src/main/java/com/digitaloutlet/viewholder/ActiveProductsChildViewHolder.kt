package com.digitaloutlet.viewholder

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.digitaloutlet.R
import com.digitaloutlet.adapter.OnNestedItemClickListener
import com.digitaloutlet.model.bean.PublishedProductsBean

class ActiveProductsChildViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

    var mTvBrandName: TextView = itemView.findViewById(R.id.tv_brand_name)
    var mTvProductName: TextView = itemView.findViewById(R.id.tv_product_name)
    var mTvProductPrice: TextView = itemView.findViewById(R.id.tv_product_price)
    var mIvProductImage: ImageView = itemView.findViewById(R.id.iv_product_image)

    var mIvEdit: ImageView = itemView.findViewById(R.id.iv_edit)
    var mIvDelete: ImageView = itemView.findViewById(R.id.iv_delete)

    lateinit var mProduct: PublishedProductsBean
    lateinit var mListener : OnNestedItemClickListener<PublishedProductsBean>
    var mPosition = 0

    companion object {
        const val ACTION_TYPE_EDIT: Int = 1
        const val ACTION_TYPE_DELETE: Int = 2
    }

    fun onBindData(position: Int, product : PublishedProductsBean, listener : OnNestedItemClickListener<PublishedProductsBean>) {
        mPosition = position
        mProduct = product
        mListener = listener

        mIvEdit.setOnClickListener(this)
        mIvDelete.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.iv_edit -> {
                mListener.onChildItemClick(adapterPosition, mProduct, ACTION_TYPE_EDIT)
            }

            R.id.iv_delete -> {
                mListener.onChildItemClick(adapterPosition, mProduct, ACTION_TYPE_DELETE)
            }
        }
    }
}