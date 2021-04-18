package com.digitaloutlet.viewmodel

import androidx.lifecycle.ViewModel
import com.digitaloutlet.model.bean.ProfileBean
import com.digitaloutlet.model.response.ResLogout
import com.digitaloutlet.repository.ProfileRepository

class ProfileActivityViewModel : ViewModel()/*, ProfileRepository.OnLogoutListener*/ {

    private var loader: SingleLiveEvent<Boolean>? = null
    private var errorDialog: SingleLiveEvent<String>? = null
    private var lvdProfileMenu: SingleLiveEvent<ArrayList<ProfileBean>>? = null
    private var lvdLogout: SingleLiveEvent<Boolean>? = null

    private val profileRepository = ProfileRepository()

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

    fun getProfileMenuObserver() : SingleLiveEvent<ArrayList<ProfileBean>> {
        if (lvdProfileMenu == null) {
            lvdProfileMenu = SingleLiveEvent()
        }
        return lvdProfileMenu!!
    }

    fun logoutObserver() : SingleLiveEvent<Boolean> {
        if (lvdLogout == null) {
            lvdLogout = SingleLiveEvent()
        }
        return lvdLogout!!
    }


    fun loadProfileMenu() {
        lvdProfileMenu?.value = profileRepository.getProfileMenu()
    }

    fun logout() {
        profileRepository.logout()
        lvdLogout?.value = true
    }

    /*override fun onSuccessLogout(response: ResLogout) {
        loader?.value = false
        if (response.status == 1) {
            lvdLogout?.value = true
        } else {
            errorDialog?.value = response.message
        }
    }

    override fun onSuccessFailureLogout(errMsg: String) {
        loader?.value = false
        errorDialog?.value = errMsg
    }

    override fun onFailureLogout(errMsg: String) {
        loader?.value = false
        errorDialog?.value = errMsg
    }*/
}