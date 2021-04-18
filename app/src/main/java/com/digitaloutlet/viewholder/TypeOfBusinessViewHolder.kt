package com.digitaloutlet.viewholder

import android.view.View
import android.widget.CompoundButton
import android.widget.RadioButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.digitaloutlet.R
import com.digitaloutlet.model.response.TypeOfShop

class TypeOfBusinessViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

    lateinit var mTypeOfShop: TypeOfShop
    lateinit var mListener : OnItemClickListener<TypeOfShop>
    private var mPosition: Int = 0

    var layoutTOB: ConstraintLayout = itemView.findViewById(R.id.lay_tob)
    var rbTob: RadioButton = itemView.findViewById(R.id.rb_tob)
    var tvTob: TextView = itemView.findViewById(R.id.tv_tob)

    fun bind(position: Int, typeOfShop : TypeOfShop, listener : OnItemClickListener<TypeOfShop>) {
        mPosition = position
        mTypeOfShop = typeOfShop
        mListener = listener

        layoutTOB.setOnClickListener(this)

        rbTob.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener {
            override fun onCheckedChanged(p0: CompoundButton?, checked: Boolean) {
                if (checked) {
                    mListener.onItemClick(mPosition, mTypeOfShop, 0)
                }
            }

        })
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.lay_tob -> {
                mListener.onItemClick(mPosition, mTypeOfShop, 0)
            }
        }
    }
}