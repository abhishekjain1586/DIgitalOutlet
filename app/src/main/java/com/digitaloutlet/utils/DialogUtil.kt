package com.digitaloutlet.utils

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.view.View
import android.widget.TextView
import com.digitaloutlet.R

object DialogUtil {

    interface DailogCallback{
        fun onEventOK()
        fun onEventCancel()
    }

    fun showCommonActionDialog(context: Context, title: String, message: String,
                               hasCancelEvent: Boolean, listener: DailogCallback?) {

        showCommonActionDialog(context, title, message,
            context.resources.getString(R.string.ok), context.resources.getString(R.string.cancel),
            hasCancelEvent, listener)
    }

    fun showCommonActionDialog(context: Context, title: String, message: String,
                                okButtonLabel: String, cancelButtonLabel: String,
                                hasCancelEvent: Boolean, listener: DailogCallback?) {
        var dialog: AlertDialog? = null
        val builder = AlertDialog.Builder(context, R.style.CustomAlertDialog).apply {
            setCancelable(false)
            val view = (context as Activity).layoutInflater.inflate(R.layout.common_dialog, null)
            val tvMsg = view.findViewById<TextView>(R.id.tv_msg)
            val btnOk = view.findViewById<TextView>(R.id.btn_ok)
            val btnCancel = view.findViewById<TextView>(R.id.btn_cancel)
            setView(view)
            /*if (title.isNotEmpty()) {
                setTitle(title)
            }*/
            if (message.isNotEmpty()) {
                tvMsg.text = message
            }
            if (okButtonLabel.isNotEmpty()) {
                btnOk.text = okButtonLabel
            }
            if (cancelButtonLabel.isNotEmpty()) {
                btnCancel.text = cancelButtonLabel
            }

            btnOk.setOnClickListener(object : View.OnClickListener {
                override fun onClick(viee: View?) {
                    listener?.onEventOK()
                    dialog?.dismiss()
                }
            })

            if (hasCancelEvent) {
                btnCancel.visibility = View.VISIBLE
                btnCancel.setOnClickListener(object : View.OnClickListener {
                    override fun onClick(viee: View?) {
                        listener?.onEventCancel()
                        dialog?.dismiss()
                    }
                })
            }
        }

        dialog = builder.create()
        dialog.show()
    }
}