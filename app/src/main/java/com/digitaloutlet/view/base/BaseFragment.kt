package com.digitaloutlet.view.base

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment

abstract class BaseFragment : Fragment() {

    lateinit var baseView: View

    abstract fun initViews()
    abstract fun initListeners()
    //abstract fun initViewModel()
    abstract fun initData()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initViews()
        initListeners()
        //initViewModel()
        initData()
    }
}