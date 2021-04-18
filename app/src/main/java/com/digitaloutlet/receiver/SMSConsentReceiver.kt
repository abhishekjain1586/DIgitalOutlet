package com.digitaloutlet.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status

class SMSConsentReceiver() : BroadcastReceiver() {

    private var mListener: OnSMSBroadcastReceivedListener? = null

    fun setListener(listener: OnSMSBroadcastReceivedListener?) {
        mListener = listener
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == SmsRetriever.SMS_RETRIEVED_ACTION) {
            val status = intent.extras?.get(SmsRetriever.EXTRA_STATUS) as Status
            when(status.statusCode) {
                CommonStatusCodes.SUCCESS -> {
                    mListener?.onSMSBroadcastReceived(intent.extras?.getParcelable<Intent>(SmsRetriever.EXTRA_CONSENT_INTENT))
                }

                CommonStatusCodes.TIMEOUT -> {
                    mListener?.onTimeout()
                }
            }
        }
    }

    interface OnSMSBroadcastReceivedListener {
        fun onSMSBroadcastReceived(intent: Intent?)
        fun onTimeout()

    }
}