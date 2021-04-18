package com.digitaloutlet.utils

import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.view.View
import android.view.Window
import androidx.fragment.app.Fragment
import com.google.android.gms.auth.api.credentials.Credentials
import com.google.android.gms.auth.api.credentials.HintRequest

object DOUtils {

    const val CREDENTIAL_PICKER_REQUEST = 1

    fun parseOneTimeCode(message: String): String {
        return Regex("(\\d{5})").find(message)?.value ?: Constants.EMPTY
    }

    // Construct a request for phone numbers and show the picker
    fun requestHint(fragment: Fragment) {
        val hintRequest = HintRequest.Builder()
            .setPhoneNumberIdentifierSupported(true)
            .build()
        val credentialsClient = Credentials.getClient(fragment.requireActivity())
        val intent = credentialsClient.getHintPickerIntent(hintRequest)
        fragment.startIntentSenderForResult(
            intent.intentSender,
            CREDENTIAL_PICKER_REQUEST,
            null, 0, 0, 0, null
        )
    }

    fun validateMobileNumber(number: String): String {
        var num = number

        num = num.replace(Constants.PLUS_91, Constants.EMPTY)
        if (num.isNotEmpty()) {
            var containsInitialZero: Boolean
            do {
                if (num.startsWith("0")) {
                    num.replaceFirst("0", Constants.EMPTY)
                }
                containsInitialZero = num.startsWith("0")
            } while (containsInitialZero)
        }
        return num
    }

    fun getActionBarSize(context: Context): Int {
        val array = context!!.theme.obtainStyledAttributes(intArrayOf(android.R.attr.actionBarSize))
        return array.getDimension(0, 0f).toInt()
    }

    fun getTitleBarHeight(activity: Activity): Int {
        val viewTop: Int = activity.window.findViewById<View>(Window.ID_ANDROID_CONTENT).top
        return viewTop + getStatusBarHeight(activity)
    }

    fun getStatusBarHeight(activity: Activity): Int {
        val r = Rect()
        val w: Window = activity.window
        w.decorView.getWindowVisibleDisplayFrame(r)
        return r.top
    }

    // Digits - 48-57
    // . - 46
    fun isDigitAndDot(value: Char?): Boolean {
        value?.let {
            if ((it.toInt() in 48..57) || it.toInt() == 46) {
                return true
            }
        }
        return false
    }
}