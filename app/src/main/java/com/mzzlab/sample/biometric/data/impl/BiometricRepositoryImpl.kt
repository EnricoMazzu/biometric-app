package com.mzzlab.sample.biometric.data.impl

import android.util.Base64
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricPrompt.CryptoObject
import com.mzzlab.sample.biometric.common.CryptoPurpose
import com.mzzlab.sample.biometric.data.crypto.CryptoEngine
import com.mzzlab.sample.biometric.data.crypto.ValidationResult
import com.mzzlab.sample.biometric.data.BiometricRepository
import com.mzzlab.sample.biometric.data.error.InvalidCryptoLayerException
import com.mzzlab.sample.biometric.data.model.BiometricAuthStatus
import com.mzzlab.sample.biometric.data.model.BiometricInfo
import com.mzzlab.sample.biometric.data.storage.KeyValueStorage
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

class BiometricRepositoryImpl(
    private val biometricManager: BiometricManager,
    private val requiredAuthenticators: Int = BIOMETRIC_STRONG,
    private val keyValueStorage: KeyValueStorage,
    private val cryptoEngine: CryptoEngine,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO

): BiometricRepository {

    private suspend fun checkInternalWithCrypto(
        biometricAuthStatus: BiometricAuthStatus = readBiometricAuthStatus()
    ): ValidationResult = withContext(dispatcher) {
        val validationResult = cryptoEngine.validate()
        when(validationResult){
            ValidationResult.KEY_PERMANENTLY_INVALIDATED,
            ValidationResult.KEY_INIT_FAIL -> {
                // this is a policy that we have decided to implement: you have to
                // notify this condition to the user
                clearCryptoAndData()
            }
            else -> {}
        }
        validationResult
    }

    override suspend fun getBiometricInfo(): BiometricInfo = withContext(dispatcher) {
        val biometricAuthStatus = readBiometricAuthStatus()
        val cryptoValidationResult = checkInternalWithCrypto(biometricAuthStatus)
        val isBiometricTokenPresent = isTokenPresent();
        BiometricInfo(
            biometricTokenPresent = isBiometricTokenPresent,
            biometricAuthStatus = biometricAuthStatus,
            keyStatus = when(cryptoValidationResult) {
                ValidationResult.OK -> BiometricInfo.KeyStatus.READY
                ValidationResult.KEY_INIT_FAIL,
                ValidationResult.VALIDATION_FAILED -> BiometricInfo.KeyStatus.NOT_READY
                ValidationResult.KEY_PERMANENTLY_INVALIDATED -> BiometricInfo.KeyStatus.INVALIDATED
            }
        )
    }

    private fun readBiometricAuthStatus() =
        when (biometricManager.canAuthenticate(requiredAuthenticators)) {
            BiometricManager.BIOMETRIC_SUCCESS -> BiometricAuthStatus.READY
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> BiometricAuthStatus.NOT_AVAILABLE
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> BiometricAuthStatus.TEMPORARY_NOT_AVAILABLE
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> BiometricAuthStatus.AVAILABLE_BUT_NOT_ENROLLED
            else -> BiometricAuthStatus.NOT_AVAILABLE
        }

    override suspend fun fetchAndStoreEncryptedToken(
        cryptoObject: CryptoObject
    )  = withContext(dispatcher) {
        validateCryptoLayer()
        // 1. fetch the token from our backend
        val token = getTokenFromBackend()
        // 2. encrypt the data using the cipher inside the cryptoObject
        val encryptedData = cryptoEngine.encrypt(token, cryptoObject)
        // 3. Store encrypted data and iv.
        storeDataAndIv(encryptedData.data, encryptedData.iv!!)
    }

    private suspend fun getTokenFromBackend(): String {
        // this is a mock generation
        val token = UUID.randomUUID().toString()
        keyValueStorage.storeValue(MOCK_TOKEN_KEY,token)
        return token
    }

    private fun storeDataAndIv(encryptedData: ByteArray, iv: ByteArray) {
        val dataBase64 = Base64.encodeToString(encryptedData, Base64.DEFAULT)
        val ivBase64 = Base64.encodeToString(iv, Base64.DEFAULT)
        keyValueStorage.storeValue(BIOMETRIC_TOKEN_KEY, dataBase64)
        keyValueStorage.storeValue(BIOMETRIC_IV_KEY, ivBase64)
    }

    override suspend fun decryptToken(cryptoObject: CryptoObject): String {
        validateCryptoLayer()
        // 1. read encrypted token (string base64 encoded)
        val encToken = keyValueStorage.getValue(BIOMETRIC_TOKEN_KEY)
        // 2. decode token data on byteArray
        val encTokenData = Base64.decode(encToken,Base64.DEFAULT)
        // 3. decrypt token via cryptoEngine (using cipher inside cryptoObject
        return cryptoEngine.decrypt(encTokenData, cryptoObject)
    }

    override suspend fun createCryptoObject(
        purpose: CryptoPurpose
    ): CryptoObject = withContext(dispatcher) {
        validateCryptoLayer()
        val iv: ByteArray? = when(purpose){
            CryptoPurpose.Decryption -> {
                Base64.decode(keyValueStorage.getValue(BIOMETRIC_IV_KEY), Base64.DEFAULT)
            }
            else -> null
        }
        cryptoEngine.createCryptoObject(purpose, iv)
    }

    private suspend fun isTokenPresent(): Boolean {
        return keyValueStorage.contains(key = BIOMETRIC_TOKEN_KEY) && keyValueStorage.contains(
            BIOMETRIC_IV_KEY)
    }

    override suspend fun clear() {
       keyValueStorage.clear()
    }

    private suspend fun validateCryptoLayer() {
        val status = checkInternalWithCrypto()
        if(status != ValidationResult.OK){
            throw InvalidCryptoLayerException(status)
        }
    }

    private fun clearCryptoAndData() {
        cryptoEngine.clear()
        keyValueStorage.clear()
    }

    companion object {
        const val BIOMETRIC_TOKEN_KEY = "BIOMETRIC_TOKEN"
        const val BIOMETRIC_IV_KEY = "BIOMETRIC_TOKEN_IV"
        const val MOCK_TOKEN_KEY = "mockTokenForFakeAuthValidation"
    }
}