package com.digitaloutlet.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.digitaloutlet.R
import com.digitaloutlet.model.response.TypeOfShop
import com.digitaloutlet.viewholder.OnItemClickListener
import com.digitaloutlet.viewholder.TypeOfBusinessViewHolder

class TypeOfBusinessAdapter(context: Context) : RecyclerView.Adapter<TypeOfBusinessViewHolder>() {

    private val mContext = context
    private val mTosLst = ArrayList<TypeOfShop>()
    private var mListener : OnItemClickListener<TypeOfShop>? = null

    fun setData(tosLst: ArrayList<TypeOfShop>) {
        mTosLst.clear()
        mTosLst.addAll(tosLst)
    }

    fun setItemClickListener(listener : OnItemClickListener<TypeOfShop>) {
        mListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TypeOfBusinessViewHolder {
        var resource = R.layout.item_type_of_business
        val view : View = LayoutInflater.from(mContext).inflate(resource, parent, false)
        return TypeOfBusinessViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mTosLst.size
    }

    override fun onBindViewHolder(holder: TypeOfBusinessViewHolder, position: Int) {
        val tosObj = mTosLst.get(position)
        holder.tvTob.text = tosObj.value

        holder.rbTob.isChecked = tosObj.isSelected
        mListener?.let {
            holder.bind(position, tosObj, it)
        }
    }
}