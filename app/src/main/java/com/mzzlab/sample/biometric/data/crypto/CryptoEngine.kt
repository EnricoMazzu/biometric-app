package com.mzzlab.sample.biometric.data.crypto

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyPermanentlyInvalidatedException
import android.security.keystore.KeyProperties
import androidx.biometric.BiometricPrompt.CryptoObject
import com.mzzlab.sample.biometric.common.CryptoPurpose
import timber.log.Timber
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import kotlin.random.Random

class CryptoEngine {

    fun validate(): ValidationResult {
        return if(!isTargetKeyPresent()){
            generateKeyWithResult()
        }else{
            doWarmupWithResult()
        }
    }

    private fun doWarmupWithResult(): ValidationResult {
        return try {
            warmup()
            ValidationResult.OK
        } catch (ex: KeyPermanentlyInvalidatedException) {
            Timber.e("KeyPermanentlyInvalidatedException ")
            ValidationResult.KEY_PERMANENTLY_INVALIDATED
        } catch (ex: Exception) {
            Timber.e(ex, "warmup unknown error")
            ValidationResult.VALIDATION_FAILED
        }
    }

    private fun generateKeyWithResult(): ValidationResult {
        return try {
            generateTargetKey()
            ValidationResult.OK
        } catch (ex: Exception) {
            Timber.e(ex, "generateTargetKey fail")
            ValidationResult.KEY_INIT_FAIL
        }
    }


    private fun warmup() {
        val bytes = ByteArray(16)
        Random.nextBytes(bytes)
        createCryptoObject(CryptoPurpose.Decryption, bytes)
    }

    private fun isTargetKeyPresent(): Boolean{
        return getKeystore().isKeyEntry(TARGET_KEY_ALIAS)
    }

    private fun getKeystore(): KeyStore {
        return KeyStore.getInstance(ANDROID_KEYSTORE).apply {
            load(null)
        }
    }

    private fun generateKeyInternal(keyGenParameterSpec: KeyGenParameterSpec) {
        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE
        )
        keyGenerator.init(keyGenParameterSpec)
        keyGenerator.generateKey()
    }

    private fun generateTargetKey(){
        generateKeyInternal(
            KeyGenParameterSpec.Builder(
                //The alias (aka name) of the key
                TARGET_KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                 // this flag require that every key usage require a user authentication
                .setUserAuthenticationRequired(true)
                .setInvalidatedByBiometricEnrollment(true)
                .build())
    }

    private fun getCipher(): Cipher {
        return Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/"
                + KeyProperties.BLOCK_MODE_CBC + "/"
                + KeyProperties.ENCRYPTION_PADDING_PKCS7
        )
    }

    private fun getSecretKey(): SecretKey {
        return getKeystore().getKey(TARGET_KEY_ALIAS, null) as SecretKey
    }

    fun createCryptoObject(purpose: CryptoPurpose, iv:ByteArray?): CryptoObject {
        val cipher = getCipher()
        val secretKey = getSecretKey()
        if(purpose == CryptoPurpose.Decryption){
            cipher.init(Cipher.DECRYPT_MODE,secretKey, IvParameterSpec(iv))
        }else{
            cipher.init(Cipher.ENCRYPT_MODE,secretKey)
        }
        return CryptoObject(cipher)
    }

    fun encrypt(clearText: String, cryptoObject: CryptoObject): EncryptDataResult {
        val cipher = cryptoObject.cipher!!
        val tokenData = clearText.toByteArray(Charsets.UTF_8)
        val encryptedData = cipher.doFinal(tokenData)
        val iv = cipher.iv
        return EncryptDataResult(
            data = encryptedData,
            iv = iv
        )
    }

    fun decrypt(encryptedData: ByteArray, cryptoObject: CryptoObject): String {
        val cipher = cryptoObject.cipher!!
        val decryptedData = cipher.doFinal(encryptedData)
        return decryptedData.toString(Charsets.UTF_8)
    }

    private fun removeTargetKey() {
        if(isTargetKeyPresent()){
            getKeystore().deleteEntry(TARGET_KEY_ALIAS)
        }
    }

    fun clear() {
        removeTargetKey()
    }

    companion object {
        const val ANDROID_KEYSTORE = "AndroidKeyStore"
        const val TARGET_KEY_ALIAS = "DefEncDecKey"
    }
}