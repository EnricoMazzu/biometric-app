package com.mzzlab.sample.biometric.common

import android.content.Context
import android.content.ContextWrapper
import androidx.fragment.app.FragmentActivity

fun Context.findActivity(): FragmentActivity? {
    var currentContext = this
    var previousContext: Context? = null
    while (currentContext is ContextWrapper && previousContext != currentContext) {
        if (currentContext is FragmentActivity) {
            return currentContext
        }
        previousContext = currentContext
        currentContext = currentContext.baseContext
    }
    return null
}