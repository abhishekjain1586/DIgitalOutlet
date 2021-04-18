package com.digitaloutlet.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.digitaloutlet.R
import com.digitaloutlet.model.response.ParentCategory
import com.digitaloutlet.viewholder.SelectedParentCatViewHolder

class SelectedParentCatAdapter(context: Context) : ArrayAdapter<ParentCategory>(context, 0) {

    private val mSelecteParentCatLst = ArrayList<ParentCategory>()
    private var mLayoutInflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    fun setData(addressLst: ArrayList<ParentCategory>) {
        mSelecteParentCatLst.clear()
        mSelecteParentCatLst.addAll(addressLst)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getCustomView(position, convertView, parent)
    }
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getCustomView(position, convertView, parent)
    }

    private fun getCustomView(position: Int, convertView: View?, parent: ViewGroup): View {
        var viewHolder: SelectedParentCatViewHolder
        var view: View? = convertView

        if (view == null) {
            viewHolder = SelectedParentCatViewHolder()

            view = mLayoutInflater.inflate(R.layout.item_selected_parent_category, parent, false);
            viewHolder.tvCatName = view.findViewById(R.id.tv_parent_cat_name)

            view.tag = viewHolder
        } else {
            viewHolder = view.tag as SelectedParentCatViewHolder
        }

        val obj = mSelecteParentCatLst.get(position)

        viewHolder.tvCatName.text = obj.parent_cat

        return view!!
    }

    override fun getItem(position: Int): ParentCategory {
        return mSelecteParentCatLst.get(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return mSelecteParentCatLst.size
    }
}