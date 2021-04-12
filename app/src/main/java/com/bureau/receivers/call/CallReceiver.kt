package com.bureau.receivers.call

import android.content.Context
import android.util.Log
import com.bureau.utils.startNumberDetectionService
import com.bureau.utils.startScreeningService
import java.util.*

/**
 * Created by Abhin.
 */
class CallReceiver : PhoneCallReceiver() {
    
    override fun onIncomingCallStarted(ctx: Context?, number: String?, start: Date?) {
        Log.e("TAG", "onIncomingCallReceived() --> InComingCall number --> $number")
        /*if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            Log.e("TAG","onIncomingCallReceived() --> Naugth")
            startScreeningService(ctx!!)
        } else {
            Log.e("TAG","onIncomingCallReceived() --> number detection")
            startNumberDetectionService(ctx!!, number)
        }*/
    }

    override fun onOutgoingCallStarted(ctx: Context?, number: String?, start: Date?) {
        Log.e("TAG","onOutgoingCallStarted() --> ")
    }

    override fun onIncomingCallEnded(ctx: Context?, number: String?, start: Date?, end: Date?) {
        Log.e("TAG","onIncomingCallEnded() --> ")
    }

    override fun onOutgoingCallEnded(ctx: Context?, number: String?, start: Date?, end: Date?) {
        Log.e("TAG","onOutgoingCallEnded() --> ")
    }

    override fun onMissedCall(ctx: Context?, number: String?, missed: Date?) {
        Log.e("TAG","onMissedCall() --> ")
    }
}