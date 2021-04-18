package com.digitaloutlet.viewholder

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.digitaloutlet.R
import com.digitaloutlet.model.bean.ProfileBean

class ProfileViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {

    var layoutProfile: ConstraintLayout = view.findViewById(R.id.lay_profile)
    var ivIcon: ImageView = view.findViewById(R.id.iv_icon)
    var tvName: TextView = view.findViewById(R.id.tv_name)

    private lateinit var obj: ProfileBean
    private var mListener: OnItemClickListener<ProfileBean>? = null

    fun bind(position:Int, obj: ProfileBean, listener: OnItemClickListener<ProfileBean>?) {
        this.obj = obj
        mListener = listener
        layoutProfile.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.lay_profile -> {
                mListener?.onItemClick(adapterPosition, obj, 0)
            }
        }
    }
}