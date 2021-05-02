package com.digitaloutlet.view.activities

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.digitaloutlet.R
import com.digitaloutlet.adapter.ActiveProductsParentAdapter
import com.digitaloutlet.db.entities.ProductsEntity
import com.digitaloutlet.utils.Constants
import com.digitaloutlet.utils.DialogUtil
import com.digitaloutlet.view.base.BaseActivity
import com.digitaloutlet.viewmodel.SearchProductActivityViewModel
import kotlinx.android.synthetic.main.activity_search_product.*

class SearchProductActivity : BaseActivity() {

    private lateinit var mViewModel : SearchProductActivityViewModel
    private lateinit var linearLayoutManager : LinearLayoutManager
    private lateinit var mAdapter: ActiveProductsParentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_product)

        initView()
        initData()
    }

    private fun initView() {
        mViewModel = ViewModelProviders.of(this).get(SearchProductActivityViewModel::class.java)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = getString(R.string.title_search_product)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        mAdapter = ActiveProductsParentAdapter(this)
        linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        rvProducts.layoutManager = linearLayoutManager
        rvProducts.adapter = mAdapter

        svSearchProduct.queryHint = getString(R.string.hint_search_product)
        svSearchProduct.setIconifiedByDefault(false)
        svSearchProduct.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrBlank()) {
                    mViewModel.searchProduct(query)
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }

        })

        initObservers()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home -> {
                onBackPressed()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initObservers() {
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
                DialogUtil.showCommonActionDialog(this@SearchProductActivity, Constants.EMPTY, message, false, null)
            }
        })

        mViewModel.obsvGetSearchProducts().observe(this, object :
            Observer<ArrayList<ProductsEntity>> {
            override fun onChanged(productsLst: ArrayList<ProductsEntity>) {
                /*tvNoSearchProduct.visibility = View.GONE
                rvProducts.visibility = View.VISIBLE
                if (productsLst.isNullOrEmpty()) {
                    rvProducts.visibility = View.GONE
                    tvNoSearchProduct.visibility = View.VISIBLE
                    return
                }
                notifyAdapter(productsLst)*/
            }
        })
    }

    private fun initData() {

    }

    private fun notifyAdapter(lst: ArrayList<ProductsActivity>) {
        /*mAdapter.setData(lst)
        mAdapter.notifyDataSetChanged()*/
    }
}
