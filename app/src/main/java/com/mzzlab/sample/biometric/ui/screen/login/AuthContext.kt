package com.mzzlab.sample.biometric.ui.screen.login

import androidx.biometric.BiometricPrompt
import com.mzzlab.sample.biometric.common.CryptoPurpose

data class AuthContext(
    val purpose: CryptoPurpose,
    val cryptoObject: BiometricPrompt.CryptoObject
)
