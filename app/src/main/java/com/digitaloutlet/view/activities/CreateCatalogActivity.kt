package com.digitaloutlet.view.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.digitaloutlet.R
import com.digitaloutlet.adapter.ParentCategoryAdapter
import com.digitaloutlet.model.response.ParentCategory
import com.digitaloutlet.utils.Constants
import com.digitaloutlet.utils.DOPrefs
import com.digitaloutlet.utils.DialogUtil
import com.digitaloutlet.view.base.BaseActivity
import com.digitaloutlet.viewholder.OnItemClickListener
import com.digitaloutlet.viewmodel.CreateCatalogActivityViewModel

class CreateCatalogActivity : BaseActivity(), View.OnClickListener,
    OnItemClickListener<ParentCategory> {

    private lateinit var mRvCategories: RecyclerView
    private lateinit var mBtnSkip: Button
    private lateinit var mBtnConfirm: Button
    private lateinit var mTvName: TextView

    private lateinit var mGridLayoutManager: GridLayoutManager
    private lateinit var mViewModel: CreateCatalogActivityViewModel
    private lateinit var adapter: ParentCategoryAdapter

    //private var mParentCategoryLst = ArrayList<ParentCategory>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_catalog)

        initView()
        initData()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)
        if (item.itemId == R.id.user_profile) {
            launchProfile()
        }
        return true
    }

    private fun initView() {
        mViewModel = ViewModelProviders.of(this).get(CreateCatalogActivityViewModel::class.java)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = getString(R.string.create_digital_menu_screen_title)

        mTvName = findViewById(R.id.tv_name)
        mRvCategories = findViewById(R.id.rv_parent_category)
        mBtnSkip = findViewById(R.id.btn_skip)
        mBtnConfirm = findViewById(R.id.btn_confirm)

        initListener()
    }

    private fun initListener() {
        mBtnSkip.setOnClickListener(this)
        mBtnConfirm.setOnClickListener(this)
    }

    private fun initData() {
        mTvName.text = getString(R.string.hello) + Constants.SPACE + DOPrefs.getMerchantId() + Constants.COMMA

        initAdapter()

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
                DialogUtil.showCommonActionDialog(this@CreateCatalogActivity, Constants.EMPTY, message, false, null)
            }
        })

        mViewModel.fetchParentCategoriesObserver().observe(this, object : Observer<ArrayList<ParentCategory>> {
            override fun onChanged(parentCatLst: ArrayList<ParentCategory>) {
                //mParentCategoryLst.clear()
                //mParentCategoryLst.addAll(parentCatLst)
                adapter.setData(parentCatLst)
                adapter.notifyDataSetChanged()
            }
        })

        mViewModel.observerProceedNextToSelectProducts().observe(this, object : Observer<ArrayList<ParentCategory>> {
            override fun onChanged(selectedParentCatLst: ArrayList<ParentCategory>) {
                launchProductSelectionScreen(selectedParentCatLst)
            }
        })
    }

    private fun initAdapter() {
        mGridLayoutManager = GridLayoutManager(this, 3);
        mRvCategories.layoutManager = mGridLayoutManager;

        adapter = ParentCategoryAdapter(this)
        adapter.setItemClickListener(this)
        adapter.setData(mViewModel.getParentCatLst())
        mRvCategories.adapter = adapter
    }

    override fun onClick(view: View?) {
        when(view?.id) {
            R.id.btn_skip -> {
                launchDashboard()
            }

            R.id.btn_confirm -> {
                mViewModel.proceedNextToSelectProducts()
            }
        }
    }

    override fun onItemClick(position: Int, parentCat: ParentCategory, type: Int) {
        adapter.notifyItemChanged(position, !parentCat.isSelected)
        mViewModel.updateParentCategoryStatus(parentCat)
    }

    private fun launchProductSelectionScreen(selectedCatLst: ArrayList<ParentCategory>) {
        val intent = Intent(this, ProductsActivity::class.java)
        intent.putParcelableArrayListExtra(Constants.INTENT_SELECTED_PRODUCT_CATEGORIES, selectedCatLst)
        startActivity(intent)
    }
}
