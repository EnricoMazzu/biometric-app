package com.mzzlab.sample.biometric.data

import androidx.biometric.BiometricPrompt.CryptoObject
import com.mzzlab.sample.biometric.common.CryptoPurpose
import com.mzzlab.sample.biometric.data.model.BiometricInfo

/**
 * Represent the repository for our biometric related data / info
 */
interface BiometricRepository {

    /**
     * Read the biometric info that contains the biometric authentication
     * state, the underling key status and a flat to inform when the token is
     * already present
     *
     * @return the biometric info object
     */
    suspend fun getBiometricInfo(): BiometricInfo

    /**
     * Store the token using the [cryptoObject] passed as parameter.
     *
     * @param cryptoObject the cryptoObject to use for encryption operations
     * @throws com.mzzlab.sample.biometric.data.error.InvalidCryptoLayerException if
     * crypto layer is invalid
     */
    suspend fun fetchAndStoreEncryptedToken(cryptoObject: CryptoObject)

    /**
     * Decrypt the token using the [cryptoObject] passed as parameter
     *
     * @param cryptoObject the cryptoObject to use for decryption operations
     * @return the token as string
     * @throws com.mzzlab.sample.biometric.data.error.InvalidCryptoLayerException if
     * crypto layer is invalid
     */
    suspend fun decryptToken(cryptoObject: CryptoObject): String

    /**
     * Create a new [CryptoObject] instance for the specified purpose
     *
     * @param purpose the final purpose of the required cryptoObject
     * @throws com.mzzlab.sample.biometric.data.error.InvalidCryptoLayerException if
     * crypto layer is invalid
     */
    suspend fun createCryptoObject(purpose: CryptoPurpose): CryptoObject

    /**
     * Clear the stored information
     */
    suspend fun clear()
}