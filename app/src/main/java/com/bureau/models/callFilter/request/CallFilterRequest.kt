package com.bureau.models.callFilter.request

data class CallFilterRequest(
    var call_reciever: String? = null,
    var call_sender: String? = null
)