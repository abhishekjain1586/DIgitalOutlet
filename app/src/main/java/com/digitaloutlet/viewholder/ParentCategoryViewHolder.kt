package com.digitaloutlet.viewholder

import android.view.View
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.digitaloutlet.R
import com.digitaloutlet.model.response.ParentCategory

class ParentCategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

    var mLayItemParentCat: ConstraintLayout = itemView.findViewById(R.id.lay_item_parent_cat)
    var mLaySelectedParCat: CardView = itemView.findViewById(R.id.lay_cat_selected)
    var mIvParentCat: ImageView = itemView.findViewById(R.id.iv_parent_cat)
    var mTvParentCatName: TextView = itemView.findViewById(R.id.tv_parent_cat_name)
    var mPosition = 0

    lateinit var mParentCat: ParentCategory
    lateinit var mListener : OnItemClickListener<ParentCategory>

    fun bind(position: Int, parentCat : ParentCategory, listener : OnItemClickListener<ParentCategory>) {
        mPosition = position
        mParentCat = parentCat
        mListener = listener

        mLayItemParentCat.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.lay_item_parent_cat -> {
                mListener.onItemClick(mPosition, mParentCat, 0)
            }
        }
    }
}