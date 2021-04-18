package com.digitaloutlet.view.activities

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.digitaloutlet.R
import com.digitaloutlet.adapter.SummaryAdapter
import com.digitaloutlet.db.entities.ProductsEntity
import com.digitaloutlet.utils.Constants
import com.digitaloutlet.utils.DialogUtil
import com.digitaloutlet.view.base.BaseActivity
import com.digitaloutlet.viewmodel.SummaryActivityViewModel

class SummaryActivity : BaseActivity(), View.OnClickListener {

    private lateinit var mRvProducts: RecyclerView
    private lateinit var mBtnPublish: Button
    private lateinit var mBtnSaveAsDraft: Button

    private lateinit var mViewModel: SummaryActivityViewModel
    private var mAdapter: SummaryAdapter? = null
    private lateinit var linearLayoutManager : LinearLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_summary)

        initView()
        initData()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home -> {
                launchDashboard()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initView() {
        mViewModel = ViewModelProviders.of(this).get(SummaryActivityViewModel::class.java)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = getString(R.string.summary)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        mRvProducts = findViewById(R.id.rv_products)
        mBtnPublish = findViewById(R.id.btn_publish)
        mBtnSaveAsDraft = findViewById(R.id.btn_save_as_draft)

        linearLayoutManager = LinearLayoutManager(this);
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        mRvProducts.layoutManager = linearLayoutManager;

        initListener()
    }

    private fun initListener() {
        mBtnPublish.setOnClickListener(this)
        mBtnSaveAsDraft.setOnClickListener(this)
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
                DialogUtil.showCommonActionDialog(this@SummaryActivity, Constants.EMPTY, message, false,
                    object : DialogUtil.DailogCallback {
                        override fun onEventOK() {
                            finish()
                        }

                        override fun onEventCancel() {

                        }
                    })
            }
        })

        mViewModel.getProductsObserver().observe(this, object :
            Observer<ArrayList<ProductsEntity>> {
            override fun onChanged(productsLst: ArrayList<ProductsEntity>) {
                initAdapter(productsLst)
            }
        })
        mViewModel.getAllProductsToPublish()

        mViewModel.publishProductsObserver().observe(this, object :
            Observer<String> {
            override fun onChanged(message: String) {
                launchDashboard()
            }
        })
    }

    private fun initAdapter(productsLst: ArrayList<ProductsEntity>) {
        var adapter = mAdapter
        if (adapter == null) {
            adapter = SummaryAdapter(this)
            mAdapter = adapter
            adapter.setData(productsLst)
            mRvProducts.adapter = mAdapter
        } else {
            adapter.setData(productsLst)
            adapter.notifyDataSetChanged()
        }
    }

    override fun onClick(view: View?) {
        when(view?.id) {
            R.id.btn_publish -> {
                mViewModel.publishProducts()

            }
            R.id.btn_save_as_draft -> {
                launchDashboard()
            }
        }
    }
}
