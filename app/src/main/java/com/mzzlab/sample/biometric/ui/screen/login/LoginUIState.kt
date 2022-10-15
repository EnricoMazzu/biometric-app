package com.mzzlab.sample.biometric.ui.screen.login

data class LoginUIState (
    val usernameField: String = "",
    val passwordField: String = "",

    /**
     * True when we want to render the "access with biometry" button
     */
    val canLoginWithBiometry: Boolean = false,

    /**
     * True when the user is logged in, false otherwise
     */
    val loggedIn: Boolean = false,

    /**
     * indicate that we should to show the biometric prompt to the user to enroll
     * the biometric token
     */
    val askBiometricEnrollment: Boolean = false,

    /**
     * Represent the Authentication context of our prompt
     */
    val authContext: AuthContext? = null,
)

