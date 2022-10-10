package com.mzzlab.sample.biometric.ui.screen.login

data class LoginUIState (
    // username and password field values
    val usernameField: String = "",
    val passwordField: String = "",
    // true when we want to render the access with biometry button
    val canLoginWithBiometry: Boolean = false,
    // indicate that the user is logged in
    val loggedIn: Boolean = false,
    // indicate that we want to ask to the user an authentication
    val askBiometricEnrollment: Boolean = false,
    // represent the Authentication context. This is used to verify
    val authContext: AuthContext? = null,
)

