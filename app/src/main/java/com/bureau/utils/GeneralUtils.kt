package com.bureau.utils

import android.Manifest
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract.PhoneLookup
import androidx.core.app.ActivityCompat
import com.bureau.services.NumberDetectionService


/**
 * Created by Abhin.
 */

var callSmsReceive: CallSmsReceiverInterface? = null

interface CallSmsReceiverInterface{
    fun detectedNumber(number: String? = null)
    fun spam()
    fun aggravated()
}

// check the service are running or not
fun isMyServiceRunning(
    context: Context?,
    serviceClass: Class<*>
): Boolean {
    val manager = context!!.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager?
    for (service in manager!!.getRunningServices(Int.MAX_VALUE)) {
        if (serviceClass.name == service.service.className) {
            return true
        }
    }
    return false
}

val phoneCallPermission = arrayOf(Manifest.permission.READ_CONTACTS, Manifest.permission.READ_PHONE_STATE, Manifest.permission.CALL_PHONE, Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_BOOT_COMPLETED, Manifest.permission.PROCESS_OUTGOING_CALLS, Manifest.permission.ACCESS_NETWORK_STATE)

fun hasPermissions(
    context: Context?,
    permissions: Array<String>?
): Boolean {
    if (context != null && !permissions.isNullOrEmpty()) {
        for (permission in permissions) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
    }
    return true
}

fun contactExists(context: Context, number: String?): Boolean {
    /// number is the phone number
    val lookupUri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number))
    val mPhoneNumberProjection = arrayOf(PhoneLookup._ID, PhoneLookup.NUMBER, PhoneLookup.DISPLAY_NAME)
    val cur = context.contentResolver.query(lookupUri, mPhoneNumberProjection, null, null, null)
    cur.use { cursor ->
        if (cursor!!.moveToFirst()) {
            cursor.close()
            return true
        }
    }
    return false
}

fun startNumberDetectionService(context: Context, number: String? = null) {
    if (!isMyServiceRunning(context, NumberDetectionService::class.java)) {
        NumberDetectionService.startService(
            context, Intent(context, NumberDetectionService::class.java).apply {
                putExtras(Bundle().apply {
                    putString(KEY_NUMBER, number)
                })
            }
        )
    }
}