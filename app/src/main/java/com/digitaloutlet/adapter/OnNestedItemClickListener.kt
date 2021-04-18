package com.digitaloutlet.adapter

interface OnNestedItemClickListener<T> {

    fun onGroupItemClick(position: Int, parentObj: T, actionType: Int)
    fun onChildItemClick(position: Int, childObj: T, actionType: Int)

}