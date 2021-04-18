package com.digitaloutlet.view.base

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.digitaloutlet.R
import com.digitaloutlet.utils.Constants
import com.digitaloutlet.view.activities.DashboardActivity
import com.digitaloutlet.view.activities.LauncherActivity
import com.digitaloutlet.view.activities.ProfileActivity
import com.digitaloutlet.view.dialogfragments.ShareDialogFragment

abstract class BaseActivity : AppCompatActivity() {

    protected var progressDialog : Dialog? = null

    fun showLoader() {
        if (progressDialog == null) {
            progressDialog = Dialog(this)
            progressDialog?.setContentView(R.layout.progress_layout)
            progressDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
            progressDialog?.setCancelable(false)
        }
        progressDialog?.show()
    }

    fun dismissLoader() {
        progressDialog?.dismiss()
    }

    fun showToastMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    fun getEditableViewForCommonEntryLayout(view: View, strLabel: String = Constants.EMPTY, strHint: String = Constants.EMPTY): EditText {
        //val layout = view.findViewById<View>(R.id.lay_mobile_no)
        val edtView = view.findViewById<EditText>(R.id.edt_value)

        if (strLabel.isNotEmpty()) {
            view.findViewById<TextView>(R.id.tv_name).text = strLabel
        }

        if (strHint.isNotEmpty()) {
            edtView.hint = strHint
        }

        return edtView
    }

    fun getSpinnerTextViewForCommonEntryLayout(view: View, strLabel: String = Constants.EMPTY, strHint: String = Constants.EMPTY): EditText {
        //val layout = view.findViewById<View>(R.id.lay_mobile_no)
        val edtView = view.findViewById<EditText>(R.id.edt_value)

        if (strLabel.isNotEmpty()) {
            view.findViewById<TextView>(R.id.tv_name).text = strLabel
        }

        if (strHint.isNotEmpty()) {
            edtView.hint = strHint
        }

        return edtView
    }

    fun share() {
        val shareDialogFragment = ShareDialogFragment()
        shareDialogFragment.show(
            supportFragmentManager,
            shareDialogFragment.tag
        )
    }

    fun launchDashboard() {
        val intent = Intent(this, DashboardActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        startActivity(intent)
    }

    fun launchProfile() {
        val intent = Intent(this, ProfileActivity::class.java)
        startActivity(intent)
    }

    fun launchLauncher() {
        val intent = Intent(this, LauncherActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        startActivity(intent)
    }
}