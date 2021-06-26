package com.digitaloutlet.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.digitaloutlet.R
import com.digitaloutlet.model.bean.ContactsBean
import com.digitaloutlet.viewholder.ContactsViewHolder
import com.digitaloutlet.viewholder.OnItemClickListener

class ContactsAdapter(context: Context) : RecyclerView.Adapter<ContactsViewHolder>() {

    private val mContext = context
    private val mContactLst = ArrayList<ContactsBean>()
    private var mListener : OnItemClickListener<ContactsBean>? = null

    fun setData(lst: ArrayList<ContactsBean>) {
        mContactLst.clear()
        mContactLst.addAll(lst)
    }

    fun getData(): ArrayList<ContactsBean> {
        return mContactLst
    }

    fun setItemClickListener(listener : OnItemClickListener<ContactsBean>) {
        mListener = listener
    }

    override fun getItemCount(): Int {
        return mContactLst.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactsViewHolder {
        var resource = R.layout.item_contacts
        val view : View = LayoutInflater.from(mContext).inflate(resource, parent, false)
        return ContactsViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ContactsViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads)
        } else {
            mContactLst.get(position).isSelected = payloads.get(0) as Boolean
            if (mContactLst.get(position).isSelected) {
                holder.ivSelection.setBackgroundResource(R.drawable.bg_radio_selected)
            } else {
                holder.ivSelection.setBackgroundResource(R.drawable.bg_radio_unselected)
            }
        }
    }

    override fun onBindViewHolder(holder: ContactsViewHolder, position: Int) {
        val contactsObj = mContactLst.get(position)

        mListener?.let {
            holder.onBindData(contactsObj, it)
        }

        holder.mTvName.text = contactsObj.name
        holder.mTvPhoneNo.text = contactsObj.phoneNumber
        if (contactsObj.isSelected) {
            holder.ivSelection.setBackgroundResource(R.drawable.bg_radio_selected)
        } else {
            holder.ivSelection.setBackgroundResource(R.drawable.bg_radio_unselected)
        }
    }
}