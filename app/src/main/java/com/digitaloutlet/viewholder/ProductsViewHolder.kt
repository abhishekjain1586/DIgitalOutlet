package com.digitaloutlet.viewholder

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.digitaloutlet.R
import com.digitaloutlet.db.entities.ProductsEntity

class ProductsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

    var mLayItemProduct: ConstraintLayout = itemView.findViewById(R.id.lay_item_product)
    var mTvBrandName: TextView = itemView.findViewById(R.id.tv_brand_name)
    var mTvProductName: TextView = itemView.findViewById(R.id.tv_product_name)
    var mTvSeletion: TextView = itemView.findViewById(R.id.tv_selection)
    var mIvProductImage: ImageView = itemView.findViewById(R.id.iv_product_image)

    var mPosition = 0

    lateinit var mProduct: ProductsEntity
    lateinit var mListener : OnItemClickListener<ProductsEntity>

    fun bind(position: Int, product : ProductsEntity, listener : OnItemClickListener<ProductsEntity>) {
        mPosition = position
        mProduct = product
        mListener = listener
        mLayItemProduct.setOnClickListener(null)
        if (product.productStateForMerchant != ProductsEntity.PRODUCT_STATE_PUBLISHED) {
            mLayItemProduct.setOnClickListener(this)
        }
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.lay_item_product -> {
                mListener.onItemClick(mPosition, mProduct, 0)
            }
        }
    }
}