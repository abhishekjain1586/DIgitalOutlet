package com.digitaloutlet.utils.consentsms

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.fragment.app.Fragment
import com.digitaloutlet.R
import com.digitaloutlet.application.DOApplication
import com.digitaloutlet.receiver.SMSConsentReceiver
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.auth.api.phone.SmsRetrieverClient
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener

object ConsentSMSUtil {

    private var mConsentListener: OnConsentSMSListener? = null
    private val intentFilter: IntentFilter = IntentFilter()
    private var client: SmsRetrieverClient? = null
    private var mReceiver: SMSConsentReceiver = SMSConsentReceiver()
    private lateinit var mContext: Context

    const val REQ_USER_CONSENT = 100

    init {
        intentFilter.addAction(SmsRetriever.SMS_RETRIEVED_ACTION)
    }

    public fun setListener(smsReceiver: OnConsentSMSListener) {
        mConsentListener = smsReceiver
    }

    /**
     *  Registering the broadcast receiver to listen for SMS
     */
    public fun registerConsentReceiver(fragment: Fragment) {
        mContext = fragment.requireContext()
        if (client == null) {
            client = SmsRetriever.getClient(mContext)
        }

        mReceiver.setListener(object : SMSConsentReceiver.OnSMSBroadcastReceivedListener {
            override fun onSMSBroadcastReceived(intent: Intent?) {
                intent?.let {
                    fragment.startActivityForResult(it, REQ_USER_CONSENT)
                }
            }

            override fun onTimeout() {
                mConsentListener?.onFailureSMS(DOApplication._INSTANCE.getAppContext().resources.getString(
                    R.string.error_on_sms_timeout))
            }

        })

        mContext.registerReceiver(mReceiver, intentFilter)
    }


    fun startConsentSMS() {
        // Started the SMS Consent API for listening to SMS Broadcast
        val task = client!!.startSmsUserConsent(null)

        // Identifying success or failure for Task
        task.addOnSuccessListener(
            object : OnSuccessListener<Void> {
                override fun onSuccess(void: Void?) {
                    mConsentListener?.onConsentApiCallSuccess()
                }
            })

        task.addOnFailureListener {
            object : OnFailureListener {
                override fun onFailure(e: Exception) {
                    mConsentListener?.onConsentApiCallFailure(DOApplication._INSTANCE.getAppContext().resources.getString(
                        R.string.error_start_consentsms_failed))
                    e.printStackTrace()
                }
            } }
        // End
    }

    public fun unregisterConsentReceiver() {
        mConsentListener = null
        mReceiver.setListener(null)
        mContext.unregisterReceiver(mReceiver)
    }

    interface OnConsentSMSListener {
        fun onFailureSMS(errorMsg: String)
        fun onConsentApiCallFailure(strError: String)
        fun onConsentApiCallSuccess()

    }
}