package com.digitaloutlet.viewholder

import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.digitaloutlet.R
import com.digitaloutlet.adapter.OnNestedItemClickListener
import com.digitaloutlet.model.bean.PublishedProductsBean

class ActiveProductsParentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

    private var mListener: OnNestedItemClickListener<PublishedProductsBean>? = null
    private var mPosition = -1
    private lateinit var mProductEntity: PublishedProductsBean

    var cbSelection: CheckBox = itemView.findViewById(R.id.cb_tick)
    var mTvParentCatName: TextView = itemView.findViewById(R.id.tv_parent_cat_name)
    var ivArrow: ImageView = itemView.findViewById(R.id.iv_arrow)

    companion object {
        const val ACTION_TYPE_EXPAND_COLLAPSE: Int = 1
    }

    fun onBindData(position: Int, productEntity: PublishedProductsBean, listener: OnNestedItemClickListener<PublishedProductsBean>) {
        mPosition = position
        mListener = listener
        mProductEntity = productEntity

        mTvParentCatName.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when(view?.id) {
            R.id.tv_parent_cat_name -> {
                mListener?.onGroupItemClick(adapterPosition, mProductEntity,
                    Companion.ACTION_TYPE_EXPAND_COLLAPSE
                )
            }
        }
    }

}