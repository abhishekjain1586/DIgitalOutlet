package com.digitaloutlet.view.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.digitaloutlet.R
import com.digitaloutlet.db.DBHelper
import com.digitaloutlet.utils.Constants
import com.digitaloutlet.utils.DOPrefs
import com.digitaloutlet.view.base.BaseActivity

class LauncherActivity : BaseActivity(), View.OnClickListener {

    private lateinit var btnLogin: Button
    private lateinit var tvSignup: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launcher)

        if (!checkIsLoggedIn()) {
            initView()
        }
    }

    private fun initView() {
        btnLogin = findViewById(R.id.btn_login)
        tvSignup = findViewById(R.id.tv_signup)

        btnLogin.setOnClickListener(this)
        tvSignup.setOnClickListener(this)
    }

    private fun checkIsLoggedIn(): Boolean {
        if (!DOPrefs.getMerchantId().isNullOrEmpty()) {
            if (DBHelper.getInstance().getProductsDao().getProductsByMerchant(DOPrefs.getMerchantId()).isNullOrEmpty()) {
                launchCategoryScreen()
            } else {
                launchDashboard()
                finish()
                //launchCategoryScreen()
            }
            return true
        }

        return false
    }

    private fun launchCategoryScreen() {
        val intent = Intent(this, CreateCatalogActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onClick(view: View?) {
        when(view?.id) {
            R.id.btn_login -> {
                launchOTPScreen(true)
            }

            R.id.tv_signup -> {
                launchOTPScreen(false)
            }
        }
    }

    private fun launchOTPScreen(isLogin: Boolean) {
        val intent = Intent(this, OTPActivity::class.java)
        intent.putExtra(Constants.INTENT_OTP_LOGIN_JOURNEY, isLogin)
        startActivity(intent)
    }
}