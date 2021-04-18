package com.digitaloutlet.utils

import android.content.Context
import android.content.SharedPreferences
import com.digitaloutlet.R
import com.digitaloutlet.application.DOApplication

object DOPrefs {

    private fun getMerchantPref(): SharedPreferences {
        return DOApplication._INSTANCE.getSharedPreferences(DOApplication._INSTANCE.getString(R.string.pref_merchant_file)
            , Context.MODE_PRIVATE)
    }

    fun updateMerchantMenu(fetched: Boolean) {
        getMerchantPref().edit().putBoolean(DOApplication._INSTANCE.getString(R.string.pref_update_merchant_menu), fetched).apply()
    }
    fun toUpdateMerchantMenu(): Boolean {
        return getMerchantPref().getBoolean(DOApplication._INSTANCE.getString(R.string.pref_update_merchant_menu), false)
    }


    fun saveMerchantId(merchantId: String) {
        getMerchantPref().edit().putString(DOApplication._INSTANCE.getString(R.string.pref_merchant_merchant_id), merchantId).apply()
    }
    fun getMerchantId(): String {
        return getMerchantPref().getString(DOApplication._INSTANCE.getString(R.string.pref_merchant_merchant_id), /*"test_merchant_1"*/Constants.EMPTY) ?: Constants.EMPTY
    }


    fun saveTOS(tosId: Int) {
        getMerchantPref().edit().putInt(DOApplication._INSTANCE.getString(R.string.pref_merchant_tos_id), tosId).apply()
    }
    fun getTOS(): Int {
        return getMerchantPref().getInt(DOApplication._INSTANCE.getString(R.string.pref_merchant_tos_id), 4)
    }


    fun saveMerchantName(merchantName: String) {
        getMerchantPref().edit().putString(DOApplication._INSTANCE.getString(R.string.pref_merchant_merchant_name), merchantName).apply()
    }
    fun getMerchantName(): String {
        return getMerchantPref().getString(DOApplication._INSTANCE.getString(R.string.pref_merchant_merchant_name), Constants.EMPTY) ?: Constants.EMPTY
    }

    fun saveMSISDN(msisdn: String) {
        getMerchantPref().edit().putString(DOApplication._INSTANCE.getString(R.string.pref_merchant_msisdn), msisdn).apply()
    }
    fun getMSISDN(): String {
        return getMerchantPref().getString(DOApplication._INSTANCE.getString(R.string.pref_merchant_msisdn), Constants.EMPTY) ?: Constants.EMPTY
    }


    fun clearPref() {
        getMerchantPref().edit().clear().apply()
    }
}