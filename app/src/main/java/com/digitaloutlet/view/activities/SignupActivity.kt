package com.digitaloutlet.view.activities

import android.content.Intent
import android.os.Bundle
import android.text.InputFilter
import android.text.InputType
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.digitaloutlet.R
import com.digitaloutlet.model.bean.SaveMerchantBean
import com.digitaloutlet.model.response.ResSaveMerchant
import com.digitaloutlet.model.response.TypeOfShop
import com.digitaloutlet.utils.Constants
import com.digitaloutlet.utils.DOPrefs
import com.digitaloutlet.utils.DialogUtil
import com.digitaloutlet.view.base.BaseActivity
import com.digitaloutlet.view.dialogfragments.BusinessTypeDialogFragment
import com.digitaloutlet.viewmodel.SignUpActivityViewModel

class SignupActivity : BaseActivity(), View.OnClickListener {

    private lateinit var mEdtFullName: EditText
    private lateinit var mEdtShopName: EditText
    private lateinit var mEdtShopNumber: EditText
    private lateinit var mEdtBusinessType: EditText
    private lateinit var mLayBusinessTypeOthers: View
    private lateinit var mEdtBusinessTypeOthers: EditText
    private lateinit var mEdtShopAddress: EditText
    private lateinit var mEdtPincode: EditText
    private lateinit var mEdtCity: EditText
    private lateinit var mEdtState: EditText
    private lateinit var mBtnSubmit: Button

    private lateinit var viewModel : SignUpActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = getString(R.string.signup)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        initViews()
        initData()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home -> {
                onBackPressed()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initViews() {
        viewModel = ViewModelProviders.of(this).get(SignUpActivityViewModel::class.java)

        mEdtFullName = getEditableViewForCommonEntryLayout(findViewById(R.id.lay_full_name), getString(R.string.full_name), getString(R.string.signup_full_name_hint))
        mEdtFullName.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(resources.getInteger(R.integer.name_length)))
        mEdtFullName.inputType = InputType.TYPE_CLASS_TEXT

        mEdtShopName = getEditableViewForCommonEntryLayout(findViewById(R.id.lay_shop_name), getString(R.string.shop_name), getString(R.string.signup_shop_name_hint))
        mEdtShopName.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(resources.getInteger(R.integer.name_length)))
        mEdtShopName.inputType = InputType.TYPE_CLASS_TEXT

        mEdtShopNumber = getEditableViewForCommonEntryLayout(findViewById(R.id.lay_shop_number), getString(R.string.shop_number), getString(R.string.signup_shop_number_hint))
        mEdtShopNumber.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(resources.getInteger(R.integer.name_length)))
        mEdtShopNumber.inputType = InputType.TYPE_CLASS_TEXT

        mEdtBusinessType = getSpinnerTextViewForCommonEntryLayout(findViewById(R.id.lay_business_type), getString(R.string.business_type), getString(R.string.signup_business_type_hint))
        mEdtBusinessType.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(resources.getInteger(R.integer.name_length)))
        mEdtBusinessType.inputType = InputType.TYPE_CLASS_TEXT
        mEdtBusinessType.isFocusable = false
        mEdtBusinessType.isLongClickable = false

        mLayBusinessTypeOthers = findViewById(R.id.lay_business_type_others)
        mEdtBusinessTypeOthers = getEditableViewForCommonEntryLayout(mLayBusinessTypeOthers, getString(R.string.others), getString(R.string.signup_business_type_others_hint))
        mEdtBusinessTypeOthers.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(resources.getInteger(R.integer.name_length)))
        mEdtBusinessTypeOthers.inputType = InputType.TYPE_CLASS_TEXT

        mEdtShopAddress = getEditableViewForCommonEntryLayout(findViewById(R.id.lay_shop_address), getString(R.string.shop_address), getString(R.string.signup_shop_address_hint))
        mEdtShopAddress.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(resources.getInteger(R.integer.name_length)))
        mEdtShopAddress.inputType = InputType.TYPE_CLASS_TEXT
        //mEdtShopAddress.isFocusable = false
        //mEdtShopAddress.isLongClickable = false

        mEdtPincode = getEditableViewForCommonEntryLayout(findViewById(R.id.lay_pin_code), getString(R.string.pin_code), getString(R.string.signup_pin_code_hint))
        mEdtPincode.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(resources.getInteger(R.integer.pincode_length)))
        mEdtPincode.inputType = InputType.TYPE_CLASS_NUMBER

        mEdtCity = getEditableViewForCommonEntryLayout(findViewById(R.id.lay_city), getString(R.string.city), getString(R.string.signup_city_hint))
        mEdtCity.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(resources.getInteger(R.integer.name_length)))
        mEdtCity.inputType = InputType.TYPE_CLASS_TEXT

        mEdtState = getEditableViewForCommonEntryLayout(findViewById(R.id.lay_state), getString(R.string.state), getString(R.string.signup_state_hint))
        mEdtState.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(resources.getInteger(R.integer.name_length)))
        mEdtState.inputType = InputType.TYPE_CLASS_TEXT

        mBtnSubmit = findViewById(R.id.btn_submit)

        initListener()
    }

    private fun initListener() {
        viewModel.showLoader().observe(this, object : Observer<Boolean> {
            override fun onChanged(value: Boolean) {
                if (value) {
                    showLoader()
                } else {
                    dismissLoader()
                }
            }
        })

        viewModel.showErrorDialog().observe(this, object : Observer<String> {
            override fun onChanged(message: String) {
                DialogUtil.showCommonActionDialog(this@SignupActivity, Constants.EMPTY, message, false, null)
            }
        })

        viewModel.observerSelectedTOS().observe(this, object : Observer<TypeOfShop> {
            override fun onChanged(selectedTOS: TypeOfShop) {
                updateBusinessType(selectedTOS)
            }
        })

        viewModel.saveMerchant().observe(this, object : Observer<ResSaveMerchant> {
            override fun onChanged(response: ResSaveMerchant) {
                saveMerchantDetails(response)
            }
        })

        mEdtBusinessType.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                val lst = viewModel.getTOSList()
                if (!lst.isNullOrEmpty()) {
                    val businessTypeDialogFragment = BusinessTypeDialogFragment()
                    businessTypeDialogFragment.setTOSLst(lst)
                    businessTypeDialogFragment.show(
                        supportFragmentManager,
                        businessTypeDialogFragment.tag
                    )
                }
            }
        })

        /*mEdtShopAddress.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                val shopLocationDialogFragment = ShopLocationDialogFragment()
                shopLocationDialogFragment.show(
                    supportFragmentManager,
                    shopLocationDialogFragment.tag
                )
            }
        })*/

        mBtnSubmit.setOnClickListener(this)
    }

    private fun initData() {
        viewModel.getTypeOfShops()
    }

    private fun informUserForSuccessRegistration(response: ResSaveMerchant) {
        DialogUtil.showCommonActionDialog(this@SignupActivity, getString(R.string.registration_successful_title),
            getString(R.string.registration_successful_message, response.merchant_id), false,
            object : DialogUtil.DailogCallback {
                override fun onEventOK() {
                    launchCreateCatalogScreen()
                }
                override fun onEventCancel() {}

            })
    }

    private fun updateBusinessType(selectedTOS: TypeOfShop) {
        mEdtBusinessTypeOthers.setText(Constants.EMPTY)
        if (selectedTOS.value!!.equals(getString(R.string.business_type_others), true)) {
            mLayBusinessTypeOthers.visibility = View.VISIBLE
        } else {
            mLayBusinessTypeOthers.visibility = View.GONE
        }
        mEdtBusinessType.setText(selectedTOS.value)
    }

    override fun onClick(view: View?) {
        when(view?.id) {
            R.id.btn_submit -> {
                val merchantBean = getMerchantData()
                if (validateMerchantData(merchantBean)) {
                    //viewModel.saveMerchant(merchantBean)
                }
            }
        }
    }

    private fun getMerchantData(): SaveMerchantBean {
        val saveMerchantBean = SaveMerchantBean()
        saveMerchantBean.msisdn = intent.getStringExtra(Constants.INTENT_MSISDN)
        saveMerchantBean.nameOfOwner = mEdtFullName.text.toString()
        saveMerchantBean.nameOfShop = mEdtShopName.text.toString()
        saveMerchantBean.tos = viewModel.getSelectedTOS()?.id
        saveMerchantBean.tosOthers = mEdtBusinessTypeOthers.text.toString()
        saveMerchantBean.shopNo = mEdtShopNumber.text.toString()
        saveMerchantBean.shopAddress = mEdtShopAddress.text.toString()
        saveMerchantBean.pinCode = mEdtPincode.text.toString()
        saveMerchantBean.city = mEdtCity.text.toString()
        saveMerchantBean.state = mEdtState.text.toString()
        saveMerchantBean.latitude = "27.2038"
        saveMerchantBean.longitude = "77.5011"

        return saveMerchantBean
    }

    private fun validateMerchantData(saveMerchant: SaveMerchantBean): Boolean {
        if (saveMerchant.msisdn.isNullOrEmpty()) {
            DialogUtil.showCommonActionDialog(this@SignupActivity, Constants.EMPTY, getString(R.string.error_invalid_msisdn), false, null)
            return false
        }

        if (saveMerchant.nameOfOwner.isNullOrEmpty()) {
            DialogUtil.showCommonActionDialog(this@SignupActivity, Constants.EMPTY, getString(R.string.error_invalid_full_name), false, null)
            return false
        }

        if (saveMerchant.nameOfShop.isNullOrEmpty()) {
            DialogUtil.showCommonActionDialog(this@SignupActivity, Constants.EMPTY, getString(R.string.error_invalid_shop_name), false, null)
            return false
        }

        if (saveMerchant.tos == null) {
            DialogUtil.showCommonActionDialog(this@SignupActivity, Constants.EMPTY, getString(R.string.error_invalid_business_type), false, null)
            return false
        } else {
            var tosOthersId = 0
            for (tosObj in viewModel.getTOSList()!!) {
                if (tosObj.value.equals(getString(R.string.business_type_others), true)) {
                    tosOthersId = tosObj.id!!
                    break
                }
            }
            if (saveMerchant.tos == tosOthersId && saveMerchant.tosOthers.isNullOrEmpty()) {
                DialogUtil.showCommonActionDialog(this@SignupActivity, Constants.EMPTY, getString(R.string.error_invalid_business_type), false, null)
                return false
            }
        }

        if (saveMerchant.shopNo.isNullOrEmpty()) {
            DialogUtil.showCommonActionDialog(this@SignupActivity, Constants.EMPTY, getString(R.string.error_invalid_shop_no), false, null)
            return false
        }

        if (saveMerchant.shopAddress.isNullOrEmpty()) {
            DialogUtil.showCommonActionDialog(this@SignupActivity, Constants.EMPTY, getString(R.string.error_invalid_shop_address), false, null)
            return false
        }

        if (saveMerchant.pinCode.isNullOrEmpty()
            || saveMerchant.pinCode!!.isBlank()
            || saveMerchant.pinCode!!.length != 6) {
            DialogUtil.showCommonActionDialog(this@SignupActivity, Constants.EMPTY, getString(R.string.error_invalid_pin_code), false, null)
            return false
        }

        if (saveMerchant.city.isNullOrEmpty()) {
            DialogUtil.showCommonActionDialog(this@SignupActivity, Constants.EMPTY, getString(R.string.error_invalid_city), false, null)
            return false
        }

        if (saveMerchant.state.isNullOrEmpty()) {
            DialogUtil.showCommonActionDialog(this@SignupActivity, Constants.EMPTY, getString(R.string.error_invalid_state), false, null)
            return false
        }

        if (saveMerchant.latitude.isNullOrEmpty() || saveMerchant.longitude.isNullOrEmpty()) {
            DialogUtil.showCommonActionDialog(this@SignupActivity, Constants.EMPTY, getString(R.string.error_location_not_fetched), false, null)
            return false
        }

        return true
    }

    private fun saveMerchantDetails(response: ResSaveMerchant) {
        response.merchant_id?.let {
            DOPrefs.saveMerchantId(it)
        }
        val merchantObj = viewModel.getMerchantObj()
        merchantObj.tos?.let {
            DOPrefs.saveTOS(it)
        }
        merchantObj.nameOfOwner?.let {
            DOPrefs.saveMerchantName(it)
        }
        merchantObj.msisdn?.let {
            DOPrefs.saveMSISDN(it)
        }
        DOPrefs.updateMerchantMenu(false)
        informUserForSuccessRegistration(response)
    }

    private fun launchCreateCatalogScreen() {
        val intent = Intent(this, CreateCatalogActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

}
