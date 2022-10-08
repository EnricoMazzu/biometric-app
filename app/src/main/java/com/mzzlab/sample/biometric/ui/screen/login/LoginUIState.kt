package com.mzzlab.sample.biometric.ui.screen.login

import com.mzzlab.sample.biometric.common.AuthContext

data class LoginUIState (
    val usernameField: String = "",
    val passwordField: String = "",
    val loggedIn: Boolean = false,
    val askBiometricEnrollment: Boolean = false,
    val canLoginWithBiometry: Boolean = false,
    val authContext: AuthContext? = null,
)

