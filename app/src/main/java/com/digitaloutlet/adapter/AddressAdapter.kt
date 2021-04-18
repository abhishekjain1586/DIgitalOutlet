package com.digitaloutlet.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.digitaloutlet.R
import com.digitaloutlet.viewholder.AddressViewHolder

class AddressAdapter(context: Context) : ArrayAdapter<String>(context, 0) {

    private val mAddressLst = ArrayList<String>()
    private var mLayoutInflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private var mListener : AddressViewHolder.OnAddressItemClickListener? = null

    fun setData(addressLst: ArrayList<String>) {
        mAddressLst.clear()
        mAddressLst.addAll(addressLst)
    }

    fun setItemClickListener(listener : AddressViewHolder.OnAddressItemClickListener?) {
        mListener = listener
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var viewHolder: AddressViewHolder
        var view: View? = convertView

        if (view == null) {
            viewHolder = AddressViewHolder()

            val view = mLayoutInflater.inflate(R.layout.item_address, parent, false);
            viewHolder.layoutAddress = view.findViewById(R.id.lay_address)
            viewHolder.tvAddress = view.findViewById(R.id.tv_address)

            view.tag = viewHolder
        } else {
            viewHolder = view.tag as AddressViewHolder
        }

        val strAddress = mAddressLst.get(position)

        //viewHolder.bindData(strAddress, mListener)
        viewHolder.tvAddress.text = strAddress

        return view!!
    }

    override fun getItem(position: Int): String {
        return mAddressLst.get(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return mAddressLst.size
    }
}