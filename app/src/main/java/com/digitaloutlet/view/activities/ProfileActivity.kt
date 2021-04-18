package com.digitaloutlet.view.activities

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.digitaloutlet.R
import com.digitaloutlet.adapter.ProfileAdapter
import com.digitaloutlet.model.bean.ProfileBean
import com.digitaloutlet.utils.Constants
import com.digitaloutlet.utils.DOPrefs
import com.digitaloutlet.utils.DialogUtil
import com.digitaloutlet.view.base.BaseActivity
import com.digitaloutlet.viewholder.OnItemClickListener
import com.digitaloutlet.viewmodel.ProfileActivityViewModel

class ProfileActivity : BaseActivity(), View.OnClickListener, OnItemClickListener<ProfileBean> {

    private lateinit var rvProfile: RecyclerView
    private lateinit var ivClose: ImageView

    private lateinit var mViewModel: ProfileActivityViewModel
    private lateinit var mLinearLayoutManager: LinearLayoutManager
    private var adapter: ProfileAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        initView()
        initData()
    }

    private fun initView() {
        mViewModel = ViewModelProviders.of(this).get(ProfileActivityViewModel::class.java)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false);
        ivClose = findViewById(R.id.iv_close)
        ivClose.setOnClickListener(this)

        rvProfile = findViewById(R.id.rv_profile)
    }

    private fun initData() {
        mViewModel.showLoader().observe(this, object : Observer<Boolean> {
            override fun onChanged(value: Boolean) {
                if (value) {
                    showLoader()
                } else {
                    dismissLoader()
                }
            }
        })

        mViewModel.showErrorDialog().observe(this, object : Observer<String> {
            override fun onChanged(message: String) {
                DialogUtil.showCommonActionDialog(this@ProfileActivity, Constants.EMPTY, message, false, null)
            }
        })

        mViewModel.getProfileMenuObserver().observe(this, object : Observer<ArrayList<ProfileBean>> {
            override fun onChanged(profileMenuLst: ArrayList<ProfileBean>) {
                initAdapter(profileMenuLst)
            }
        })

        mViewModel.logoutObserver().observe(this, object : Observer<Boolean> {
            override fun onChanged(value: Boolean) {
                if (value) {
                    DOPrefs.clearPref()
                    showLauncherScreen()
                }
            }
        })

        mViewModel.loadProfileMenu()
    }

    private fun initAdapter(profileMenuLst: ArrayList<ProfileBean>) {
        mLinearLayoutManager = LinearLayoutManager(this);
        rvProfile.layoutManager = mLinearLayoutManager;
        adapter = ProfileAdapter(this)
        adapter?.setItemClickListener(this)
        adapter?.setData(profileMenuLst)
        rvProfile.adapter = adapter
    }

    override fun onClick(view: View?) {
        when(view?.id) {
            R.id.iv_close -> {
                finish()
            }
        }
    }

    override fun onItemClick(position: Int, obj: ProfileBean, actionType: Int) {
        if (obj.value == ProfileBean.LOGOUT) {
            mViewModel.logout()
        }
    }

    private fun showLauncherScreen() {
        launchLauncher()
    }

}
