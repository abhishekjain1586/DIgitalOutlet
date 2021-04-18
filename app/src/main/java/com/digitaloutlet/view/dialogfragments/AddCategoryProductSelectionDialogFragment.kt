package com.digitaloutlet.view.dialogfragments

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.digitaloutlet.R
import com.digitaloutlet.application.DOApplication
import com.digitaloutlet.utils.DOUtils
import com.digitaloutlet.view.activities.CreateCatalogActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AddCategoryProductSelectionDialogFragment : BottomSheetDialogFragment(), View.OnClickListener {

    private val TAG = AddCategoryProductSelectionDialogFragment::class.java.simpleName

    private lateinit var contentView: View
    private lateinit var ivCLose: ImageView
    private lateinit var btmShtLayout: LinearLayout
    private lateinit var rlAddCategories: RelativeLayout
    private lateinit var rlAddProducts: RelativeLayout

    private lateinit var bottomSheetDialog: BottomSheetDialog
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    private var mIsExpandHeightSet = false

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        bottomSheetDialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog

        contentView = View.inflate(context, R.layout.dialog_fragment_add_new_category_product, null) as LinearLayout
        bottomSheetDialog.setContentView(contentView)


        ivCLose = contentView.findViewById(R.id.iv_close)
        btmShtLayout = contentView.findViewById(R.id.bottomSheet)
        rlAddCategories = contentView.findViewById(R.id.lay_add_categories)
        rlAddProducts = contentView.findViewById(R.id.lay_add_products)

        bottomSheetBehavior = BottomSheetBehavior.from(contentView.parent as View)
        bottomSheetBehavior.peekHeight = (DOApplication._INSTANCE.getWindowHeight()*0.4).toInt() //BottomSheetBehavior.PEEK_HEIGHT_AUTO

        //initAdapter()
        setCallback()

        return bottomSheetDialog
    }

    /*private fun initAdapter() {
        linearLayoutManager = LinearLayoutManager(requireContext());
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        rvTOS.layoutManager = linearLayoutManager;

        adapter = TypeOfBusinessAdapter(requireContext())
        adapter.setItemClickListener(this)
        adapter.setData(mTOSLst)
        rvTOS.adapter = adapter
    }*/

    private fun setCallback() {
        ivCLose.setOnClickListener(this)
        rlAddCategories.setOnClickListener(this)
        rlAddProducts.setOnClickListener(this)

        bottomSheetBehavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(view: View, i: Int) {
                if (BottomSheetBehavior.STATE_EXPANDED == i) {
                    Log.d(TAG, "STATE_EXPANDED")
                }
                if (BottomSheetBehavior.STATE_COLLAPSED == i) {
                    Log.d(TAG, "STATE_COLLAPSED")
                }
                if (BottomSheetBehavior.STATE_DRAGGING == i) {
                    Log.d(TAG, "STATE_DRAGGING")

                    if (!mIsExpandHeightSet && (btmShtLayout.height > DOApplication._INSTANCE.getWindowHeight() - DOUtils.getActionBarSize(requireContext()) - DOUtils.getStatusBarHeight(requireActivity()))) {
                        val params = LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        params.height = DOApplication._INSTANCE.getWindowHeight() - DOUtils.getActionBarSize(requireContext()) - DOUtils.getStatusBarHeight(requireActivity())
                        btmShtLayout.layoutParams = params
                    }
                    mIsExpandHeightSet = true
                }
                if (BottomSheetBehavior.STATE_HIDDEN == i) {
                    Log.d(TAG, "STATE_HIDDEN")
                    dismiss()
                }
            }

            override fun onSlide(p0: View, p1: Float) {

            }
        })
    }

    override fun onClick(view: View?) {
        when(view?.id) {
            R.id.iv_close -> {
                bottomSheetDialog.dismiss()
            }

            R.id.lay_add_categories -> {
                launchCreateCatalogScreen()
                bottomSheetDialog.dismiss()
            }

            R.id.lay_add_products -> {
                launchSearchProductsScreen()
                bottomSheetDialog.dismiss()
            }
        }
    }

    private fun launchCreateCatalogScreen() {
        val intent = Intent(requireContext(), CreateCatalogActivity::class.java)
        startActivity(intent)
    }

    private fun launchSearchProductsScreen() {
        val intent = Intent(requireContext(), CreateCatalogActivity::class.java)
        startActivity(intent)
    }
}