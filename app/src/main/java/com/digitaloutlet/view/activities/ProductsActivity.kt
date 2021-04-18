package com.digitaloutlet.view.activities

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProviders
import com.digitaloutlet.R
import com.digitaloutlet.model.response.ParentCategory
import com.digitaloutlet.utils.Constants
import com.digitaloutlet.view.base.BaseActivity
import com.digitaloutlet.view.fragments.ProductsPriceFragment
import com.digitaloutlet.view.fragments.ProductsSelectionFragment
import com.digitaloutlet.viewmodel.ProductsActivityViewModel

class ProductsActivity : BaseActivity()/*, View.OnClickListener, OnItemClickListener<Products>*/ {

    //private lateinit var mSpParentCat: Spinner

    private lateinit var mViewModel: ProductsActivityViewModel
    private var mSelectedparentCatLst = ArrayList<ParentCategory>()
    var currentParentCatPosition: Int = 0
    private lateinit var fragmentManager: FragmentManager
    var isEditPublishedProduct = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_products)

        initView()
        initData()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home -> {
                onBackPressed()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initView() {
        mViewModel = ViewModelProviders.of(this).get(ProductsActivityViewModel::class.java)

        mSelectedparentCatLst = intent.getParcelableArrayListExtra(Constants.INTENT_SELECTED_PRODUCT_CATEGORIES)
        mViewModel.setSelectedParentCatLst(mSelectedparentCatLst)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        //mSpParentCat = findViewById(R.id.sp_parent_cat)

        fragmentManager = supportFragmentManager

        initListener()
    }

    private fun initListener() {
        /*mSpParentCat.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {}

            override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, position: Int, p3: Long) {
                val parentCatObj = adapterView?.getItemAtPosition(position) as ParentCategory
                mViewModel.getProductsByParentCatId(parentCatObj.id!!, 0)
            }
        }*/
    }

    private fun initData() {
        //mSelectedparentCatLst = intent.getParcelableArrayListExtra(Constants.INTENT_SELECTED_PRODUCT_CATEGORIES)

        supportActionBar?.title = getCurrentParentCategory().parent_cat
        /*mViewModel.getParentCategoryTitle().observe(this, object : Observer<String> {
            override fun onChanged(title: String) {
                supportActionBar?.title = title ?: Constants.EMPTY
            }
        })*/

        /*val parentCatAdapter = SelectedParentCatAdapter(this)
        parentCatAdapter.setData(mSelectedparentCatLst)*/
        //mViewModel.setSelectedParentCatLst(mSelectedparentCatLst)
        //parentCatAdapter.setDropDownViewResource(R.layout.item_selected_parent_category)
        //mSpParentCat.adapter = parentCatAdapter

        var isSearch: Boolean
        isEditPublishedProduct = intent.getBooleanExtra(Constants.INTENT_EDIT_PUBLISHED_PRODUCT, false)
        if (!isEditPublishedProduct) {
            addFragment(ProductsSelectionFragment(), null)
            isSearch = true
        } else {
            val bundle = Bundle()
            bundle.putInt(Constants.INTENT_PRODUCT_ID, intent.getIntExtra(Constants.INTENT_PRODUCT_ID, 0))
            addFragment(ProductsPriceFragment(), bundle)
            isSearch = false
        }
        showSearchView(isSearch)
    }

    fun getCurrentParentCategory(): ParentCategory {
        return mSelectedparentCatLst.get(currentParentCatPosition)
    }

    fun hasNextCategory(): Boolean {
        if (currentParentCatPosition < mSelectedparentCatLst.size - 1) {
            return true
        }
        return false
    }

    fun incrementParentCatPosition() {
        currentParentCatPosition++
        supportActionBar?.title = getCurrentParentCategory().parent_cat
    }

    fun showSearchView(isEditPublishedProduct: Boolean) {

    }

    fun clearAllFragmentsInclusive(backstackTag: String?) {
        fragmentManager.popBackStack(backstackTag, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        /*for (i in 0..fragmentManager.backStackEntryCount-1) {
            fragmentManager.popBackStackImmediate()
        }*/
    }

    fun addFragment(fragment: Fragment, args: Bundle?) {
        val fragmentTransaction = fragmentManager.beginTransaction()
        args?.let {
            fragment.arguments = it
        }
        fragmentTransaction.replace(R.id.products_container, fragment, fragment::class.java.simpleName)
        fragmentTransaction.addToBackStack(fragment::class.java.simpleName)
        fragmentTransaction.commit()
    }

    override fun onBackPressed() {
        if (fragmentManager.backStackEntryCount > 1) {
            super.onBackPressed()
        } else {
            finish()
        }
    }
}
