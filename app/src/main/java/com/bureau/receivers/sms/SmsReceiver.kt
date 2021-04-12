package com.bureau.receivers.sms

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.SmsManager
import android.telephony.SmsMessage
import android.util.Log
import android.widget.Toast


/**
 * Created by Abhin.
 */

class SmsReceiver : BroadcastReceiver() {

    var sms = SmsManager.getDefault()

    override fun onReceive(context: Context?, intent: Intent?) {
        val bundle = intent?.extras
        try {
            if (bundle != null) {
                val pdusObj : Array<Any>? = bundle["pdus"] as Array<Any>?
                for (i in pdusObj!!.indices) {
                    val currentMessage: SmsMessage = SmsMessage.createFromPdu(pdusObj[i] as ByteArray)
                    val phoneNumber: String = currentMessage.displayOriginatingAddress
                    val message: String = currentMessage.displayMessageBody
                    Toast.makeText(context, "senderNum: $phoneNumber, message: $message", Toast.LENGTH_LONG).show()
                }
            }
        } catch (e: Exception) {
            Log.e("TAG", "Exception smsReceiver$e")
        }
    }
}
