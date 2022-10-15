package com.mzzlab.sample.biometric.data.model

enum class BiometricAuthStatus {
    /**
     *  We can interact with the biometric support
     */
    READY,

    /**
     * Biometry support not present
     */
    NOT_AVAILABLE,

    /**
     * Biometric support is currently unavailable
     */
    TEMPORARY_NOT_AVAILABLE,

    /**
     * Biometric support is available, but no biometry has enrolled
     */
    AVAILABLE_BUT_NOT_ENROLLED,
}