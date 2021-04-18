package com.digitaloutlet.view.dialogfragments

import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.digitaloutlet.R
import com.digitaloutlet.application.DOApplication
import com.digitaloutlet.service.ServiceHelper
import com.digitaloutlet.utils.DOPrefs
import com.digitaloutlet.utils.DOUtils
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class ShareDialogFragment : BottomSheetDialogFragment(), View.OnClickListener {

    private val TAG = ShareDialogFragment::class.java.simpleName

    private lateinit var contentView: View

    private lateinit var btmShtLayout: LinearLayout
    private lateinit var bottomSheetDialog: BottomSheetDialog
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    private var mIsExpandHeightSet = false

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        bottomSheetDialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog

        contentView = View.inflate(context, R.layout.dialog_fragment_add_new_category_product, null) as LinearLayout
        bottomSheetDialog.setContentView(contentView)

        contentView.findViewById<TextView>(R.id.tv_add_new_title).setText(R.string.title_share_via)
        contentView.findViewById<TextView>(R.id.tv_divider2).visibility = View.VISIBLE
        contentView.findViewById<RelativeLayout>(R.id.lay_option_3).visibility = View.VISIBLE

        contentView.findViewById<ImageView>(R.id.iv_category_icon).visibility = View.GONE
        contentView.findViewById<ImageView>(R.id.iv_product_icon).visibility = View.GONE
        contentView.findViewById<ImageView>(R.id.iv_option_3_icon).visibility = View.GONE

        contentView.findViewById<TextView>(R.id.tv_category_title).setText(getString(R.string.title_share_via_whatsapp))
        contentView.findViewById<TextView>(R.id.tv_product_title).setText(getString(R.string.title_share_via_email))
        contentView.findViewById<TextView>(R.id.tv_option_3_title).setText(getString(R.string.title_share_via_sms))

        btmShtLayout = contentView.findViewById(R.id.bottomSheet)
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
        contentView.findViewById<ImageView>(R.id.iv_close).setOnClickListener(this)
        contentView.findViewById<RelativeLayout>(R.id.lay_add_categories).setOnClickListener(this)
        contentView.findViewById<RelativeLayout>(R.id.lay_add_products).setOnClickListener(this)
        contentView.findViewById<RelativeLayout>(R.id.lay_option_3).setOnClickListener(this)

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
                shareOnWhatsApp()
                bottomSheetDialog.dismiss()
            }

            R.id.lay_add_products -> {
                shareOnEmail()
                bottomSheetDialog.dismiss()
            }

            R.id.lay_option_3 -> {
                shareOnSMS()
                bottomSheetDialog.dismiss()
            }
        }
    }

    private fun shareOnWhatsApp() {
        val whatsappIntent = Intent(Intent.ACTION_SEND)
        whatsappIntent.type = "text/plain"
        whatsappIntent.setPackage("com.whatsapp")
        whatsappIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.title_digital_menu) + "\n" + ServiceHelper.OUTLET_URL + DOPrefs.getMerchantId())
        try {
            activity!!.startActivity(whatsappIntent)
        } catch (ex: ActivityNotFoundException) {
            Toast.makeText(context, resources.getString(R.string.error_whatsapp_not_installed), Toast.LENGTH_LONG).show()
        }
    }

    private fun shareOnEmail() {
        val emailIntent = Intent(Intent.ACTION_SENDTO)
        emailIntent.setData(Uri.parse("mailto:"))
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.title_digital_menu))
        emailIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.title_digital_menu) + "\n" + ServiceHelper.OUTLET_URL + DOPrefs.getMerchantId())
        startActivity(Intent.createChooser(emailIntent, resources.getString(R.string.title_choose_app)))
    }

    private fun shareOnSMS() {

    }
}