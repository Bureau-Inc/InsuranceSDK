package com.bureau.services

import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.telecom.Call
import android.telecom.CallScreeningService
import android.util.Log
import androidx.annotation.RequiresApi
import com.bureau.utils.startNumberDetectionService

@RequiresApi(Build.VERSION_CODES.N)
class ScreeningService : CallScreeningService() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return super.onBind(intent)
    }
    override fun onScreenCall(callDetails: Call.Details) {
        Log.e("TAG","onScreenCall() --> ")
        val phoneNumber = callDetails.handle.schemeSpecificPart
        Log.e("TAG","onScreenCall() phoneNumber--> $phoneNumber")
        startNumberDetectionService(this, phoneNumber)
    }
}