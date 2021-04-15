package com.bureau.services

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.widget.Toast
import com.bureau.models.callFilter.request.CallFilterRequest
import com.bureau.models.callFilter.request.SmsFilterRequest
import com.bureau.network.APIClient
import com.bureau.utils.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


/**
 * Created by Abhin.
 */

class NumberDetectionService : Service() {

    private var number: String? = null
    private var isSms: Boolean? = null
    private var smsTextBody: String? = null

    // start the service if it is not already running.
    companion object {
        var callSmsReceiverListener: CallSmsReceiverInterface? = null
        private var preferenceManager: PreferenceManager? = null

        fun init(userNumber: String, callSmsReceive: CallSmsReceiverInterface? = null) {
            this.callSmsReceiverListener = callSmsReceive
            preferenceManager?.setValue(PREF_USER_MOBILE, userNumber)
        }

        fun startService(context: Context, intent: Intent) {
            try {
                if (!isMyServiceRunning(context, NumberDetectionService::class.java)) {
                    context.startService(intent)
                }
            } catch (e: IllegalStateException) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(intent)
                } else {
                    throw IllegalStateException()
                }
            }
        }
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        preferenceManager = PreferenceManager(getSharedPreferences(MY_PREFERENCE, Context.MODE_PRIVATE))
        number = intent?.getStringExtra(KEY_NUMBER)
        isSms = intent?.getBooleanExtra(KEY_IS_SMS, false)
        smsTextBody = intent?.getStringExtra(KEY_SMS_BODY)
        identifyNumber()
        return super.onStartCommand(intent, flags, startId)
    }

    // identify number in contact list
    @SuppressLint("MissingPermission", "HardwareIds")
    private fun identifyNumber() {
        val userNumber = preferenceManager?.getValue(PREF_USER_MOBILE,"")
        if (isSms!= null && isSms!!) {
            //sma filtering
            when {
                number != null && contactExists(this, number) -> {
                    Toast.makeText(this, "validNumber [$number]", Toast.LENGTH_SHORT).show()
                    callSmsReceiverListener?.validNumber(number)
                }
                number != null && !contactExists(this, number) && isInBlackList(number) -> {
                    Toast.makeText(this, "warning [$number]", Toast.LENGTH_SHORT).show()
                    callSmsReceiverListener?.warning()
                }
                number != null && !contactExists(this, number) && isInWhiteList(number) -> {
                    Toast.makeText(this, "validNumber [$number]", Toast.LENGTH_SHORT).show()
                    callSmsReceiverListener?.validNumber(number)
                }
                else -> {
                    apiCallForSMSFiltering(userNumber, number, smsTextBody)
                }
            }
        } else {
            //call filtering
            if (number != null && contactExists(this, number)) {
                Toast.makeText(this, "VALID number [$number]", Toast.LENGTH_LONG).show()
                callSmsReceiverListener?.detectedNumber(number)
            } else {
                apiCallForCallFiltering(userNumber, number)
            }
        }

    }

    private fun isInBlackList(number: String?): Boolean = mBlackList.contains(number.toString())

    private fun isInWhiteList(number: String?): Boolean = mWhiteList.contains(number.toString())

    private fun apiCallForSMSFiltering(userNumber: String?, receiverNumber: String?, smsText: String?) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val apiCall = APIClient(this@NumberDetectionService).getClient().smsFilterApi(SmsFilterRequest(userNumber, receiverNumber, smsText))
                if (apiCall.isSuccessful) {
                    Toast.makeText(this@NumberDetectionService, "ApI Success --> ${apiCall.body()?.reason}", Toast.LENGTH_LONG).show()
                    if (apiCall.body()?.warn != null && apiCall.body()?.warn!!) {
                        callSmsReceiverListener?.spam()
                        Toast.makeText(this@NumberDetectionService, "spam [$number]", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this@NumberDetectionService, "validNumber [$number]", Toast.LENGTH_LONG).show()
                        callSmsReceiverListener?.validNumber(number)
                    }
                } else {
                    Toast.makeText(this@NumberDetectionService, "ApI Failure --> ${apiCall.body()}", Toast.LENGTH_LONG).show()
                }
                stopService()
            } catch (e: Exception) {
                Toast.makeText(this@NumberDetectionService, e.message, Toast.LENGTH_LONG).show()
                stopService()
            }
        }
    }

    private fun apiCallForCallFiltering(userNumber: String?, receiverNumber: String?) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val apiCall = APIClient(this@NumberDetectionService).getClient().callFilterApi(CallFilterRequest(userNumber, receiverNumber))
                if (apiCall.isSuccessful) {
                    Toast.makeText(this@NumberDetectionService, "ApI Success --> ${apiCall.body()?.reason} ", Toast.LENGTH_LONG).show()
                    if (apiCall.body()?.warn != null && apiCall.body()?.warn!!) {
                        Toast.makeText(this@NumberDetectionService, "spam [$number]", Toast.LENGTH_LONG).show()
                        callSmsReceiverListener?.spam()
                    } else {
                        Toast.makeText(this@NumberDetectionService, "validNumber [$number]", Toast.LENGTH_LONG).show()
                        callSmsReceiverListener?.detectedNumber(number)
                    }
                } else {
                    Toast.makeText(this@NumberDetectionService, "ApI Failure --> ${apiCall.body()}", Toast.LENGTH_LONG).show()
                }
                stopService()
            } catch (e: Exception) {
                Toast.makeText(this@NumberDetectionService, e.message, Toast.LENGTH_LONG).show()
                stopService()
            }
        }
    }

    //stop the service.
    private fun stopService() {
        stopForeground(true)
        stopSelf()
    }

}