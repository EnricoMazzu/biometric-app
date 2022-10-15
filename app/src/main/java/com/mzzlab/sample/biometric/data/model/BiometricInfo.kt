package com.mzzlab.sample.biometric.data.model

data class BiometricInfo(
    /**
     * True if our biometric token is present, false otherwise
     */
    val biometricTokenPresent: Boolean = false,

    /**
     * Give us information about the status of our biometric authentication
     */
    val biometricAuthStatus: BiometricAuthStatus,

    /**
     * Give us the status of our cryptographic key
     */
    val keyStatus: KeyStatus
){
    enum class KeyStatus {
        /**
         * Key is not ready to use
         */
        NOT_READY,

        /**
         * Key is ready
         */
        READY,

        /**
         * Key is present but permanently invalidate
         */
        INVALIDATED
    }

    fun canAskAuthentication() = (biometricAuthStatus == BiometricAuthStatus.READY
            && keyStatus == KeyStatus.READY)

}
