package com.bureau.services

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import com.bureau.utils.*

/**
 * Created by Abhin.
 */

class NumberDetectionService : Service() {

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {
        identifyNumber(intent)
        return super.onStartCommand(
            intent,
            flags,
            startId
        )
    }

    private fun identifyNumber(intent: Intent?) {
        val number = intent?.getStringExtra(KEY_NUMBER)
        if (number!=null && contactExists(this,number)){
            Log.e("TAG","identifyNumber() --> yes")
            callSmsReceive?.detectedNumber(number)
            callSmsReceive?.aggravated()
        }else{
            //TODO: Call API
            Log.e("TAG","identifyNumber() --> not Identify Number")
            callSmsReceive?.spam()
        }
    }


    companion object {
        fun startService(
            context: Context,
            intent: Intent
        ) {
            try {
                if (!isMyServiceRunning(
                        context,
                        NumberDetectionService::class.java
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