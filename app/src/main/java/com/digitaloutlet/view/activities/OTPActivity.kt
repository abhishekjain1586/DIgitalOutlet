package com.digitaloutlet.view.activities

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProviders
import com.digitaloutlet.R
import com.digitaloutlet.utils.Constants
import com.digitaloutlet.view.base.BaseActivity
import com.digitaloutlet.view.fragments.GenerateOTPFragment
import com.digitaloutlet.viewmodel.OTPActivityViewModel

class OTPActivity : BaseActivity() {

    private lateinit var fragmentManager: FragmentManager
    private lateinit var viewModel : OTPActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp)

        initView()
        initData()
    }

    private fun initView() {
        viewModel = ViewModelProviders.of(this).get(OTPActivityViewModel::class.java)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = Constants.EMPTY
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home -> {
                onBackPressed()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initData() {
        fragmentManager = supportFragmentManager
        viewModel.setIsLoginJourney(intent.getBooleanExtra(Constants.INTENT_OTP_LOGIN_JOURNEY, false))
        addGenerateOTPFragment()
    }

    private fun addGenerateOTPFragment() {
        addFragment(GenerateOTPFragment())
    }

    fun addFragment(fragment: Fragment) {
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.container, fragment)
        fragmentTransaction.addToBackStack(fragment::class.java.simpleName)
        fragmentTransaction.commit()
    }

    override fun onBackPressed() {
        if (fragmentManager.backStackEntryCount > 1) {
            super.onBackPressed()
        } else {
            finish()
        }
    }
}
