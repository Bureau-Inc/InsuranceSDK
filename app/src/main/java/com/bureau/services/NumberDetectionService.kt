package com.bureau.services

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.telephony.TelephonyManager
import android.widget.Toast
import com.bureau.models.callFilter.request.CallFilterRequest
import com.bureau.network.APIClient
import com.bureau.utils.KEY_NUMBER
import com.bureau.utils.callSmsReceive
import com.bureau.utils.contactExists
import com.bureau.utils.isMyServiceRunning
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


/**
 * Created by Abhin.
 */

class NumberDetectionService : Service() {

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        identifyNumber(intent)
        return super.onStartCommand(intent, flags, startId)
    }

    // identify number in contact list
    @SuppressLint("MissingPermission", "HardwareIds")
    private fun identifyNumber(intent: Intent?) {
        val number = intent?.getStringExtra(KEY_NUMBER)
        if (number != null && contactExists(this, number)) {
            Toast.makeText(this, "VALID number --> $number", Toast.LENGTH_LONG).show()
            callSmsReceive?.detectedNumber(number)
            callSmsReceive?.aggravated()
        } else {
            // number is not contact list (check for number By API Call)
            // get user's mobile number
            val telephonyManager = this.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            var usersPhoneNumber = telephonyManager.line1Number
            if (usersPhoneNumber.isNullOrEmpty()){
                usersPhoneNumber = "12345"
            }
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    val apiCall = APIClient(this@NumberDetectionService).getClient().callFilterApi(CallFilterRequest(usersPhoneNumber, number))
                    if (apiCall.isSuccessful) {
                        Toast.makeText(this@NumberDetectionService, "ApI Success --> ${apiCall.body()?.warn} ", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this@NumberDetectionService, "ApI Failure --> ${apiCall.body()}", Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@NumberDetectionService, e.message, Toast.LENGTH_LONG).show()
                }
            }
        }
        stopService()
    }

    //stop the service.
    private fun stopService() {
        stopForeground(true)
        stopSelf()
    }

    // start the service if it is not already running.
    companion object {
        fun startService(context: Context, intent: Intent) {
            try {
                if (!isMyServiceRunning(context, NumberDetectionService::class.java
                    )
                ) {
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
}