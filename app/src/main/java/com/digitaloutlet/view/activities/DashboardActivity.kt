package com.digitaloutlet.view.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.Group
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.digitaloutlet.R
import com.digitaloutlet.adapter.ActiveProductsParentAdapter
import com.digitaloutlet.adapter.OnNestedItemClickListener
import com.digitaloutlet.db.entities.ProductsEntity
import com.digitaloutlet.model.bean.PublishedProductsBean
import com.digitaloutlet.model.response.ParentCategory
import com.digitaloutlet.utils.Constants
import com.digitaloutlet.utils.DOPrefs
import com.digitaloutlet.utils.DialogUtil
import com.digitaloutlet.view.base.BaseActivity
import com.digitaloutlet.view.dialogfragments.AddCategoryProductSelectionDialogFragment
import com.digitaloutlet.view.enums.ProductSelectionState
import com.digitaloutlet.viewholder.ActiveProductsChildViewHolder
import com.digitaloutlet.viewholder.ActiveProductsParentViewHolder.Companion.ACTION_TYPE_EXPAND_COLLAPSE
import com.digitaloutlet.viewmodel.DashboardActivityViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

class DashboardActivity : BaseActivity(), View.OnClickListener,
    OnNestedItemClickListener<PublishedProductsBean> {

    private lateinit var tvDraftMsg: TextView
    private lateinit var fabAdd: FloatingActionButton
    private lateinit var groupDraft: Group
    private lateinit var mRvProducts: RecyclerView
    private lateinit var tvYes: TextView

    private lateinit var mViewModel: DashboardActivityViewModel
    private lateinit var linearLayoutManager : LinearLayoutManager
    private var adapter: ActiveProductsParentAdapter? = null
    private var childObjPositionToDelete: Int? = null
    private var childObjToDelete: PublishedProductsBean? = null
    private var childObjToEdit: PublishedProductsBean? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        initView()
        initData()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.user_profile) {
            launchProfile()
            return true
        } else if (item.itemId == R.id.share_menu) {
            share()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu?.let {
            it.findItem(R.id.share_menu).isVisible = mViewModel.hasPublishedProducts()
            return true //super.onPrepareOptionsMenu(menu)
        }
        return super.onPrepareOptionsMenu(menu)
    }

    private fun initView() {
        mViewModel = ViewModelProviders.of(this).get(DashboardActivityViewModel::class.java)

        tvDraftMsg = findViewById(R.id.tv_draft_msg)
        groupDraft = findViewById(R.id.group)
        fabAdd = findViewById(R.id.fab_add)
        mRvProducts = findViewById(R.id.rv_products)
        tvYes = findViewById(R.id.btn_yes);

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = getString(R.string.digiworld)

        linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        mRvProducts.layoutManager = linearLayoutManager


//        mBtnPublish = findViewById(R.id.btn_publish)

        initListener()
    }

    private fun initListener() {
        fabAdd.setOnClickListener(this)
        tvYes.setOnClickListener(this)
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
                DialogUtil.showCommonActionDialog(this@DashboardActivity, Constants.EMPTY, message, false, null)
            }
        })

        mViewModel.getProductsOnMsisdnObserver().observe(this, object : Observer<Boolean> {
            override fun onChanged(value: Boolean) {
                if (!value) {
                    DialogUtil.showCommonActionDialog(this@DashboardActivity,
                        Constants.EMPTY, resources.getString(R.string.error_fetching_products_on_msisdn),
                        false, object : DialogUtil.DailogCallback {
                            override fun onEventOK() {
                                finish()
                            }

                            override fun onEventCancel() {}
                        })
                    return
                }
                DOPrefs.updateMerchantMenu(false)
            }
        })

        mViewModel.getUnpublishedProducts().observe(this, object :
            Observer<ArrayList<ProductsEntity>> {
            override fun onChanged(productsLst: ArrayList<ProductsEntity>) {
                if (!productsLst.isNullOrEmpty()) {
                    groupDraft.visibility = View.VISIBLE
                    tvDraftMsg.text = String.format(getString(R.string.draft_msg, productsLst.size))
                } else {
                    groupDraft.visibility = View.GONE
                }
            }
        })

        mViewModel.getPublishedCategoriesObserver().observe(this, object :
            Observer<ArrayList<PublishedProductsBean>> {
            override fun onChanged(categoriesLst: ArrayList<PublishedProductsBean>) {
                invalidateOptionsMenu()
                initAdapter(categoriesLst)
            }
        })

        mViewModel.getDraftCategories().observe(this, object :
            Observer<ArrayList<ParentCategory>> {
            override fun onChanged(categoriesLst: ArrayList<ParentCategory>) {
                launchProductSelectionScreen(categoriesLst, true)
            }
        })

        mViewModel.deleteProductObserver().observe(this, object : Observer<Boolean> {
            override fun onChanged(value: Boolean) {
                childObjPositionToDelete?.let {
                    it1 -> childObjToDelete?.let {
                    it2 -> deleteProduct(it1, it2)
                    }
                }
            }
        })

        mViewModel.editProductObserver().observe(this, object : Observer<ArrayList<ParentCategory>> {
            override fun onChanged(parentCatLst: ArrayList<ParentCategory>) {
                launchProductSelectionScreen(parentCatLst)
            }
        })

        if (DOPrefs.toUpdateMerchantMenu()) {
            mViewModel.fetchProductsOnMsisdn(DOPrefs.getMSISDN())
        }
    }

    private fun initAdapter(categoriesLst: ArrayList<PublishedProductsBean>) {
        var tempAdapter = adapter
        if (tempAdapter == null) {
            tempAdapter = ActiveProductsParentAdapter(this)
            adapter = tempAdapter
        }
        tempAdapter.setData(categoriesLst)
        mRvProducts.adapter = adapter
        tempAdapter.setItemClickListener(this)
    }

    override fun onNewIntent(intent: Intent?) {
        Log.d("DashboardActivity", "onNewIntent")
        super.onNewIntent(intent)
    }

    override fun onClick(view: View?) {
        when(view?.id) {
            R.id.btn_yes -> {
                mViewModel.fetchDraftCategories()
            }

            R.id.fab_add -> {
                openAddNewProductsDialog()
            }
        }
    }

    private fun openAddNewProductsDialog() {
        val addNewDialogFragment = AddCategoryProductSelectionDialogFragment()
        addNewDialogFragment.show(
            supportFragmentManager,
            addNewDialogFragment.tag
        )
    }

    override fun onGroupItemClick(position: Int, parentObj: PublishedProductsBean, actionType: Int) {
        var adpt = adapter!!
        when(actionType) {
            ACTION_TYPE_EXPAND_COLLAPSE -> {
                val tempLst = adpt.getData()
                if (parentObj.isExpanded) {
                    parentObj.isExpanded = false
                    val startIndex = position + 1
                    val endIndex = startIndex + parentObj.productsLst.size
                    tempLst.subList(startIndex, endIndex).clear()
                    adpt.notifyItemRangeRemoved(startIndex, parentObj.productsLst.size)
                    //adapter?.notifyItemRangeChanged(position + 1, parentObj.productsLst?.size!!, ACTION_TYPE_EXPAND_COLLAPSE)
                } else {
                    parentObj.isExpanded = true
                    val startIndex = position + 1
                    tempLst.addAll(startIndex, tempLst.get(position).productsLst)
                    adpt.notifyItemRangeInserted(startIndex, parentObj.productsLst.size)
                    //adapter?.notifyItemRangeChanged(position + 1, parentObj.productsLst?.size!!, ACTION_TYPE_EXPAND_COLLAPSE)
                }
            }
        }
    }

    override fun onChildItemClick(position: Int, childObj: PublishedProductsBean, actionType: Int) {
        when(actionType) {
            ActiveProductsChildViewHolder.ACTION_TYPE_EDIT -> {
                childObjToEdit = childObj
                mViewModel.editProduct(childObj)
            }

            ActiveProductsChildViewHolder.ACTION_TYPE_DELETE -> {
                DialogUtil.showCommonActionDialog(this, Constants.EMPTY,
                    getString(R.string.confirm_delete_product,
                        childObj.productName!!
                                + Constants.SPACE + Constants.DASH + Constants.SPACE
                                + childObj.quantity + Constants.SPACE + childObj.unit),
                    resources.getString(R.string.yes),
                    resources.getString(R.string.no)
                    , true, object : DialogUtil.DailogCallback {
                        override fun onEventOK() {
                            childObjPositionToDelete = position
                            childObjToDelete = childObj
                            mViewModel.deleteProduct(childObj)
                        }

                        override fun onEventCancel() {}
                    })
            }
        }
    }

    private fun deleteProduct(position: Int, childObj: PublishedProductsBean) {
        var groupPos: Int = -1
        var isInnerListEmpty = false
        adapter!!.getData().let {
            for (i in 0 until it.size) {
                var updateGroup = false
                if (it.get(i).parentCatId.equals(childObj.parentCatId) && it.get(i).isGroup) {
                    groupPos = i
                    if (it.get(i).productsLst.size <= 1) {
                        updateGroup = true
                        isInnerListEmpty = true
                        it.get(i).productsLst.clear()
                    } else {
                        for (obj in it.get(i).productsLst) {
                            if (obj.productId == childObj.productId) {
                                it.get(i).productsLst.remove(obj)
                                updateGroup = true
                                break
                            }
                        }
                    }
                    if (updateGroup)
                        break
                }
            }

            if (isInnerListEmpty) {
                it.subList(position-1, position+1).clear()
                adapter?.notifyItemRangeRemoved(position - 1, 2)
            } else {
                it.remove(childObj)
                adapter?.notifyItemRemoved(position)
                if (groupPos != -1) adapter?.notifyItemChanged(groupPos) else {}
            }
        }
    }

    private fun updateProduct(productBean: PublishedProductsBean) {
        val intent = Intent(this, ProductsActivity::class.java)
        intent.putExtra(Constants.INTENT_PARENT_CATEGORY_ID, productBean.parentCatId)
        intent.putExtra(Constants.INTENT_PRODUCT_ID, productBean.productId)
        startActivity(intent)
    }

    private fun launchProductSelectionScreen(selectedCatLst: ArrayList<ParentCategory>, isDraft: Boolean = false) {
        val intent = Intent(this, ProductsActivity::class.java)
        intent.putParcelableArrayListExtra(Constants.INTENT_SELECTED_PRODUCT_CATEGORIES, selectedCatLst)
        if (!isDraft) {
            intent.putExtra(Constants.INTENT_PRODUCT_ID, childObjToEdit?.productId!!.toInt())
            intent.putExtra(Constants.INTENT_PRODUCT_SELECTION_STATE, ProductSelectionState.EDIT_PRODUCT)
            intent.putExtra(Constants.INTENT_EDIT_PUBLISHED_PRODUCT, true)
        }
        startActivity(intent)
    }
}
