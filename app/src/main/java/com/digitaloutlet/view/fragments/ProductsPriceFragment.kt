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
import com.digitaloutlet.db.entities.ProductsEntity
import com.digitaloutlet.utils.Constants
import com.digitaloutlet.utils.DialogUtil
import com.digitaloutlet.view.activities.ProductsActivity
import com.digitaloutlet.view.activities.SummaryActivity
import com.digitaloutlet.view.base.BaseActivity
import com.digitaloutlet.view.base.BaseFragment
import com.digitaloutlet.view.enums.ProductSelectionState
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
        initObservers()
    }

    private fun initObservers() {
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

        mViewModel.obsvGetProducts().observe(viewLifecycleOwner, object :
                Observer<ArrayList<ProductsEntity>> {
            override fun onChanged(productsPriceLst: ArrayList<ProductsEntity>) {
                initAdapter(productsPriceLst)
            }
        })

        mViewModel.obsvSaveAsDraft().observe(viewLifecycleOwner, object :
                Observer<Boolean> {
            override fun onChanged(saveAsDraft: Boolean) {
                if (saveAsDraft) {
                    launchDashboard()
                }
            }
        })

        mViewModel.obsvMoveToNextCategory().observe(viewLifecycleOwner, object :
                Observer<Boolean> {
            override fun onChanged(isNextCatAvailable: Boolean) {
                proceedNext()
            }
        })
    }

    override fun initData() {
        mViewModel.setCurrentCategory(mActivityViewModel.getCurrentCategory())

        var strNextLabel = requireContext().getString(R.string.next_category)
        if (!mActivityViewModel.hasNextCategory()) {
            strNextLabel = requireContext().getString(R.string.review)
        }
        mBtnNextCategory.text = strNextLabel

        if (mActivityViewModel.getProductSelectionState() == ProductSelectionState.EDIT_PRODUCT) {
            arguments?.let {
                mViewModel.getProductByID(it.getInt(Constants.INTENT_PRODUCT_ID))
            }
        } else {
            mViewModel.getProducts(mActivityViewModel.getProductSelectionState() == ProductSelectionState.EDIT_CATEGORIES)
        }
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
            launchProductsScreen()
        } else {
            launchSummaryScreen()
        }
    }

    private fun launchProductsScreen() {
        mActivityViewModel.updateCurrentCategory()
        (requireContext() as ProductsActivity).clearAllFragmentsInclusive(ProductsPriceFragment::class.simpleName)
        if (mActivityViewModel.getProductSelectionState() == ProductSelectionState.EDIT_PRODUCT
                || mActivityViewModel.getProductSelectionState() == ProductSelectionState.EDIT_CATEGORIES
                || mActivityViewModel.getProductSelectionState() == ProductSelectionState.ADD_PRICE) {
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