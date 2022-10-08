package com.mzzlab.sample.biometric.common

import androidx.biometric.BiometricPrompt

data class AuthContext(
    val purpose: CryptoPurpose,
    val cryptoObject: BiometricPrompt.CryptoObject
)
