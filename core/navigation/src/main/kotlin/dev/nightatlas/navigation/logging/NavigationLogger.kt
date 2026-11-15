package dev.nightatlas.navigation.logging

import android.util.Log

internal object NavigationLogger {
    fun d(tag: String, message: String) {
        try {
            Log.d(tag, message)
        } catch (_: RuntimeException) {
            // Android Log is not available in plain JVM unit tests.
        }
    }

    fun w(tag: String, message: String) {
        try {
            Log.w(tag, message)
        } catch (_: RuntimeException) {
            // Android Log is not available in plain JVM unit tests.
        }
    }

    fun e(tag: String, message: String, throwable: Throwable? = null) {
        try {
            if (throwable == null) {
                Log.e(tag, message)
            } else {
                Log.e(tag, message, throwable)
            }
        } catch (_: RuntimeException) {
            // Android Log is not available in plain JVM unit tests.
        }
    }
}
