package com.ylabz.basepro.core.util

import android.util.Log

object Logging {

    // Global tag for filtering all app logs
    private const val GLOBAL_TAG = "BasePro"

    // Log level control: Change to Log.ERROR for production
    private const val LOG_LEVEL = Log.DEBUG

    // Function to decide whether to log based on the log level
    private fun shouldLog(level: Int): Boolean = level >= LOG_LEVEL

    // Generic logging functions for global tag
    fun d(message: String) {
        if (shouldLog(Log.DEBUG)) Log.d(GLOBAL_TAG, message)
    }

    fun e(message: String, throwable: Throwable? = null) {
        if (shouldLog(Log.ERROR)) Log.e(GLOBAL_TAG, message, throwable)
    }

    fun i(message: String) {
        if (shouldLog(Log.INFO)) Log.i(GLOBAL_TAG, message)
    }

    fun w(message: String) {
        if (shouldLog(Log.WARN)) Log.w(GLOBAL_TAG, message)
    }

    // Functions with a local tag (e.g., per class or feature)
    fun d(tag: String, message: String) {
        if (shouldLog(Log.DEBUG)) Log.d("$GLOBAL_TAG-$tag", message)
    }

    fun e(tag: String, message: String, throwable: Throwable? = null) {
        if (shouldLog(Log.ERROR)) Log.e("$GLOBAL_TAG-$tag", message, throwable)
    }

    fun i(tag: String, message: String) {
        if (shouldLog(Log.INFO)) Log.i("$GLOBAL_TAG-$tag", message)
    }

    fun w(tag: String, message: String) {
        if (shouldLog(Log.WARN)) Log.w("$GLOBAL_TAG-$tag", message)
    }

    // Helper to create a tag from class name
    fun getTag(clazz: Class<*>): String = clazz.simpleName
}
