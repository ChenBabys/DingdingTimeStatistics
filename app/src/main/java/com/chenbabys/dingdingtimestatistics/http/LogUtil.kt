package com.chenbabys.dingdingtimestatistics.http

import android.util.Log

object LogUtil {
    private const val TAG = "ctms_log"
    private const val TAG_NET = "ctms_net"

    fun i(message: String?) {
         Log.i(TAG, message.toString())
    }

    fun e(message: String?) {
         Log.e(TAG, message.toString())
    }

    fun showHttpHeaderLog(message: String?) {
         Log.d(TAG_NET, message.toString())
    }

    fun showHttpApiLog(message: String?) {
         Log.w(TAG_NET, message.toString())
    }

    fun showHttpLog(message: String?) {
         Log.i(TAG_NET, message.toString())
    }
}