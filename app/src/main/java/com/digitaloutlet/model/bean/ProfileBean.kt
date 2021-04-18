package com.digitaloutlet.model.bean

class ProfileBean {

    companion object {
        const val EDIT_PROFILE = 1;
        const val RAISE_REQUEST = 2;
        const val MY_REQUEST = 3;
        const val LOGOUT = 4;
        const val PRODUCT_HISTORY = 5;
        const val DELETE_ACCOUNT = 6;
    }

    constructor(name: String, value: Int, image: Int) {
        this.name = name
        this.value = value
        this.image = image
    }

    var name: String? = null
    var value: Int? = null
    var image: Int? = null
}