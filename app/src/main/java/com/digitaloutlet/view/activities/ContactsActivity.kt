package com.digitaloutlet.view.activities

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.digitaloutlet.R
import com.digitaloutlet.adapter.ContactsAdapter
import com.digitaloutlet.model.bean.ContactsBean
import com.digitaloutlet.utils.view.ContactsUtility
import com.digitaloutlet.view.base.BaseActivity
import com.digitaloutlet.viewholder.OnItemClickListener
import com.digitaloutlet.viewmodel.SummaryActivityViewModel

class ContactsActivity : BaseActivity(), View.OnClickListener,OnItemClickListener<ContactsBean> {

    private lateinit var mRvContacts: RecyclerView
    private lateinit var mBtnSend: Button
    private lateinit var mViewModel: SummaryActivityViewModel
    private var mAdapter: ContactsAdapter? = null
    private lateinit var linearLayoutManager : LinearLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contacts)

        initView()
        initData()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initView() {
        mViewModel = ViewModelProviders.of(this).get(SummaryActivityViewModel::class.java)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = getString(R.string.title_contacts)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        mBtnSend = findViewById(R.id.btn_send)
        mRvContacts = findViewById(R.id.rv_contacts)

        linearLayoutManager = LinearLayoutManager(this);
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        mRvContacts.layoutManager = linearLayoutManager;

        initListener()
    }

    private fun initListener() {
        mBtnSend.setOnClickListener(this)
    }

    private fun initData() {
        val contactsLst = ContactsUtility.getInstance(this).getContacts()
        if (!contactsLst.isNullOrEmpty()) {
            initAdapter(contactsLst)
        }
    }

    private fun initAdapter(contactsLst: ArrayList<ContactsBean>) {
        mAdapter = ContactsAdapter(this)
        mAdapter?.setData(contactsLst)
        mAdapter?.setItemClickListener(this)
        mRvContacts.adapter = mAdapter
    }

    override fun onItemClick(position: Int, obj: ContactsBean, actionType: Int) {
        mAdapter?.notifyItemChanged(position, !obj.isSelected)
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.btn_send -> {
                sendSms()
            }
        }
    }

    private fun sendSms() {
        val lst = getContactLst()
        if (lst.isNullOrEmpty()) {
            showToastMessage(getString(R.string.error_select_contacts))
            return
        }

        sendSms(lst)
    }

    private fun sendSms(lst: List<ContactsBean>) {

    }

    private fun getContactLst(): List<ContactsBean> {
        val lst = ArrayList<ContactsBean>()
        for (obj in mAdapter?.getData()!!) {
            if (obj.isSelected) {
                lst.add(obj)
            }
        }
        return lst
    }
}