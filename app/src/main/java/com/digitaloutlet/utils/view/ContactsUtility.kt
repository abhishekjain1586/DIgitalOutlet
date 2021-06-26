package com.digitaloutlet.utils.view

import android.content.Context
import android.database.Cursor
import android.provider.ContactsContract
import com.digitaloutlet.model.bean.ContactsBean

class ContactsUtility private constructor(context: Context){

    var mContext = context

    companion object {
        var INSTANCE: ContactsUtility? = null
        fun getInstance(context: Context): ContactsUtility {
            if (INSTANCE == null) {
                INSTANCE = ContactsUtility(context)
            }
            return INSTANCE!!
        }
    }

    fun getContacts(): ArrayList<ContactsBean> {
        val contactsLst = ArrayList<ContactsBean>()
        val resolver = mContext.contentResolver
        val cur = resolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null)
        if ((if (cur != null) cur.getCount() else 0) > 0) {
            while (cur != null && cur.moveToNext()) {
                val id: String = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID))
                val name: String = cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME))
                if (cur.getInt(cur.getColumnIndex(
                                ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    val pCur: Cursor = resolver.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", arrayOf(id), null) as Cursor
                    while (pCur.moveToNext()) {
                        val phoneNo: String = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER))
                        val contactsObj = ContactsBean()
                        contactsObj.name = name
                        contactsObj.phoneNumber = phoneNo
                        contactsLst.add(contactsObj)
                    }
                    pCur.close()
                }
            }
        }
        cur?.close()

        return contactsLst
    }
}