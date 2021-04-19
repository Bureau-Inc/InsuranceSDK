package com.bureau.receivers.sim

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.telephony.TelephonyManager
import android.text.TextUtils
import android.util.Log


/**
 * Created by Abhin.
 */

class SimChangeReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        if (ACTION_SIM_STATE_CHANGED == action) {
            val extras = intent.extras
            printExtras(extras)
            val state = extras!!.getString(EXTRA_SIM_STATE)
            Log.w(TAG, "SIM Action : $action / State : $state")
            // Test phoneName = GSM ?
            if (SIM_STATE_LOADED == state) {
                // Read Phone number
                val phoneNumber = getSystemPhoneNumber(context)
                if (TextUtils.isEmpty(phoneNumber)) {
                    Log.e(TAG, "EventSpy : No phone number Readable in TelephonyManager")
                } else {
                    // Compare to preference
                    val prefs = PreferenceManager.getDefaultSharedPreferences(context)
                    val prefPhone = prefs.getString("spyEventSimChangePhoneNumber", null)
                    if (TextUtils.isEmpty(prefPhone)) {
                        // Save as New Phone
                        savePrefsPhoneNumber(prefs, phoneNumber)
                        Log.w(TAG, "EventSpy Register for SIM Change Listener Phone Number : $phoneNumber")
                        // TODO Need to notify ?
                    } else if (prefPhone != phoneNumber) {
                        Log.w(TAG, "EventSpy SIM Change for new Phone Number : $phoneNumber")
                        // Send message
                        sendSpyNotifSms(context, prefPhone, phoneNumber)
                        // Save as New Phone
                        // NO savePrefsPhoneNumber(prefs, phoneNumber);
                    }
                }
            }
        }
    }

    private fun sendSpyNotifSms(context: Context, prefPhone: String?, phoneNumber: String) {
        Log.e("TAG", "sendSpyNotifSms() --> prefPhone $prefPhone, phoneNumber $phoneNumber")
    }

    private fun printExtras(extras: Bundle?) {
        if (extras != null) {
            for (key in extras.keySet()) {
                val value = extras[key]
                Log.d(TAG, "EventSpy SIM extras : $key = $value")
            }
        }
    }

    companion object {
        private const val TAG = "SimChangeReceiver"

        /**
         * com.android.internal.telephony.TelephonyIntents.ACTION_SIM_STATE_CHANGED
         *
         * Broadcast Action: The sim card state has changed. The intent will have
         * the following extra values:
         *
         *  * *phoneName* - A string version of the phone name.
         *  * *ss* - The sim state. One of `"ABSENT"`
         * `"LOCKED"` `"READY"` `"ISMI"`
         * `"LOADED"`
         *  * *reason* - The reason while ss is LOCKED, otherwise is null
         * `"PIN"` locked on PIN1 `"PUK"` locked on PUK1
         * `"NETWORK"` locked on Network Personalization
         *
         *
         *
         *
         * Requires the READ_PHONE_STATE permission.
         *
         *
         *
         * This is a protected intent that can only be sent by the system.
         */
        private const val ACTION_SIM_STATE_CHANGED = "android.intent.action.SIM_STATE_CHANGED"
        private const val EXTRA_SIM_STATE = "ss"
        private const val SIM_STATE_LOADED = "LOADED"

        @SuppressLint("MissingPermission")
        private fun getSystemPhoneNumber(context: Context): String {
            // Read Phone number
            val telephoneMgr = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            val phoneNumber = telephoneMgr.line1Number
            Log.d(TAG, "EventSpy SIM Operator : " + telephoneMgr.simOperator) // Code IMEI
            Log.d(TAG, "EventSpy SIM Network Operator Name : " + telephoneMgr.networkOperatorName)
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                Log.d(TAG, "EventSpy SIM Carrier Number : " + telephoneMgr.simCarrierIdName)
            }
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                Log.d(TAG, "EventSpy SIM Subscription ID : " + telephoneMgr.subscriptionId)
            }
            Log.d(TAG, "EventSpy SIM PhoneNumber : $phoneNumber") // Code IMEI
            return phoneNumber
        }

        fun savePrefsPhoneNumber(prefs: SharedPreferences, phoneNumber: String) {
            if (!TextUtils.isEmpty(phoneNumber)) {
                val prefEditor = prefs.edit()
                prefEditor.putString("spyEventSimChangePhoneNumber", phoneNumber)
                prefEditor.commit()
                Log.d(TAG, "Register Sim Change Phone : $phoneNumber")
            } else {
                Log.w(TAG, "No Phone number to save in pref Key : " + "spyEventSimChangePhoneNumber")
            }
        }
    }
}