package com.digitaloutlet.view.fragments

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.digitaloutlet.R
import com.digitaloutlet.utils.Constants
import com.digitaloutlet.utils.DOPrefs
import com.digitaloutlet.utils.DOUtils
import com.digitaloutlet.utils.DialogUtil
import com.digitaloutlet.utils.consentsms.ConsentSMSUtil
import com.digitaloutlet.view.activities.CreateCatalogActivity
import com.digitaloutlet.view.activities.OTPActivity
import com.digitaloutlet.view.activities.SignupActivity
import com.digitaloutlet.view.base.BaseActivity
import com.digitaloutlet.view.base.BaseFragment
import com.digitaloutlet.viewmodel.OTPActivityViewModel
import com.google.android.gms.auth.api.phone.SmsRetriever

class VerifyOTPFragment : BaseFragment(), ConsentSMSUtil.OnConsentSMSListener, View.OnClickListener {

    private lateinit var mTvDesc: TextView
    private lateinit var mTvResendTimer: TextView
    private lateinit var mTvResend: TextView
    private lateinit var mEdtOTP: EditText
    private lateinit var mBtnNext: Button
    private lateinit var viewModel : OTPActivityViewModel

    private var countDownTimer: CountDownTimer? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        baseView = inflater.inflate(R.layout.fragment_verify_otp, container, false)
        return baseView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    override fun initViews() {
        viewModel = ViewModelProviders.of(requireActivity()).get(OTPActivityViewModel::class.java)

        mTvDesc = baseView.findViewById<TextView>(R.id.tv_otp_desc)
        mEdtOTP = (requireContext() as BaseActivity)
            .getEditableViewForCommonEntryLayout(baseView.findViewById(R.id.lay_mobile_no), resources.getString(R.string.otp)
                , resources.getString(R.string.enter_otp))
        mEdtOTP.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(resources.getInteger(R.integer.otp_length)))
        mEdtOTP.inputType = InputType.TYPE_NUMBER_VARIATION_PASSWORD or InputType.TYPE_CLASS_NUMBER
        mTvResendTimer = baseView.findViewById(R.id.tv_resend_otp_counter)
        mTvResend = baseView.findViewById(R.id.tv_resend_otp)
        mBtnNext = baseView.findViewById(R.id.btn_next)

        if (viewModel.isRegisteredMerchant()) {
            mBtnNext.setText(getString(R.string.submit))
        }
    }

    override fun initListeners() {
        mTvResend.setOnClickListener(this)
        mBtnNext.setOnClickListener(this)

        mEdtOTP.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {

            }

            override fun beforeTextChanged(charSeq: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(charSeq: CharSequence?, p1: Int, p2: Int, p3: Int) {
                enableSubmitOTP()
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

        viewModel.observerVerifyOtp().observe(viewLifecycleOwner, object : Observer<Boolean> {
            override fun onChanged(value: Boolean) {
                saveMerchantDetailsAndProceed()
            }
        })

        viewModel.observerGenerateOtp().observe(viewLifecycleOwner, object : Observer<String> {
            override fun onChanged(message: String) {
                initResendTimer()
            }
        })

        initResendTimer()

        val strClickableValue = resources.getString(R.string.edit_number)
        var strDescText = resources.getString(R.string.sent_otp_on) +
                Constants.SPACE + viewModel.getMSISDN() + "." +
                Constants.SPACE + resources.getString(R.string.enter_wrong_no) +
                Constants.SPACE + strClickableValue

        val spannable = SpannableString(strDescText)
        spannable.setSpan(
            object : ClickableSpan() {
                override fun onClick(view: View) {
                    (requireActivity() as OTPActivity).onBackPressed()
                }

                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.color = ContextCompat.getColor(requireContext(), R.color.colorEnabled)
                    ds.isUnderlineText = false
                }
            },
            strDescText.indexOf(strClickableValue), strDescText.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        mTvDesc.text = spannable
        mTvDesc.movementMethod = LinkMovementMethod.getInstance()
        mTvDesc.highlightColor = Color.TRANSPARENT
    }

    private fun initResendTimer() {
        mTvResendTimer.visibility = View.VISIBLE
        mTvResend.visibility = View.GONE


        if (countDownTimer == null) {
            countDownTimer = object : CountDownTimer(30000, 1000) {
                override fun onFinish() {
                    mTvResendTimer.visibility = View.GONE
                    mTvResend.visibility = View.VISIBLE
                }

                override fun onTick(millisUntilFinished: Long) {
                    mTvResendTimer.text = getString(R.string.resend_in) + Constants.SPACE + millisUntilFinished / 1000
                }

            }
        }
        countDownTimer?.start()
    }

    override fun onStart() {
        super.onStart()
        ConsentSMSUtil.setListener(this)
        ConsentSMSUtil.registerConsentReceiver(this)
    }

    override fun onResume() {
        super.onResume()
        ConsentSMSUtil.startConsentSMS()
    }

    override fun onStop() {
        super.onStop()
        ConsentSMSUtil.unregisterConsentReceiver()
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
        countDownTimer = null
    }

    private fun enableSubmitOTP() {
        mBtnNext.isEnabled = mEdtOTP.text.toString().length == resources.getInteger(R.integer.otp_length)
    }

    override fun onFailureSMS(errorMsg: String) {

    }

    override fun onConsentApiCallFailure(strError: String) {

    }

    override fun onConsentApiCallSuccess() {

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ConsentSMSUtil.REQ_USER_CONSENT) {
            var oneTimeCode = Constants.EMPTY
            if (resultCode == Activity.RESULT_OK) {

                val message = data?.let {
                    it.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE)
                } ?: Constants.EMPTY

                if (message.trim().isNotEmpty() && message.contains(Constants.hash_code)) {
                    oneTimeCode = DOUtils.parseOneTimeCode(message)
                }
            }
            setOtp(oneTimeCode)
        }
    }

    private fun setOtp(oneTimeCode: String) {
        if (oneTimeCode.trim().isNotEmpty()) {
            mEdtOTP.setText(oneTimeCode)
            viewModel.verifyOtp(mEdtOTP.text.toString())
        }
    }

    override fun onClick(view: View?) {
        when(view?.id) {
            R.id.tv_resend_otp -> {
                mEdtOTP.setText(Constants.EMPTY)
                viewModel.triggerOTP()
            }

            R.id.btn_next -> {
                viewModel.verifyOtp(mEdtOTP.text.toString())
            }
        }
    }

    private fun saveMerchantDetailsAndProceed() {
        /*var className: Class<*> = OTPActivity::class.java
        if (!viewModel.mIsRegisteredMerchant) {
            className = SignupActivity::class.java
        }*/

        if (viewModel.isRegisteredMerchant()) {
            DOPrefs.saveMerchantId(viewModel.getMerchantId())
            DOPrefs.saveMSISDN(viewModel.getMSISDN())
            DOPrefs.updateMerchantMenu(true)
            (requireContext() as BaseActivity).launchDashboard()
            return
        }
        launchSignupScreen()
        requireActivity().finish()
    }

    private fun launchSignupScreen() {
        val intent = Intent(requireContext(), SignupActivity::class.java)
        intent.putExtra(Constants.INTENT_MSISDN, viewModel.getMSISDN())
        startActivity(intent)
    }

    private fun launchCategoryScreen() {
        val intent = Intent(requireContext(), CreateCatalogActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK/* or Intent.FLAG_ACTIVITY_NEW_TASK*/)
        startActivity(intent)
        (requireContext() as OTPActivity).finish()
    }

}