package com.mzzlab.sample.biometric.data.model

data class BiometricInfo(
    val biometricTokenPresent: Boolean = false,
    val biometricAuthStatus: BiometricAuthStatus,
    val keyStatus: KeyStatus
){
    enum class KeyStatus {
        NOT_READY,
        READY,
        INVALIDATED
    }

    fun canAskAuthentication() = (biometricAuthStatus == BiometricAuthStatus.READY
            && keyStatus == KeyStatus.READY)

}
