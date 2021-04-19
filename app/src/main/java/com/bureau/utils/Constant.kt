package com.bureau.utils

/**
 * Created by Abhin.
 */

const val KEY_NUMBER = "number"
const val KEY_API_CALL_TYPE = "KEY_API_CALL_TYPE"
const val KEY_SMS_BODY = "KEY_SMS_BODY"
const val KEY_PACKAGE_DATA = "KEY_PACKAGE_DATA"
const val MY_PREFERENCE = "MyPreference"
const val MY_PERMISSIONS_REQUEST_READ_CALL_LOG = 0
const val MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 1
const val MY_PERMISSIONS_REQUEST_PROCESS_OUTGOING_CALLS = 2
const val PREF_USER_MOBILE = "PREF_USER_MOBILE"
const val PERMISSIONS_REQUEST_CODE = 10003

val mBlackList = arrayListOf("1111111111", "2222222222", "3333333333", "4444444444", "5555555555")
val mWhiteList = arrayListOf("1231231231", "1122334455", "1112223334", "1111222233", "1111122222")

enum class ApiCallType {
    SMS, CALL, PACKAGE
}