package com.bureau.models.packageDetectorHelper

data class AllInstalledAppResponse(
    var package_name: String? = null,
    var reason: String? = null,
    var warn: Boolean? = null
)