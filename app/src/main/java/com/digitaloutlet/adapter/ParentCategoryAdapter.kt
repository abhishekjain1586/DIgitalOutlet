package com.digitaloutlet.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.digitaloutlet.R
import com.digitaloutlet.model.response.ParentCategory
import com.digitaloutlet.viewholder.OnItemClickListener
import com.digitaloutlet.viewholder.ParentCategoryViewHolder
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso

class ParentCategoryAdapter(context: Context) : RecyclerView.Adapter<ParentCategoryViewHolder>() {

    private val mContext = context
    private val mParentCatLst = ArrayList<ParentCategory>()
    private var mListener : OnItemClickListener<ParentCategory>? = null

    fun setData(tosLst: ArrayList<ParentCategory>) {
        mParentCatLst.clear()
        mParentCatLst.addAll(tosLst)
    }

    fun setItemClickListener(listener : OnItemClickListener<ParentCategory>) {
        mListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParentCategoryViewHolder {
        var resource = R.layout.item_parent_category
        val view : View = LayoutInflater.from(mContext).inflate(resource, parent, false)
        return ParentCategoryViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mParentCatLst.size
    }

    override fun onBindViewHolder(
        holder: ParentCategoryViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads)
        } else {
            mParentCatLst.get(position).isSelected = payloads.get(0) as Boolean
            if (mParentCatLst.get(position).isSelected) {
                holder.mLaySelectedParCat.visibility = View.VISIBLE
            } else {
                holder.mLaySelectedParCat.visibility = View.GONE
            }
        }
    }
    override fun onBindViewHolder(holder: ParentCategoryViewHolder, position: Int) {
        val parentCategoryObj = mParentCatLst.get(position)

        mListener?.let {
            holder.bind(position, parentCategoryObj, it)
        }

        holder.mTvParentCatName.text = parentCategoryObj.parent_cat

        parentCategoryObj.image?.let {
            //val targetSize = Constants.IMAGE_GRID_SIZE_IN_PIXELS_300
            Picasso.with(mContext).load(it)
                //.resize(1500, 1500)
                .resizeDimen(R.dimen.grid_image_dimen_93dp, R.dimen.grid_image_dimen_93dp)
                //.centerInside()
                .into(holder.mIvParentCat, object : Callback {
                    override fun onSuccess() {
                        if (parentCategoryObj.isSelected) {
                            holder.mLaySelectedParCat.visibility = View.VISIBLE
                        } else {
                            holder.mLaySelectedParCat.visibility = View.GONE
                        }
                    }
                    override fun onError() {
                        if (parentCategoryObj.isSelected) {
                            holder.mLaySelectedParCat.visibility = View.VISIBLE
                        } else {
                            holder.mLaySelectedParCat.visibility = View.GONE
                        }
                    }
                });
        }
    }
}