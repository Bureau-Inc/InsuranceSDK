package com.bureau

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bureau.services.NumberDetectionService
import com.bureau.utils.*

@Suppress("DEPRECATED_IDENTITY_EQUALS")
class MainActivity : AppCompatActivity() {

    private var marshMellowHelper: MarshMellowHelper? = null
    private var preferenceManager: PreferenceManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        preferenceManager = PreferenceManager(getSharedPreferences(MY_PREFERENCE, Context.MODE_PRIVATE))
        // Firstly, we check READ_CALL_LOG permission
        if (ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.READ_CALL_LOG) !== PackageManager.PERMISSION_GRANTED) {
            // We do not have this permission. Let's ask the user
            ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.READ_CALL_LOG), MY_PERMISSIONS_REQUEST_READ_CALL_LOG)
        }
        initRequestPermission()
        NumberDetectionService.init("12345", object : CallSmsReceiverInterface {
            override fun detectedNumber(number: String?) {
            }

            override fun spam() {
            }

            override fun aggravated() {
            }

            override fun warning() {
            }

            override fun validNumber(number: String?) {
            }
        })
    }


    private fun initRequestPermission() {
        marshMellowHelper = MarshMellowHelper(this, phoneCallPermission, PERMISSIONS_REQUEST_CODE)
        marshMellowHelper!!.request(object : MarshMellowHelper.PermissionCallback {
            override fun onPermissionGranted() {
            }

            override fun onPermissionDenied(permissionDeniedError: String) {

            }

            override fun onPermissionDeniedBySystem(permissionDeniedBySystem: String) {

            }
        })
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (marshMellowHelper != null) {
            marshMellowHelper!!.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_READ_CALL_LOG -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission granted!
                    // check READ_PHONE_STATE permission only when READ_CALL_LOG is granted
                    if (ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.READ_PHONE_STATE) !== PackageManager.PERMISSION_GRANTED) {
                        // We do not have this permission. Let's ask the user
                        ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.READ_PHONE_STATE), MY_PERMISSIONS_REQUEST_READ_PHONE_STATE)
                    }
                }
            }
            MY_PERMISSIONS_REQUEST_READ_PHONE_STATE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission granted!
                    // check PROCESS_OUTGOING_CALLS permission only when READ_PHONE_STATE is granted
                    if (ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.PROCESS_OUTGOING_CALLS) !== PackageManager.PERMISSION_GRANTED) {
                        // We do not have this permission. Let's ask the user
                        ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.PROCESS_OUTGOING_CALLS), MY_PERMISSIONS_REQUEST_PROCESS_OUTGOING_CALLS)
                    }
                }
            }
            MY_PERMISSIONS_REQUEST_PROCESS_OUTGOING_CALLS -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission granted!
                }
            }
        }
    }
}