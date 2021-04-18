package com.digitaloutlet.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.digitaloutlet.R
import com.digitaloutlet.model.bean.ProfileBean
import com.digitaloutlet.viewholder.OnItemClickListener
import com.digitaloutlet.viewholder.ProfileViewHolder

class ProfileAdapter(context: Context) : RecyclerView.Adapter<ProfileViewHolder>() {

    private val mContext = context
    private val mProfileLst = ArrayList<ProfileBean>()
    private var mListener: OnItemClickListener<ProfileBean>? = null

    fun setData(tosLst: ArrayList<ProfileBean>) {
        mProfileLst.clear()
        mProfileLst.addAll(tosLst)
    }

    fun setItemClickListener(listener : OnItemClickListener<ProfileBean>) {
        mListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileViewHolder {
        var view: View
        var resource: Int = R.layout.item_profile
        view = LayoutInflater.from(mContext).inflate(resource, parent, false)
        return ProfileViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mProfileLst.size
    }

    override fun onBindViewHolder(holder: ProfileViewHolder, position: Int) {
        val profileObj = mProfileLst.get(position)

        mListener?.let {
            holder.bind(holder.adapterPosition, profileObj, it)
        }

        holder.ivIcon.setImageDrawable(ContextCompat.getDrawable(mContext, profileObj.image!!))
        holder.tvName.setText(profileObj.name)
    }
}