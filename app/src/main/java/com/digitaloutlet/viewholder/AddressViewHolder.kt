package com.digitaloutlet.viewholder

import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.digitaloutlet.R

class AddressViewHolder : View.OnClickListener {

    lateinit var layoutAddress: ConstraintLayout
    lateinit var tvAddress: TextView

    private lateinit var mAddress: String
    private var mListener: OnAddressItemClickListener? = null

    interface OnAddressItemClickListener {
        fun onItemClicked(address: String)
    }

    fun bindData(address:String, listener: OnAddressItemClickListener?) {
        mAddress = address
        mListener = listener
        layoutAddress.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.lay_address -> {
                mListener?.onItemClicked(mAddress)
            }
        }
    }

}