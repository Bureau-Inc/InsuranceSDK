package com.bureau.receivers.call

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import com.bureau.utils.hasPermissions
import java.util.*


/**
 * Created by Abhin.
 */
abstract class PhoneCallReceiver : BroadcastReceiver() {

    // The receiver will be recreated whenever android feels like it.
    // We need a static variable to remember data between instantiations
    private var lastState = TelephonyManager.CALL_STATE_IDLE
    private var callStartTime: Date? = null
    private var isIncoming = false
    private var savedNumber // because the passed incoming is only valid in ringing
            : String? = null

    override fun onReceive(context: Context, intent: Intent) {
        if (hasPermissions(context, arrayOf(Manifest.permission.READ_PHONE_STATE))) {
            // We listen to two intents. The new outgoing call only tells us of an outgoing call.
            // We use it to get the number.
            if (intent.action == "android.intent.action.NEW_OUTGOING_CALL") {
                savedNumber = intent.extras!!.getString("android.intent.extra.PHONE_NUMBER")
            } else {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
                    val stateStr = intent.extras!!.getString(TelephonyManager.EXTRA_STATE)
                    val number = intent.extras!!.getString(TelephonyManager.EXTRA_INCOMING_NUMBER)
                    var state = 0
                    when (stateStr) {
                        TelephonyManager.EXTRA_STATE_IDLE -> {
                            state = TelephonyManager.CALL_STATE_IDLE
                        }
                        TelephonyManager.EXTRA_STATE_OFFHOOK -> {
                            state = TelephonyManager.CALL_STATE_OFFHOOK
                        }
                        TelephonyManager.EXTRA_STATE_RINGING -> {
                            state = TelephonyManager.CALL_STATE_RINGING
                        }
                    }
                    onCustomCallStateChanged(context, state, number)
                } else {
                    // Android 9+
                    val telephony = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                    telephony.listen(object : PhoneStateListener() {
                        override fun onCallStateChanged(state: Int, number: String) {
                            onCustomCallStateChanged(context, state, number)
                        }
                    }, PhoneStateListener.LISTEN_CALL_STATE)
                }
            }
        }
    }

    // Derived classes should override these to respond to specific events of interest
    protected abstract fun onIncomingCallStarted(context: Context?, number: String?, start: Date?)

    protected abstract fun onOutgoingCallStarted(context: Context?, number: String?, start: Date?)

    protected abstract fun onIncomingCallEnded(context: Context?, number: String?, start: Date?, end: Date?)

    protected abstract fun onOutgoingCallEnded(context: Context?, number: String?, start: Date?, end: Date?)

    protected abstract fun onMissedCall(context: Context?, number: String?, missed: Date?)

    // Deals with actual events

    // Deals with actual events
    // Incoming call - goes from IDLE to RINGING when it rings, to OFFHOOK when it's answered, to IDLE when its hung up
    // Outgoing call - goes from IDLE to OFFHOOK when it dials out, to IDLE when hung up
    open fun onCustomCallStateChanged(
        context: Context?,
        state: Int,
        number: String?
    ) {
        if (lastState == state) {
            // No change, debounce extras
            return
        }
        when (state) {
            TelephonyManager.CALL_STATE_RINGING -> {
                isIncoming = true
                callStartTime = Date()
                savedNumber = number
                //                Log.e("TAG", "number >>$number")
                onIncomingCallStarted(context, number, callStartTime)
            }
            TelephonyManager.CALL_STATE_OFFHOOK ->                 // Transition of ringing->offhook are pickups of incoming calls. Nothing done on them
                if (lastState != TelephonyManager.CALL_STATE_RINGING) {
                    isIncoming = false
                    callStartTime = Date()
                    onOutgoingCallStarted(context, savedNumber, callStartTime)
                }
            TelephonyManager.CALL_STATE_IDLE ->                 // Went to idle - this is the end of a call.  What type depends on previous state(s)
                when {
                    lastState == TelephonyManager.CALL_STATE_RINGING -> {
                        // Ring but no pickup - a miss
                        onMissedCall(context, savedNumber, callStartTime)
                    }
                    isIncoming -> {
                        onIncomingCallEnded(context, savedNumber, callStartTime, Date())
                    }
                    else -> {
                        onOutgoingCallEnded(context, savedNumber, callStartTime, Date())
                    }
                }
        }
        lastState = state
    }
}