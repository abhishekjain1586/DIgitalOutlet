package com.digitaloutlet.viewmodel

import androidx.lifecycle.ViewModel
import com.digitaloutlet.R
import com.digitaloutlet.application.DOApplication
import com.digitaloutlet.model.bean.SaveMerchantBean
import com.digitaloutlet.model.response.ResSaveMerchant
import com.digitaloutlet.model.response.ResTOS
import com.digitaloutlet.model.response.TypeOfShop
import com.digitaloutlet.repository.SaveMerchantRepository
import com.digitaloutlet.repository.TOSRepository
import com.digitaloutlet.utils.Constants

class SignUpActivityViewModel : ViewModel(), TOSRepository.OnTOSListener, SaveMerchantRepository.OnSaveMerchantListener {

    private var loader: SingleLiveEvent<Boolean>? = null
    private var errorDialog: SingleLiveEvent<String>? = null
    private var lvdSelectedTOS: SingleLiveEvent<TypeOfShop>? = null
    private var lvdSaveMerchant: SingleLiveEvent<ResSaveMerchant>? = null

    private var repoTOS = TOSRepository()
    private var repoSaveMerchant: SaveMerchantRepository? = null

    private var mTOSLst: ArrayList<TypeOfShop>? = null
    private var mMerchantObj: SaveMerchantBean? = null
    private var mSelectedTOS: TypeOfShop? = null

    fun showLoader() : SingleLiveEvent<Boolean> {
        if (loader == null) {
            loader = SingleLiveEvent()
        }

        return loader!!
    }

    fun showErrorDialog() : SingleLiveEvent<String> {
        if (errorDialog == null) {
            errorDialog = SingleLiveEvent()
        }

        return errorDialog!!
    }

    fun observerSelectedTOS(): SingleLiveEvent<TypeOfShop> {
        if (lvdSelectedTOS == null) {
            lvdSelectedTOS = SingleLiveEvent()
        }
        return lvdSelectedTOS!!
    }

    fun getTypeOfShops() {
        loader?.value = true
        repoTOS.setListener(this)
        repoTOS.getTOS()
    }

    fun getTOSList(): ArrayList<TypeOfShop>? {
        return mTOSLst
    }

    override fun onSuccessTOS(response: ResTOS) {
        loader?.value = false

        if (response.status == 1) {
            val tosLst = response.records
            if (!tosLst.isNullOrEmpty()) {
                if (mTOSLst == null) {
                    mTOSLst = ArrayList()
                }
                mTOSLst?.clear()
                mTOSLst?.addAll(tosLst)
            } else {
                errorDialog?.value = response.message
            }
        } else {
            errorDialog?.value = response.message
        }
    }

    override fun onFailureTOS(errMsg: String) {
        loader?.value = false
        errorDialog?.value = errMsg
    }

    fun setSelectedTypeOfShop(typeOfShop: TypeOfShop) {
        typeOfShop.isSelected = true
        mSelectedTOS = typeOfShop
        mTOSLst?.let {
            for (obj in it) {
                obj.isSelected = obj.id == typeOfShop.id
            }
            lvdSelectedTOS?.value = typeOfShop
        }
    }

    fun getSelectedTOS(): TypeOfShop? {
        return mSelectedTOS
    }

    fun saveMerchant(): SingleLiveEvent<ResSaveMerchant> {
        if (lvdSaveMerchant == null) {
            lvdSaveMerchant = SingleLiveEvent()
        }

        if (repoSaveMerchant == null) {
            repoSaveMerchant = SaveMerchantRepository()
            repoSaveMerchant?.setListener(this)
        }

        return lvdSaveMerchant!!
    }

    fun saveMerchant(saveMerchant: SaveMerchantBean) {
        loader?.value = true
        mMerchantObj = saveMerchant
        repoSaveMerchant?.saveMerchant(saveMerchant)
    }

    fun getMerchantObj(): SaveMerchantBean {
        return mMerchantObj!!
    }

    override fun onSuccessSaveMerchant(response: ResSaveMerchant) {
        loader?.value = false
        val merchantId: String = response.merchant_id ?: Constants.EMPTY
        if (merchantId.isNullOrEmpty()) {
            errorDialog?.value = DOApplication._INSTANCE.getString(R.string.error_merchant_not_created)
            return
        }
        lvdSaveMerchant?.value = response
    }

    override fun onFailureSaveMerchant(errMsg: String) {
        loader?.value = false
        errorDialog?.value = errMsg
    }
}