package com.nexis.aybike.util

interface NotifyMessage {
    fun onSuccess(message: String)
    fun onError(message: String?)
}