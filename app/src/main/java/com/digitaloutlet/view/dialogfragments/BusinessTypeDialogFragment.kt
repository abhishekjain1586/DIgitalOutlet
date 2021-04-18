package com.digitaloutlet.view.dialogfragments

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.digitaloutlet.R
import com.digitaloutlet.adapter.TypeOfBusinessAdapter
import com.digitaloutlet.application.DOApplication
import com.digitaloutlet.model.response.TypeOfShop
import com.digitaloutlet.utils.DOUtils
import com.digitaloutlet.viewholder.OnItemClickListener
import com.digitaloutlet.viewmodel.SignUpActivityViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class BusinessTypeDialogFragment : BottomSheetDialogFragment(), View.OnClickListener,
    OnItemClickListener<TypeOfShop> {

    private val TAG = BusinessTypeDialogFragment::class.java.simpleName

    private lateinit var btmShtLayout: LinearLayout
    private lateinit var ivCLose: ImageView
    private lateinit var rvTOS: RecyclerView

    private lateinit var bottomSheetDialog: BottomSheetDialog
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    private lateinit var contentView: View
    private var mIsExpandHeightSet = false

    private lateinit var viewModel : SignUpActivityViewModel
    private var mTOSLst = ArrayList<TypeOfShop>()
    lateinit var linearLayoutManager: LinearLayoutManager
    lateinit var adapter: TypeOfBusinessAdapter

    /*interface OnTypeOfShopSelectedListener {
        fun onTypeOfShopSelected(selectedTOS: TypeOfShop)
    }*/

    fun setTOSLst(tosLst: ArrayList<TypeOfShop>) {
        mTOSLst.clear()
        mTOSLst.addAll(tosLst)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        viewModel = ViewModelProviders.of(requireActivity()).get(SignUpActivityViewModel::class.java)

        bottomSheetDialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog

        contentView = View.inflate(context, R.layout.dialog_fragment_business_type, null) as LinearLayout
        bottomSheetDialog.setContentView(contentView)


        ivCLose = contentView.findViewById(R.id.iv_close)
        btmShtLayout = contentView.findViewById(R.id.bottomSheet)
        rvTOS = contentView.findViewById(R.id.rv_tob)

        bottomSheetBehavior = BottomSheetBehavior.from(contentView.parent as View)
        bottomSheetBehavior.peekHeight = (DOApplication._INSTANCE.getWindowHeight()*0.4).toInt() //BottomSheetBehavior.PEEK_HEIGHT_AUTO

        initAdapter()
        setCallback()

        return bottomSheetDialog
    }

    private fun initAdapter() {
        linearLayoutManager = LinearLayoutManager(requireContext());
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        rvTOS.layoutManager = linearLayoutManager;

        adapter = TypeOfBusinessAdapter(requireContext())
        adapter.setItemClickListener(this)
        adapter.setData(mTOSLst)
        rvTOS.adapter = adapter
    }

    private fun setCallback() {
        ivCLose.setOnClickListener(this)

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
        }
    }

    override fun onItemClick(position: Int, typeOfShop: TypeOfShop, type: Int) {
        viewModel.setSelectedTypeOfShop(typeOfShop)
        dismiss()
    }
}