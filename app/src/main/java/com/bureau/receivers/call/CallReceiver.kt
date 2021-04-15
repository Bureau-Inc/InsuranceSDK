package com.bureau.receivers.call

import android.content.Context
import com.bureau.utils.startNumberDetectionService
import java.util.*

/**
 * Created by Abhin.
 */
class CallReceiver : PhoneCallReceiver() {

    // triggered for incoming calls
    override fun onIncomingCallStarted(context: Context?, number: String?, start: Date?) {
        context?.let {
            startNumberDetectionService(context = it, number = number, isSms = false, message = null)
        }
    }

    override fun onOutgoingCallStarted(context: Context?, number: String?, start: Date?) {
    }

    override fun onIncomingCallEnded(context: Context?, number: String?, start: Date?, end: Date?) {

    }

    override fun onOutgoingCallEnded(context: Context?, number: String?, start: Date?, end: Date?) {
    }

    override fun onMissedCall(context: Context?, number: String?, missed: Date?) {
    }
}