package com.digitaloutlet.view.activities

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProviders
import com.digitaloutlet.R
import com.digitaloutlet.utils.Constants
import com.digitaloutlet.view.base.BaseActivity
import com.digitaloutlet.view.enums.ProductSelectionState
import com.digitaloutlet.view.fragments.ProductsPriceFragment
import com.digitaloutlet.view.fragments.ProductsSelectionFragment
import com.digitaloutlet.viewmodel.ProductsActivityViewModel

class ProductsActivity : BaseActivity()/*, View.OnClickListener, OnItemClickListener<Products>*/ {

    private lateinit var mViewModel: ProductsActivityViewModel
    //private var mSelectedparentCatLst = ArrayList<ParentCategory>()
    //var currentParentCatPosition: Int = 0
    private lateinit var fragmentManager: FragmentManager
    //var isEditPublishedProduct = false

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

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        fragmentManager = supportFragmentManager

        initListener()
    }

    private fun initListener() {

    }

    private fun initData() {
        var productId: Int = -1
        var enumProductState = ProductSelectionState.ADD_NEW
        if (intent.hasExtra(Constants.INTENT_PRODUCT_SELECTION_STATE)) {
            enumProductState = intent.getSerializableExtra(Constants.INTENT_PRODUCT_SELECTION_STATE) as ProductSelectionState
        }
        mViewModel.setProductSelectionState(enumProductState)
        mViewModel.setCategoryLst(intent.getParcelableArrayListExtra(Constants.INTENT_SELECTED_PRODUCT_CATEGORIES))

        var isAddNewProducts = false;
        if (enumProductState == ProductSelectionState.ADD_NEW) {
            isAddNewProducts = true;
        } else if (enumProductState == ProductSelectionState.EDIT_PRODUCT) {
            productId = intent.getIntExtra(Constants.INTENT_PRODUCT_ID, 0)
        }

        supportActionBar?.title = mViewModel.getCurrentCategory()?.parent_cat

        if (isAddNewProducts) {
            addFragment(ProductsSelectionFragment())
        } else {
            val bundle = Bundle()
            if (productId != -1) {
                bundle.putInt(Constants.INTENT_PRODUCT_ID, productId)
            }
            addFragment(ProductsPriceFragment(), bundle)
        }

        /*var isSearch: Boolean
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
        showSearchView(isSearch)*/
    }

    /*fun getCurrentParentCategory(): ParentCategory {
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

    }*/

    fun clearAllFragmentsInclusive(backstackTag: String?) {
        fragmentManager.popBackStack(backstackTag, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        /*for (i in 0..fragmentManager.backStackEntryCount-1) {
            fragmentManager.popBackStackImmediate()
        }*/
    }

    fun addFragment(fragment: Fragment, args: Bundle? = null) {
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
