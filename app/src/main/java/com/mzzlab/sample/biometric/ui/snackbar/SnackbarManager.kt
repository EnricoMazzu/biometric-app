package com.mzzlab.sample.biometric.ui.snackbar

import androidx.annotation.StringRes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.*

/**
 * Jetsnack utility class with a some minor change
 */
object SnackbarManager {

    private val _messages: MutableStateFlow<List<UiMessage>> = MutableStateFlow(emptyList())
    val messages: StateFlow<List<UiMessage>> get() = _messages.asStateFlow()

    fun showMessage(@StringRes messageTextId: Int) {
        _messages.update { currentMessages ->
            currentMessages + UiMessage(
                id = UUID.randomUUID().mostSignificantBits,
                messageResId = messageTextId
            )
        }
    }

    fun showMessage(message: String) {
        _messages.update { currentMessages ->
            currentMessages + UiMessage(
                id = UUID.randomUUID().mostSignificantBits,
                messageText = message
            )
        }
    }

    fun setMessageShown(messageId: Long) {
        _messages.update { currentMessages ->
            currentMessages.filterNot { it.id == messageId }
        }
    }
}