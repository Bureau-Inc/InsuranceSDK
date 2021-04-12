package com.bureau

import android.Manifest
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bureau.receivers.call.CallReceiver
import com.bureau.utils.*

class MainActivity : AppCompatActivity() {

    private var marshMellowHelper: MarshMellowHelper? = null
    private val PERMISSIONS_REQUEST_CODE = 10003
    var callReceiver: CallReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        /*if (!hasPermissions(this,phoneCallPermission)){
            initRequestPermission()
        } else {
            if (callReceiver == null) {
                callReceiver = CallReceiver()
            }
            val intentFilter = IntentFilter()
            intentFilter.addAction("android.intent.action.PHONE_STATE")
            intentFilter.addAction("android.intent.action.NEW_OUTGOING_CALL")
            registerReceiver(callReceiver, intentFilter)
        }*/

        // Firstly, we check READ_CALL_LOG permission
        if (ContextCompat.checkSelfPermission(
                this@MainActivity,
                Manifest.permission.READ_CALL_LOG
            ) !== PackageManager.PERMISSION_GRANTED
        ) {
            // We do not have this permission. Let's ask the user
            ActivityCompat.requestPermissions(
                this@MainActivity,
                arrayOf(Manifest.permission.READ_CALL_LOG),
                MY_PERMISSIONS_REQUEST_READ_CALL_LOG
            )
        }

        // dynamically register CallReceiver

        // dynamically register CallReceiver
        if (callReceiver == null) {
            callReceiver = CallReceiver()
        }
        val intentFilter = IntentFilter()
        intentFilter.addAction("android.intent.action.PHONE_STATE")
        intentFilter.addAction("android.intent.action.NEW_OUTGOING_CALL")
        registerReceiver(callReceiver, intentFilter)
    }


    private fun initRequestPermission() {
        Log.e("TAG","initRequestPermission() --> ")
        marshMellowHelper=MarshMellowHelper(this,phoneCallPermission,PERMISSIONS_REQUEST_CODE)
        marshMellowHelper!!.request(object : MarshMellowHelper.PermissionCallback {
            override fun onPermissionGranted() {
                //code here
                Log.e("TAG","onPermissionGranted() --> ")
                // dynamically register CallReceiver
                if (callReceiver == null) {
                    callReceiver = CallReceiver()
                }
                val intentFilter = IntentFilter()
                intentFilter.addAction("android.intent.action.PHONE_STATE")
                intentFilter.addAction("android.intent.action.NEW_OUTGOING_CALL")
                registerReceiver(callReceiver, intentFilter)
                /*callSmsReceive = object : CallSmsReceiverInterface {
                    override fun detectedNumber(number: String?) {
                        Log.e("TAG", "detectedNumber() number--> $number")
                    }

                    override fun spam() {
                        Log.e("TAG", "spam() --> ")
                    }

                    override fun aggravated() {
                        Log.e("TAG", "aggravated() --> ")
                    }
                }*/
            }

            override fun onPermissionDenied(permissionDeniedError: String) {

            }

            override fun onPermissionDeniedBySystem(permissionDeniedBySystem: String) {

            }
        })
    }

    /*override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (marshMellowHelper != null) {
            marshMellowHelper!!.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }*/

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_READ_CALL_LOG -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission granted!
                    Log.d("###", "READ_CALL_LOG granted!")
                    // check READ_PHONE_STATE permission only when READ_CALL_LOG is granted
                    if (ContextCompat.checkSelfPermission(
                            this@MainActivity,
                            Manifest.permission.READ_PHONE_STATE
                        ) !== PackageManager.PERMISSION_GRANTED
                    ) {
                        // We do not have this permission. Let's ask the user
                        ActivityCompat.requestPermissions(
                            this@MainActivity,
                            arrayOf(Manifest.permission.READ_PHONE_STATE),
                            MY_PERMISSIONS_REQUEST_READ_PHONE_STATE
                        )
                    }
                } else {
                    // permission denied or has been cancelled
                    Log.d("###", "READ_CALL_LOG denied!")
                    Toast.makeText(
                        applicationContext,
                        "missing READ_CALL_LOG",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
            MY_PERMISSIONS_REQUEST_READ_PHONE_STATE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission granted!
                    Log.d("###", "READ_PHONE_STATE granted!")
                    // check PROCESS_OUTGOING_CALLS permission only when READ_PHONE_STATE is granted
                    if (ContextCompat.checkSelfPermission(
                            this@MainActivity,
                            Manifest.permission.PROCESS_OUTGOING_CALLS
                        ) !== PackageManager.PERMISSION_GRANTED
                    ) {
                        // We do not have this permission. Let's ask the user
                        ActivityCompat.requestPermissions(
                            this@MainActivity,
                            arrayOf(Manifest.permission.PROCESS_OUTGOING_CALLS),
                            MY_PERMISSIONS_REQUEST_PROCESS_OUTGOING_CALLS
                        )
                    }
                } else {
                    // permission denied or has been cancelled
                    Log.d("###", "READ_PHONE_STATE denied!")
                    Toast.makeText(
                        applicationContext,
                        "missing READ_PHONE_STATE",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
            MY_PERMISSIONS_REQUEST_PROCESS_OUTGOING_CALLS -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission granted!
                    Log.d("###", "PROCESS_OUTGOING_CALLS granted!")
                } else {
                    // permission denied or has been cancelled
                    Log.d("###", "PROCESS_OUTGOING_CALLS denied!")
                    Toast.makeText(
                        applicationContext,
                        "missing PROCESS_OUTGOING_CALLS",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}