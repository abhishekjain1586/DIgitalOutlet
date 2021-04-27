package com.digitaloutlet.view.fragments

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Button
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.digitaloutlet.R
import com.digitaloutlet.adapter.ProductsAdapter
import com.digitaloutlet.db.entities.ProductsEntity
import com.digitaloutlet.utils.Constants
import com.digitaloutlet.utils.DialogUtil
import com.digitaloutlet.view.activities.ProductsActivity
import com.digitaloutlet.view.activities.SummaryActivity
import com.digitaloutlet.view.base.BaseActivity
import com.digitaloutlet.view.base.BaseFragment
import com.digitaloutlet.viewholder.OnItemClickListener
import com.digitaloutlet.viewmodel.ProductsActivityViewModel
import com.digitaloutlet.viewmodel.ProductsFragmentViewModel

class ProductsSelectionFragment : BaseFragment(), View.OnClickListener,
    OnItemClickListener<ProductsEntity> {

    private lateinit var mRvProducts: RecyclerView
    private lateinit var mBtnSaveAsDraft: Button
    private lateinit var mBtnProceedToNext: Button

    private lateinit var mViewModel: ProductsFragmentViewModel
    private lateinit var mActivityViewModel: ProductsActivityViewModel
    private lateinit var linearLayoutManager : LinearLayoutManager
    private var mAdapter: ProductsAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        baseView = inflater.inflate(R.layout.fragment_products_selection, container, false)
        return baseView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_product_selection_frag, menu)

        val menuItem = menu.findItem(R.id.action_search)
        val searchView = menuItem.actionView as SearchView
        searchView.queryHint = getString(R.string.hint_search_product)
        searchView.setIconifiedByDefault(true)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                mAdapter?.getFilter()?.filter(newText)
                return false
            }

        })
    }

    override fun initViews() {
        mViewModel = ViewModelProviders.of(this).get(ProductsFragmentViewModel::class.java)
        mActivityViewModel = ViewModelProviders.of(requireActivity()).get(ProductsActivityViewModel::class.java)
        (requireActivity() as ProductsActivity).supportActionBar?.title = mActivityViewModel.getCurrentCategory()?.parent_cat
        mViewModel.setCurrentCategory(mActivityViewModel.getCurrentCategory())

        mRvProducts = baseView.findViewById(R.id.rv_parent_category)
        mBtnSaveAsDraft = baseView.findViewById(R.id.btn_save_as_draft)
        mBtnProceedToNext = baseView.findViewById(R.id.btn_proceed_next)

        linearLayoutManager = LinearLayoutManager(requireContext())
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        mRvProducts.layoutManager = linearLayoutManager
    }

    override fun initListeners() {
        mBtnSaveAsDraft.setOnClickListener(this)
        mBtnProceedToNext.setOnClickListener(this)
    }

    override fun initData() {
        //mViewModel.setCurrentParentCategory((requireContext() as ProductsActivity).getCurrentParentCategory())
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

        mViewModel.observerGetProducts(mActivityViewModel.getCurrentCategory()?.id!!).observe(viewLifecycleOwner, object : Observer<ArrayList<ProductsEntity>> {
            override fun onChanged(productsLst: ArrayList<ProductsEntity>?) {
                if (productsLst == null) {
                    DialogUtil.showCommonActionDialog(requireContext(), Constants.EMPTY, getString(R.string.error_no_record_found), false, null)
                    return
                }
                initAdapter(productsLst)
            }
        })

        mViewModel.observerChangeProductState().observe(viewLifecycleOwner, object : Observer<Map<Int, ProductsEntity>> {
            override fun onChanged(productObjMap: Map<Int,ProductsEntity>) {
                for (key in productObjMap.keys) {
                    mAdapter?.notifyItemChanged(key, productObjMap.get(key))
                    break
                }
            }
        })

        mViewModel.observerSaveAsDraft().observe(viewLifecycleOwner, object :
            Observer<Boolean> {
            override fun onChanged(saveAsDraft: Boolean) {
                if (saveAsDraft) {
                    launchDashboard()
                }
            }
        })

        mViewModel.proceedNextToCapturePriceObserver().observe(viewLifecycleOwner, object : Observer<ArrayList<ProductsEntity>> {
            override fun onChanged(selectedLst: ArrayList<ProductsEntity>) {
                if (mActivityViewModel.hasNextCategory()) {
                    mActivityViewModel.updateCurrentCategory()
                }
                launchPriceScreen(selectedLst)
            }
        })

        mViewModel.userConsentToProceedObserver().observe(viewLifecycleOwner, object :
            Observer<Boolean> {
            override fun onChanged(isNextCatAvailable: Boolean) {
                confirmUser()
            }
        })
    }

    private fun initAdapter(productsLst: ArrayList<ProductsEntity>) {
        var tempAdapter = mAdapter
        if (tempAdapter == null) {
            tempAdapter = ProductsAdapter(requireContext())
            mAdapter = tempAdapter
        }
        tempAdapter.setData(productsLst)
        mRvProducts.adapter = mAdapter
        tempAdapter.setItemClickListener(this)
    }

    override fun onClick(view: View?) {
        when(view?.id) {
            R.id.btn_save_as_draft -> {
                mViewModel.saveProductsInDB(mActivityViewModel.getCurrentCategory()?.parent_cat)
            }

            R.id.btn_proceed_next -> {
                mViewModel.saveProductsInDB(mActivityViewModel.getCurrentCategory()?.parent_cat, false)
            }
        }
    }

    override fun onItemClick(position: Int, productObj: ProductsEntity, type: Int) {
        mViewModel.onProductChangeState(position, productObj)
    }

    private fun confirmUser() {
        val isNextCatAvailable = mActivityViewModel.hasNextCategory()
        var errorMsg = resources.getString(R.string.error_no_products_selected_to_proceed_further)
        if (isNextCatAvailable) {
            errorMsg = resources.getString(R.string.error_no_products_selected)
        }
        DialogUtil.showCommonActionDialog(requireContext(), Constants.EMPTY,
            errorMsg,
            resources.getString(R.string.yes),
            resources.getString(R.string.no)
            , true, object : DialogUtil.DailogCallback {
                override fun onEventOK() {
                    if (isNextCatAvailable) {
                        mActivityViewModel.updateCurrentCategory()
                        selectNextCategory()
                    } else {
                        launchSummaryScreen()
                    }
                }

                override fun onEventCancel() {}

            })
    }

    private fun launchDashboard() {
        (requireContext() as BaseActivity).launchDashboard()
        //(requireContext() as ProductsActivity).finish()
    }

    private fun launchSummaryScreen() {
        val intent = Intent(requireContext(), SummaryActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    private fun launchPriceScreen(selectedLst: ArrayList<ProductsEntity>) {
        val bundle = Bundle()
        //bundle.putParcelableArrayList(Constants.INTENT_SELECTED_PRODUCT, selectedLst)
        bundle.putString(Constants.INTENT_PARENT_CATEGORY_ID, selectedLst.get(0).parentCatId)
        (requireActivity() as ProductsActivity).addFragment(ProductsPriceFragment(), bundle)
    }

    private fun selectNextCategory() {
        (requireContext() as ProductsActivity).clearAllFragmentsInclusive(ProductsSelectionFragment::class.simpleName)
        (requireContext() as ProductsActivity).addFragment(ProductsSelectionFragment(), null)
    }
}