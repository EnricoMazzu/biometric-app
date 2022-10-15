package com.mzzlab.sample.biometric.data.error

import com.mzzlab.sample.biometric.data.crypto.ValidationResult

class InvalidCryptoLayerException(validationResult: ValidationResult) : Exception() {

    val isKeyPermanentlyInvalidated = validationResult == ValidationResult.KEY_PERMANENTLY_INVALIDATED

    val isKeyInitFailed = validationResult == ValidationResult.KEY_INIT_FAIL
}
