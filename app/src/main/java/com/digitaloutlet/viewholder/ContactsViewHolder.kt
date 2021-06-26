package com.digitaloutlet.viewholder

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.digitaloutlet.R
import com.digitaloutlet.model.bean.ContactsBean

class ContactsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

    private var mListener: OnItemClickListener<ContactsBean>? = null
    private var mPosition = -1
    private lateinit var mContactsBean: ContactsBean

    var mLayContacts: ConstraintLayout = itemView.findViewById(R.id.lay_contacts)
    var mTvName: TextView = itemView.findViewById(R.id.tv_name)
    var mTvPhoneNo: TextView = itemView.findViewById(R.id.tv_phoneno)
    var ivSelection: ImageView = itemView.findViewById(R.id.iv_tick)

    companion object {
        const val ACTION_TYPE_SELECTION: Int = 1
    }

    fun onBindData(contactsBean: ContactsBean, listener: OnItemClickListener<ContactsBean>) {
        mPosition = adapterPosition
        mListener = listener
        mContactsBean = contactsBean

        mLayContacts.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when(view?.id) {
            R.id.lay_contacts -> {
                mListener?.onItemClick(adapterPosition, mContactsBean, ACTION_TYPE_SELECTION)
            }
        }
    }
}