package com.digitaloutlet.view.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.digitaloutlet.R
import com.digitaloutlet.utils.Constants
import com.digitaloutlet.utils.DOUtils
import com.digitaloutlet.utils.DialogUtil
import com.digitaloutlet.utils.consentsms.ConsentSMSUtil
import com.digitaloutlet.view.activities.OTPActivity
import com.digitaloutlet.view.base.BaseActivity
import com.digitaloutlet.view.base.BaseFragment
import com.digitaloutlet.viewmodel.OTPActivityViewModel
import com.google.android.gms.auth.api.credentials.Credential

class GenerateOTPFragment : BaseFragment()
    , ConsentSMSUtil.OnConsentSMSListener, View.OnClickListener {

    private lateinit var mBtnGenerateOTP: Button
    private lateinit var mEdtMobileNo: EditText

    private lateinit var viewModel : OTPActivityViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        baseView = inflater.inflate(R.layout.fragament_generate_otp, container, false)
        return baseView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    override fun initViews() {
        viewModel = ViewModelProviders.of(requireActivity()).get(OTPActivityViewModel::class.java)

        mBtnGenerateOTP = baseView.findViewById(R.id.btn_generate_otp)
        (baseView.findViewById<TextView>(R.id.tv_login_desc)).text = resources.getString(R.string.your_online_to_offline_experience)

        if (viewModel.isLoginJourney()) {
            (baseView.findViewById<TextView>(R.id.tv_login_title)).text = resources.getString(R.string.login_to_digital_world)
        } else {
            (baseView.findViewById<TextView>(R.id.tv_login_title)).text = resources.getString(R.string.signup_to_digital_world)
        }

        mEdtMobileNo = (requireContext() as BaseActivity)
            .getEditableViewForCommonEntryLayout(baseView.findViewById(R.id.lay_mobile_no))
    }

    override fun initListeners() {
        mBtnGenerateOTP.setOnClickListener(this)

        mEdtMobileNo.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {

            }

            override fun beforeTextChanged(charSeq: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(charSeq: CharSequence?, p1: Int, p2: Int, p3: Int) {
                enableGenerateOtp()
            }
        })
    }

    override fun initData() {
        viewModel.showLoader().observe(viewLifecycleOwner, object : Observer<Boolean> {
            override fun onChanged(value: Boolean) {
                if (value) {
                    (requireContext() as OTPActivity).showLoader()
                } else {
                    (requireContext() as OTPActivity).dismissLoader()
                }
            }
        })

        viewModel.showErrorDialog().observe(viewLifecycleOwner, object : Observer<String> {
            override fun onChanged(message: String) {
                DialogUtil.showCommonActionDialog(requireContext(), Constants.EMPTY, message, false, null)
            }
        })

        viewModel.observerCheckMerchantStatus().observe(viewLifecycleOwner, object : Observer<Boolean> {
            override fun onChanged(value: Boolean) {
                isUserConsentRequired()
            }
        })

        viewModel.observerGenerateOtp().observe(viewLifecycleOwner, object : Observer<String> {
            override fun onChanged(message: String) {
                launchVerifyOTPScreen()
            }
        })


        if (viewModel.getMSISDN().isNotEmpty()) {
            mEdtMobileNo.setText(viewModel.getMSISDN())
        } else {
            DOUtils.requestHint(this)
        }
    }

    private fun isUserConsentRequired() {
        var isUserConsentRequired = false;
        var message = Constants.EMPTY
        if (!viewModel.isRegisteredMerchant() && viewModel.isLoginJourney()) {
            isUserConsentRequired = true
            message = getString(R.string.merchant_not_registered)
        } else if (viewModel.isRegisteredMerchant() && !viewModel.isLoginJourney()) {
            isUserConsentRequired = true
            message = getString(R.string.merchant_already_registered)
        }

        if (isUserConsentRequired) {
            DialogUtil.showCommonActionDialog(requireContext(), Constants.EMPTY, message
                , true,
                object : DialogUtil.DailogCallback {
                    override fun onEventOK() {
                        viewModel.triggerOTP()
                    }

                    override fun onEventCancel() {

                    }
                })
            return
        }

        viewModel.triggerOTP()
    }

    private fun enableGenerateOtp() {
        mBtnGenerateOTP.isEnabled = mEdtMobileNo.text.trim().length >= 10
    }

    override fun onFailureSMS(errorMsg: String) {

    }

    override fun onConsentApiCallFailure(strError: String) {

    }

    override fun onConsentApiCallSuccess() {

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == DOUtils.CREDENTIAL_PICKER_REQUEST) {
            var phoneNumber = Constants.EMPTY
            if (resultCode == Activity.RESULT_OK) {
                data?.let {
                    phoneNumber = (data.getParcelableExtra<Credential>(Credential.EXTRA_KEY)).id
                }
            }

            //viewModel.setMSISDN(DOUtils.validateMobileNumber(phoneNumber))
            phoneNumber = phoneNumber.replace(Constants.PLUS_91, Constants.EMPTY)
            mEdtMobileNo.setText(phoneNumber)
            //enableGenerateOtp()
        }
    }

    override fun onClick(view: View?) {
        when(view?.id) {
            R.id.btn_generate_otp -> {
                if (DOUtils.validateMobileNumber(mEdtMobileNo.text.toString()).length != 10) {
                    (requireActivity() as BaseActivity)
                        .showToastMessage(resources.getString(R.string.error_enter_valid_10_digit_mobile_no))
                    return
                }

                //viewModel.setMSISDN(mEdtMobileNo.text.toString())
                viewModel.generateOTP(mEdtMobileNo.text.toString())
                //viewModel.checkMerchantStatus()
            }
        }
    }

    private fun launchVerifyOTPScreen() {
        (requireActivity() as OTPActivity).addFragment(VerifyOTPFragment())
    }
}