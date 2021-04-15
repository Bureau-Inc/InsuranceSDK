package com.bureau.receivers.sms

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.SmsMessage
import android.util.Log
import android.widget.Toast
import com.bureau.utils.MY_PREFERENCE
import com.bureau.utils.PreferenceManager
import com.bureau.utils.startNumberDetectionService


/**
 * Created by Abhin.
 */

class SmsReceiver : BroadcastReceiver() {

    private var preferenceManager: PreferenceManager? = null

    override fun onReceive(context: Context?, intent: Intent?) {
        preferenceManager = PreferenceManager(context!!.getSharedPreferences(MY_PREFERENCE, Context.MODE_PRIVATE))
        val bundle = intent?.extras
        try {
            if (bundle != null) {
                val pdusObj: Array<Any>? = bundle["pdus"] as Array<Any>?
                for (i in pdusObj!!.indices) {
                    val currentMessage: SmsMessage = SmsMessage.createFromPdu(pdusObj[i] as ByteArray)
                    val phoneNumber: String = currentMessage.displayOriginatingAddress
                    val message: String = currentMessage.displayMessageBody
                    startNumberDetectionService(context = context, number = phoneNumber, isSms = true, message = message)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
