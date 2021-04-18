package com.digitaloutlet.viewholder

interface OnItemClickListener<T> {

    fun onItemClick(position: Int, obj: T, actionType: Int)
}