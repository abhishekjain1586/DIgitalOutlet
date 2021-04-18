package com.digitaloutlet.viewmodel

import androidx.lifecycle.ViewModel
import com.digitaloutlet.model.response.ResCheckMerchantStatus
import com.digitaloutlet.model.response.ResGenerateOTP
import com.digitaloutlet.model.response.ResValidateOTP
import com.digitaloutlet.repository.CheckMerchantStatusRepository
import com.digitaloutlet.repository.GenerateOTPRepository
import com.digitaloutlet.repository.ValidateOTPRepository
import com.digitaloutlet.utils.Constants

class OTPActivityViewModel : ViewModel(),
    CheckMerchantStatusRepository.OnCheckMerchantStatusListener, GenerateOTPRepository.OnGenerateOTPListener
    , ValidateOTPRepository.OnVerifyOTPListener {

    private var mMSISDN: String = Constants.EMPTY
    private var mIsLoginJourney: Boolean = false
    private var mIsRegisteredMerchant: Boolean = false

    private var mMerchantId: String? = null

    private var loader: SingleLiveEvent<Boolean>? = null
    private var lvdGenerateOtp: SingleLiveEvent<String>? = null
    private var errorDialog: SingleLiveEvent<String>? = null
    private var lvdVerifyOtp: SingleLiveEvent<Boolean>? = null
    private var lvdCheckMerchantStatus: SingleLiveEvent<Boolean>? = null

    private var mCheckMerchantStatusRepository = CheckMerchantStatusRepository()
    private var mGenerateOTPRepository = GenerateOTPRepository()
    private var mValidateOTPRepository = ValidateOTPRepository()


    fun showLoader() : SingleLiveEvent<Boolean> {
        if (loader == null) {
            loader = SingleLiveEvent<Boolean>()
        }

        return loader!!
    }

    fun showErrorDialog() : SingleLiveEvent<String> {
        if (errorDialog == null) {
            errorDialog = SingleLiveEvent<String>()
        }

        return errorDialog!!
    }

    fun setIsLoginJourney(isLoginJourney: Boolean) {
        mIsLoginJourney = isLoginJourney
    }

    fun isLoginJourney(): Boolean {
        return mIsLoginJourney
    }

    fun isRegisteredMerchant(): Boolean {
        return mIsRegisteredMerchant
    }

    fun getMerchantId(): String {
        return mMerchantId!!
    }

    fun observerGenerateOtp(): SingleLiveEvent<String> {
        if (lvdGenerateOtp == null) {
            lvdGenerateOtp = SingleLiveEvent<String>()
        }

        return lvdGenerateOtp!!
    }

    fun observerVerifyOtp(): SingleLiveEvent<Boolean> {
        if (lvdVerifyOtp == null) {
            lvdVerifyOtp = SingleLiveEvent<Boolean>()
        }

        return lvdVerifyOtp!!
    }

    fun observerCheckMerchantStatus(): SingleLiveEvent<Boolean> {
        if (lvdCheckMerchantStatus == null) {
            lvdCheckMerchantStatus = SingleLiveEvent<Boolean>()
        }

        return lvdCheckMerchantStatus!!
    }

    private fun checkMerchantStatus() {
        loader?.value = true
        mCheckMerchantStatusRepository.setListener(this)
        mCheckMerchantStatusRepository.checkMerchantRegistration(mMSISDN)
    }

    override fun onSuccessCheckMerchantStatus(response: ResCheckMerchantStatus) {
        loader?.value = false
        mIsRegisteredMerchant = false
        if (response.status == 1) {
            mMerchantId = response.merchant_id
            mIsRegisteredMerchant = true
        }
        lvdCheckMerchantStatus?.value = true
        //isUserConsentRequired()
    }

    override fun onFailureCheckMerchantStatus(errMsg: String) {
        loader?.value = false
        errorDialog?.value = errMsg
    }

    /*private fun isUserConsentRequired() {
        if (!mIsRegisteredMerchant && mIsLoginJourney) {
            lvdUserConsentToProceed?.value = DOApplication._INSTANCE.getString(R.string.merchant_not_registered)
        } else if (mIsRegisteredMerchant && !mIsLoginJourney) {
            lvdUserConsentToProceed?.value = DOApplication._INSTANCE.getString(R.string.merchant_already_registered)
        } else {
            triggerOTP()
        }
    }*/

    fun triggerOTP() {
        loader?.value = true
        mGenerateOTPRepository.setListener(this)
        mGenerateOTPRepository.generateOTP(mMSISDN)
    }

    override fun onSuccessGenerateOTP(response: ResGenerateOTP) {
        loader?.value = false

        if (response.status == 1) {
            lvdGenerateOtp?.value = response.message
        } else {
            errorDialog?.value = response.message
        }
    }

    override fun onFailureGenerateOTP(errMsg: String) {
        loader?.value = false
        errorDialog?.value = errMsg
    }

    fun verifyOtp(otp: String) {
        loader?.value = true
        mValidateOTPRepository.setListener(this)
        mValidateOTPRepository.verifyOTP(mMSISDN, otp)
    }

    fun getMSISDN(): String {
        return mMSISDN
    }

    fun generateOTP(msisdn: String) {
        mMSISDN = msisdn
        checkMerchantStatus()
    }

    override fun onSuccessVerifyOTP(response: ResValidateOTP) {
        loader?.value = false
        if (response.status == 1) {
            lvdVerifyOtp?.value = true
            return
        }
        errorDialog?.value = response.message
    }

    override fun onFailureVerifyOTP(errMsg: String) {
        loader?.value = false
        errorDialog?.value = errMsg
    }
}