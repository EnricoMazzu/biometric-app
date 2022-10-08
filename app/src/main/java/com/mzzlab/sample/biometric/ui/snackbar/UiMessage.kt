package com.mzzlab.sample.biometric.ui.snackbar

import androidx.annotation.StringRes

data class UiMessage(
    val id: Long,
    @StringRes val messageResId: Int? = null,
    val messageText: String? = null
)
