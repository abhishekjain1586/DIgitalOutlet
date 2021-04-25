package com.digitaloutlet.view.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.digitaloutlet.R
import com.digitaloutlet.adapter.ProductsPriceAdapter
import com.digitaloutlet.application.DOApplication
import com.digitaloutlet.db.entities.ProductsEntity
import com.digitaloutlet.utils.Constants
import com.digitaloutlet.utils.DialogUtil
import com.digitaloutlet.view.activities.ProductsActivity
import com.digitaloutlet.view.activities.SummaryActivity
import com.digitaloutlet.view.base.BaseActivity
import com.digitaloutlet.view.base.BaseFragment
import com.digitaloutlet.viewholder.OnItemClickListener
import com.digitaloutlet.viewmodel.ProductsActivityViewModel
import com.digitaloutlet.viewmodel.ProductsPriceFragmentViewModel

class ProductsPriceFragment : BaseFragment(), View.OnClickListener,
    OnItemClickListener<String> {

    private lateinit var mRvProducts: RecyclerView
    private lateinit var mBtnSaveAsDraft: Button
    private lateinit var mBtnNextCategory: Button

    private lateinit var mViewModel: ProductsPriceFragmentViewModel
    private lateinit var mActivityViewModel: ProductsActivityViewModel
    private lateinit var linearLayoutManager : LinearLayoutManager
    private var mAdapter: ProductsPriceAdapter? = null
    private var editProductID: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            if (it.containsKey(Constants.INTENT_PRODUCT_ID)) {
                editProductID = it.getInt(Constants.INTENT_PRODUCT_ID)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        baseView = inflater.inflate(R.layout.fragment_products_price, container, false)
        return baseView
    }

    override fun onResume() {
        super.onResume()
        Log.d("Hashhhcode", "ProductsPriceFragment // "+hashCode())
    }

    override fun initViews() {
        mViewModel = ViewModelProviders.of(this).get(ProductsPriceFragmentViewModel::class.java)
        mActivityViewModel = ViewModelProviders.of(requireActivity()).get(ProductsActivityViewModel::class.java)
        (requireActivity() as ProductsActivity).supportActionBar?.title = mActivityViewModel.getCurrentCategory()?.parent_cat

        mRvProducts = baseView.findViewById(R.id.rv_product_price)
        mBtnSaveAsDraft = baseView.findViewById(R.id.btn_save_as_draft)
        mBtnNextCategory = baseView.findViewById(R.id.btn_next_category)

        linearLayoutManager = LinearLayoutManager(requireContext());
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        mRvProducts.layoutManager = linearLayoutManager;
    }

    override fun initListeners() {
        mBtnSaveAsDraft.setOnClickListener(this)
        mBtnNextCategory.setOnClickListener(this)
    }

    override fun initData() {
        var parentCatID: String? = null

        //mViewModel.setCurrentParentCategory((requireContext() as ProductsActivity).getCurrentParentCategory())

        /*var productsLst = ArrayList<ProductsEntity>()
        if (!(activity as ProductsActivity).isEditPublishedProduct) {
            arguments?.getParcelableArrayList<ProductsEntity>(Constants.INTENT_SELECTED_PRODUCT)?.let {
                productsLst = it
            }
        }*/

        if (arguments?.containsKey(Constants.INTENT_PARENT_CATEGORY_ID)!!) {
            parentCatID = arguments?.getString(Constants.INTENT_PARENT_CATEGORY_ID)
        }


        mViewModel.showLoader().observe(viewLifecycleOwner, object : Observer<Boolean> {
            override fun onChanged(value: Boolean) {
                if (value) {
                    (requireContext() as ProductsActivity).showLoader()
                } else {
                    (requireContext() as ProductsActivity).dismissLoader()
                }
            }
        })

        mViewModel.showErrorDialog().observe(viewLifecycleOwner, object : Observer<String> {
            override fun onChanged(message: String) {
                DialogUtil.showCommonActionDialog(requireContext(), Constants.EMPTY, message, false, null)
            }
        })

        mViewModel.getAllMerchantActiveProductsObserver().observe(viewLifecycleOwner, object :
            Observer<ArrayList<ProductsEntity>> {
            override fun onChanged(productsPriceLst: ArrayList<ProductsEntity>) {
                initAdapter(productsPriceLst)
            }
        })

        mViewModel.saveAsDraftObserver().observe(viewLifecycleOwner, object :
            Observer<Boolean> {
            override fun onChanged(saveAsDraft: Boolean) {
                if (saveAsDraft) {
                    launchDashboard()
                }
            }
        })

        mViewModel.moveToNextCategoryObserver().observe(viewLifecycleOwner, object :
            Observer<Boolean> {
            override fun onChanged(isNextCatAvailable: Boolean) {
                proceedNext()
            }
        })

        editProductID?.let {
            mViewModel.setMerchantActiveProducts(it)
        } ?: run {
            parentCatID?.let {
                mViewModel.getProducts(it)
            }
        }

        var strNextLabel = requireContext().getString(R.string.next_category)
        if (!mActivityViewModel.hasNextCategory()) {
            strNextLabel = requireContext().getString(R.string.review)
        }
        mBtnNextCategory.text = strNextLabel
    }

    private fun initAdapter(productsPriceLst: ArrayList<ProductsEntity>) {
        var tempAdapter = mAdapter
        if (tempAdapter == null) {
            tempAdapter = ProductsPriceAdapter(requireContext())
            mAdapter = tempAdapter
        }
        tempAdapter.setData(productsPriceLst)
        mRvProducts.adapter = mAdapter
        tempAdapter.setListener(this)
    }

    override fun onClick(view: View?) {
        when(view?.id) {
            R.id.btn_save_as_draft -> {
                mViewModel.savePriceInDB(true)
            }

            R.id.btn_next_category -> {
                mViewModel.savePriceInDB(false)
            }
        }
    }

    override fun onItemClick(position: Int, price: String, type: Int) {
        mViewModel.updateProductPrice(position, price)
    }

    private fun proceedNext() {
        if (mActivityViewModel.hasNextCategory()) {
            mActivityViewModel.updateCurrentCategory()
            launchProductsScreen()
        } else {
            //mViewModel.checkProductsToPublish()
            launchSummaryScreen()
        }
    }

    private fun launchProductsScreen() {
        (requireContext() as ProductsActivity).clearAllFragmentsInclusive(ProductsPriceFragment::class.simpleName)
        if ((requireContext() as ProductsActivity).isEditPublishedProduct) {
            (requireContext() as ProductsActivity).addFragment(ProductsPriceFragment(), null)
        } else {
            (requireContext() as ProductsActivity).addFragment(ProductsSelectionFragment(), null)
        }
    }

    private fun launchSummaryScreen() {
        val intent = Intent(requireContext(), SummaryActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    private fun launchDashboard() {
        (requireContext() as BaseActivity).launchDashboard()
        //requireActivity().finish()
    }
}